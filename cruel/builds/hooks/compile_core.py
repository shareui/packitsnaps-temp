import hashlib
import json
import os
import shutil
import struct
import subprocess
from pathlib import Path

NAMESPACE = 'compile_core'
CORE_SRC_REL = Path('PackIt') / 'src' / 'kotlin' / 'sh' / 'packit' / 'core'
STUBS_SRC_REL = Path('PackIt') / 'src' / 'kotlin' / 'stubs'
ASSETS_DEX_REL = Path('PackIt') / 'res' / 'assets' / 'Core.dex'
CACHE_REL = Path('cruel') / 'local' / 'cache' / 'kotlin'

def _find_android_sdk():
    for sdk_env in ('ANDROID_HOME', 'ANDROID_SDK_ROOT'):
        sdk = os.environ.get(sdk_env)
        if sdk and Path(sdk).is_dir():
            return sdk
    home = Path.home()
    for cand in (
        home / 'Android' / 'Sdk',
        home / 'Library' / 'Android' / 'sdk',
        Path('/usr/lib/android-sdk'),
        Path('/opt/android-sdk'),
    ):
        if cand.is_dir():
            return str(cand)
    return None

_CACHED_DEPS = None

def _is_valid_deps(deps: dict) -> bool:
    if not isinstance(deps, dict):
        return False
    required_files = ('d8_jar', 'android_jar', 'kotlinc', 'javac')
    for key in required_files:
        val = deps.get(key)
        if not val or not Path(val).is_file():
            return False
    sdk = deps.get('android_sdk')
    if not sdk or not Path(sdk).is_dir():
        return False
    return True

def _load_cached_toolchain(cache_dir: Path):
    toolchain_file = cache_dir / 'toolchain.json'
    if not toolchain_file.is_file():
        return None
    try:
        data = json.loads(toolchain_file.read_text(encoding='utf-8'))
        if _is_valid_deps(data):
            return data
    except Exception:
        pass
    return None

def _save_cached_toolchain(cache_dir: Path, deps: dict):
    try:
        cache_dir.mkdir(parents=True, exist_ok=True)
        toolchain_file = cache_dir / 'toolchain.json'
        toolchain_file.write_text(json.dumps(deps, indent=2), encoding='utf-8')
    except Exception:
        pass

def check_kotlin_deps(cache_dir: Path, buildlog):
    global _CACHED_DEPS
    if _CACHED_DEPS is not None and _is_valid_deps(_CACHED_DEPS):
        return _CACHED_DEPS

    cached = _load_cached_toolchain(cache_dir)
    if cached is not None:
        _CACHED_DEPS = cached
        return cached

    # only if not found in cache or path invalid on disk, search from scratch
    android_sdk = _find_android_sdk()
    if android_sdk is None:
        buildlog.error('android SDK not found (set ANDROID_HOME)')
        return None
    build_tools_dir = Path(android_sdk) / 'build-tools'
    if not build_tools_dir.is_dir():
        buildlog.error(f'no build-tools under {android_sdk}')
        return None
    build_tools = sorted(build_tools_dir.glob('*'))
    if not build_tools:
        buildlog.error(f'no build-tools installed under {build_tools_dir}')
        return None
    d8_jar = build_tools[-1] / 'lib' / 'd8.jar'
    if not d8_jar.is_file():
        buildlog.error(f'd8.jar not found at {d8_jar}')
        return None
    platforms = sorted(Path(android_sdk, 'platforms').glob('android-*')) if Path(android_sdk, 'platforms').is_dir() else []
    if not platforms:
        buildlog.error(f'no android platforms installed under {android_sdk}/platforms')
        return None
    android_jar = platforms[-1] / 'android.jar'
    if not android_jar.is_file():
        buildlog.error(f'android.jar not found at {android_jar}')
        return None
    kotlinc = shutil.which('kotlinc')
    if kotlinc is None:
        buildlog.error('kotlinc not found on PATH')
        return None
    javac = shutil.which('javac')
    if javac is None:
        buildlog.error('javac not found on PATH')
        return None
    deps = {
        'android_sdk': android_sdk,
        'd8_jar': str(d8_jar),
        'android_jar': str(android_jar),
        'kotlinc': kotlinc,
        'javac': javac,
    }
    _save_cached_toolchain(cache_dir, deps)
    _CACHED_DEPS = deps
    return deps

def _hash_file(path: Path) -> str:
    h = hashlib.sha256()
    h.update(path.read_bytes())
    return h.hexdigest()[:16]

def _hash_dir(root: Path, pattern: str = '*') -> str:
    files = sorted(p for p in root.rglob(pattern) if p.is_file())
    parts = [f'{p.relative_to(root).as_posix()}:{_hash_file(p)}' for p in files]
    return hashlib.sha256('|'.join(parts).encode('utf-8')).hexdigest()[:16]

def read_dex_classes(dex_path: Path) -> tuple[list[str], list[str]]:
    try:
        data = Path(dex_path).read_bytes()
        if len(data) < 0x70 or data[:4] != b'dex\n':
            return [], []
        string_ids_size, string_ids_off = struct.unpack_from('<II', data, 0x38)
        type_ids_size, type_ids_off = struct.unpack_from('<II', data, 0x40)
        class_defs_size, class_defs_off = struct.unpack_from('<II', data, 0x60)
        string_offs = [struct.unpack_from('<I', data, string_ids_off + i * 4)[0] for i in range(string_ids_size)]
        def get_string(idx: int) -> str:
            off = string_offs[idx]
            while data[off] & 0x80:
                off += 1
            off += 1
            end = data.find(b'\x00', off)
            return data[off:end].decode('utf-8', errors='replace')
        type_strings = [struct.unpack_from('<I', data, type_ids_off + i * 4)[0] for i in range(type_ids_size)]
        all_classes = []
        for i in range(class_defs_size):
            class_idx = struct.unpack_from('<I', data, class_defs_off + i * 32)[0]
            desc = get_string(type_strings[class_idx])
            if desc.startswith('L') and desc.endswith(';'):
                desc = desc[1:-1].replace('/', '.')
            all_classes.append(desc)
        all_classes.sort()
        primary_classes = [c for c in all_classes if 'ExternalSynthetic' not in c and not c.rsplit('$', 1)[-1].isdigit()]
        return primary_classes, all_classes
    except Exception:
        return [], []

def clean_core_dex(project_root, buildlog=None):
    root = Path(project_root).resolve()
    target_dex = root / ASSETS_DEX_REL
    if target_dex.is_file():
        try:
            target_dex.unlink()
            if buildlog:
                buildlog.info('cleaned temporary Core.dex from assets')
        except Exception as e:
            if buildlog:
                buildlog.warn(f'failed to clean Core.dex: {e}')

def build_core_dex(project_root, buildlog, cache=None, cruel_bin=None) -> bool:
    root = Path(project_root).resolve()
    core_src = root / CORE_SRC_REL
    stubs_src = root / STUBS_SRC_REL
    target_dex = root / ASSETS_DEX_REL
    cache_dir = root / CACHE_REL

    if not core_src.is_dir():
        buildlog.error(f'core source directory not found at {core_src}')
        return False

    deps = check_kotlin_deps(cache_dir, buildlog)
    if deps is None:
        return False

    # calculate digests
    core_digest = _hash_dir(core_src, '*.kt')
    stubs_java_digest = _hash_dir(stubs_src, '*.java')
    stubs_kt_files = sorted(p for p in stubs_src.rglob('*.kt') if 'composeshell' not in p.parts)
    stubs_kt_digest = hashlib.sha256('|'.join(f'{p.relative_to(stubs_src).as_posix()}:{_hash_file(p)}' for p in stubs_kt_files).encode('utf-8')).hexdigest()[:16]
    stubs_digest = f'{stubs_java_digest}::{stubs_kt_digest}'
    total_digest = f'{core_digest}::{stubs_digest}'

    manifest_path = cache_dir / 'manifest.json'
    cached_dex = cache_dir / 'Core.dex'

    manifest = {}
    if manifest_path.is_file():
        try:
            manifest = json.loads(manifest_path.read_text(encoding='utf-8'))
        except Exception:
            manifest = {}

    if cached_dex.is_file() and manifest.get('digest') == total_digest:
        buildlog.info('  = Core.dex (cached)')
        target_dex.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(cached_dex, target_dex)
        return True

    try:
        with buildlog.task(NAMESPACE, 'compile_core') as t:
            buildlog.info('building Core.dex')
            temp_build_dir = root / 'cruel' / 'local' / 'temp' / 'kotlin-core-build'
            if temp_build_dir.exists():
                shutil.rmtree(temp_build_dir)
            temp_build_dir.mkdir(parents=True, exist_ok=True)

            stubs_classes_dir = cache_dir / 'stubs'
            classes_dir = temp_build_dir / 'classes'
            dex_classes_dir = cache_dir / 'dex'
            merge_dir = temp_build_dir / 'merged'

            classes_dir.mkdir(parents=True, exist_ok=True)
            dex_classes_dir.mkdir(parents=True, exist_ok=True)
            merge_dir.mkdir(parents=True, exist_ok=True)

            # 1. compile stubs with javac and kotlinc if changed
            stubs_cached_digest = manifest.get('stubs_digest')
            if not stubs_classes_dir.is_dir() or stubs_cached_digest != stubs_digest:
                if stubs_classes_dir.exists():
                    shutil.rmtree(stubs_classes_dir)
                stubs_classes_dir.mkdir(parents=True, exist_ok=True)
                stub_sources = [str(p) for p in stubs_src.rglob('*.java')]
                if stub_sources:
                    buildlog.info(f'  compiling {len(stub_sources)} stub files with javac')
                    javac_cmd = [deps['javac'], '-cp', deps['android_jar'], '-d', str(stubs_classes_dir), *stub_sources]
                    res = subprocess.run(javac_cmd, capture_output=True, text=True)
                    if res.returncode != 0:
                        buildlog.error(f'javac stubs compilation failed: {res.stderr.strip()}')
                        return False
                if stubs_kt_files:
                    buildlog.info(f'  compiling {len(stubs_kt_files)} stub kotlin files with kotlinc')
                    kotlinc_stub_cmd = [
                        deps['kotlinc'],
                        '-jvm-target', '1.8',
                        '-classpath', f"{deps['android_jar']}:{stubs_classes_dir}",
                        '-d', str(stubs_classes_dir),
                        *(str(p) for p in stubs_kt_files),
                    ]
                    res = subprocess.run(kotlinc_stub_cmd, capture_output=True, text=True)
                    if res.returncode != 0:
                        buildlog.error(f'kotlinc stubs compilation failed: {res.stderr.strip()}')
                        return False
            t.progress(25)

            # 2. compile core kotlin sources with kotlinc
            kt_sources = sorted(core_src.rglob('*.kt'))
            buildlog.info(f'  compiling {len(kt_sources)} kotlin files with kotlinc:')
            for p in kt_sources:
                buildlog.info(f'    * {p.relative_to(core_src).as_posix()}')
            classpath = f"{deps['android_jar']}:{stubs_classes_dir}"
            kotlinc_cmd = [
                deps['kotlinc'],
                '-jvm-target', '1.8',
                '-classpath', classpath,
                '-d', str(classes_dir),
                *(str(p) for p in kt_sources),
            ]
            res = subprocess.run(kotlinc_cmd, capture_output=True, text=True)
            if res.returncode != 0:
                buildlog.error(f'kotlinc failed:\n{res.stderr.strip() or res.stdout.strip()}')
                return False
            t.progress(50)

            # 3. compile class files to intermediate dex files
            class_files = sorted(classes_dir.rglob('*.class'))
            if not class_files:
                buildlog.error('no .class files emitted by kotlinc')
                return False

            class_hashes = manifest.get('class_hashes', {})
            updated_class_hashes = {}
            intermediate_dex_files = []

            for cf in class_files:
                rel = cf.relative_to(classes_dir).as_posix()
                chash = _hash_file(cf)
                updated_class_hashes[rel] = chash
                dex_file_name = rel.replace('/', '_').replace('.class', '.dex')
                out_dex_file = dex_classes_dir / dex_file_name

                # check intermediate dex cache
                if not out_dex_file.is_file() or class_hashes.get(rel) != chash:
                    d8_cmd = [
                        'java', '-cp', deps['d8_jar'], 'com.android.tools.r8.D8',
                        '--intermediate',
                        '--min-api', '26',
                        '--lib', deps['android_jar'],
                        '--output', str(temp_build_dir),
                        str(cf),
                    ]
                    res = subprocess.run(d8_cmd, capture_output=True, text=True)
                    if res.returncode != 0:
                        buildlog.error(f'd8 intermediate compilation failed for {rel}: {res.stderr.strip()}')
                        return False
                    gen_dex = temp_build_dir / 'classes.dex'
                    if gen_dex.is_file():
                        shutil.move(str(gen_dex), str(out_dex_file))
                intermediate_dex_files.append(out_dex_file)
            t.progress(75)

            # 4. merge intermediate dex files with release d8
            buildlog.info(f'  merging {len(intermediate_dex_files)} dex files with release D8')
            merge_cmd = [
                'java', '-cp', deps['d8_jar'], 'com.android.tools.r8.D8',
                '--release',
                '--min-api', '26',
                '--lib', deps['android_jar'],
                '--output', str(merge_dir),
                *(str(p) for p in intermediate_dex_files),
            ]
            res = subprocess.run(merge_cmd, capture_output=True, text=True)
            if res.returncode != 0:
                buildlog.error(f'd8 merge failed: {res.stderr.strip()}')
                return False

            final_dex = merge_dir / 'classes.dex'
            if not final_dex.is_file():
                buildlog.error('D8 release merge produced no classes.dex')
                return False

            # save to cache and output
            cache_dir.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(final_dex, cached_dex)
            target_dex.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(cached_dex, target_dex)

            # save manifest
            manifest['digest'] = total_digest
            manifest['stubs_digest'] = stubs_digest
            manifest['class_hashes'] = updated_class_hashes
            manifest_path.write_text(json.dumps(manifest, indent=2), encoding='utf-8')

            size_kb = target_dex.stat().st_size // 1024
            buildlog.info(f'built Core.dex ({size_kb} KB)')
            primary_classes, _ = read_dex_classes(target_dex)
            buildlog.info(f'  classes in Core.dex ({len(primary_classes)}):')
            for cls in primary_classes:
                buildlog.info(f'    * {cls}')
            t.progress(100)
            return True

    except Exception as e:
        buildlog.error(f'compile_core error: {e}')
        clean_core_dex(project_root, buildlog)
        return False
