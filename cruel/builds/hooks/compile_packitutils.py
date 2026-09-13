import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

try:
    import tomllib
except ImportError:
    import tomli as tomllib

nameSpace = 'compile_packitutils'
wheelSrcRel = Path('PackIt') / 'src' / 'wheels' / 'packutil'
pyprojectRel = wheelSrcRel / 'pyproject.toml'
cacheRel = Path('cruel') / 'local' / 'cache' / 'wheels'
tempBuildRel = Path('cruel') / 'local' / 'temp' / 'wheel-build'
stateFileRel = Path('cruel') / 'local' / 'temp' / 'packutil_state.json'
artifactDirs = {'build', 'dist', '__pycache__'}

def isArtifact(relPath: Path) -> bool:
    for part in relPath.parts:
        if part in artifactDirs or part.endswith('.egg-info'):
            return True
    return False

def hashFile(filePath: Path) -> str:
    h = hashlib.sha256()
    h.update(filePath.read_bytes())
    return h.hexdigest()[:16]

def hashSourceTree(srcDir: Path) -> str:
    if not srcDir.is_dir():
        return ''
    files = sorted(
        p for p in srcDir.rglob('*')
        if p.is_file() and not isArtifact(p.relative_to(srcDir))
    )
    parts = [f'{p.relative_to(srcDir).as_posix()}:{hashFile(p)}' for p in files]
    return hashlib.sha256('|'.join(parts).encode('utf-8')).hexdigest()[:16]

def cleanSourceArtifacts(srcDir: Path) -> None:
    if not srcDir.is_dir():
        return
    for entry in srcDir.iterdir():
        if entry.is_dir() and (entry.name in artifactDirs or entry.name.endswith('.egg-info')):
            shutil.rmtree(entry)

def readPyprojectMeta(pyprojectPath: Path) -> tuple[str, str]:
    if not pyprojectPath.is_file():
        raise FileNotFoundError(f'{pyprojectPath} not found')
    with open(pyprojectPath, 'rb') as f:
        data = tomllib.load(f)
    project = data.get('project', {})
    name = project.get('name')
    version = project.get('version')
    if not name or not version:
        raise ValueError(f'name or version missing in {pyprojectPath}')
    return str(name), str(version)

def buildWheel(srcDir: Path, outDir: Path, buildLog) -> Path:
    if outDir.exists():
        shutil.rmtree(outDir)
    outDir.mkdir(parents=True, exist_ok=True)
    pythonBin = sys.executable or shutil.which('python3.11') or shutil.which('python3')
    cmd = [pythonBin, '-m', 'build', '--wheel', '--no-isolation', '--outdir', str(outDir), str(srcDir)]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        buildLog.error(f'wheel build failed: {res.stderr.strip()}')
        raise RuntimeError(f'wheel build failed: {res.stderr.strip()}')
    builtWheels = list(outDir.glob('*.whl'))
    if not builtWheels:
        buildLog.error('no .whl file produced by build')
        raise RuntimeError('no .whl file produced by build')
    cleanSourceArtifacts(srcDir)
    return builtWheels[0]

def updateCruelConfig(cfgPath: Path, projectRoot: Path, targetRelPath: str) -> None:
    content = cfgPath.read_text(encoding='utf-8')
    pattern = r'(packutil\s*=\s*)"([^"]*<name>[^"]*<version>[^"]*)"'
    match = re.search(pattern, content)
    if match:
        template = match.group(2)
        newContent = content[:match.start(2)] + targetRelPath + content[match.end(2):]
        cfgPath.write_text(newContent, encoding='utf-8')
        stateFile = projectRoot / stateFileRel
        stateFile.parent.mkdir(parents=True, exist_ok=True)
        stateFile.write_text(json.dumps({
            'template': template,
            'target_rel_path': targetRelPath,
            'cfg_path': str(cfgPath.resolve())
        }, indent=2), encoding='utf-8')

def build_packutil(cfg_path, buildlog) -> bool:
    cfgPath = Path(cfg_path).resolve()
    projectRoot = cfgPath.parent
    wheelSrc = projectRoot / wheelSrcRel
    pyprojectPath = projectRoot / pyprojectRel
    cacheDir = projectRoot / cacheRel
    tempBuildDir = projectRoot / tempBuildRel

    try:
        name, version = readPyprojectMeta(pyprojectPath)
    except Exception as e:
        buildlog.error(f'failed to read {pyprojectRel}: {e}')
        return False

    resolvedWheelName = f'{name}-{version}.whl'
    targetRelPath = f'PackIt/res/{resolvedWheelName}'
    targetWhlPath = projectRoot / targetRelPath

    updateCruelConfig(cfgPath, projectRoot, targetRelPath)

    totalDigest = hashSourceTree(wheelSrc)
    manifestPath = cacheDir / 'manifest.json'
    cachedWhl = cacheDir / resolvedWheelName
    manifest = {}
    if manifestPath.is_file():
        try:
            manifest = json.loads(manifestPath.read_text(encoding='utf-8'))
        except Exception:
            manifest = {}

    if cachedWhl.is_file() and manifest.get('digest') == totalDigest:
        buildlog.info(f'  = {resolvedWheelName} (cached)')
        targetWhlPath.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(cachedWhl, targetWhlPath)
        return True

    try:
        with buildlog.task(nameSpace, 'compile_packutil') as t:
            buildlog.info(f'building {resolvedWheelName}')
            t.progress(20)
            builtWhl = buildWheel(wheelSrc, tempBuildDir, buildlog)
            t.progress(70)
            cacheDir.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(builtWhl, cachedWhl)
            manifest['digest'] = totalDigest
            manifest['name'] = name
            manifest['version'] = version
            manifest['file_name'] = resolvedWheelName
            manifestPath.write_text(json.dumps(manifest, indent=2), encoding='utf-8')
            targetWhlPath.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(cachedWhl, targetWhlPath)
            if tempBuildDir.exists():
                shutil.rmtree(tempBuildDir)
            buildlog.info(f'built {resolvedWheelName}')
            t.progress(100)
        return True
    except Exception as e:
        buildlog.error(f'compile_packutil failed: {e}')
        clean_packutil(projectRoot, buildlog)
        return False

def clean_packutil(projectRoot, buildLog=None) -> None:
    root = Path(projectRoot).resolve()
    stateFile = root / stateFileRel
    targetRelPath = None
    template = 'PackIt/res/<name>-<version>.whl'
    cfgPath = root / 'cruel.toml'

    if stateFile.is_file():
        try:
            data = json.loads(stateFile.read_text(encoding='utf-8'))
            targetRelPath = data.get('target_rel_path')
            template = data.get('template', template)
            if data.get('cfg_path'):
                cfgPath = Path(data['cfg_path'])
        except Exception:
            pass
        try:
            stateFile.unlink()
        except Exception:
            pass

    if targetRelPath:
        targetFile = root / targetRelPath
        if targetFile.is_file():
            try:
                targetFile.unlink()
                if buildLog:
                    buildLog.info(f'cleaned {targetRelPath}')
            except Exception:
                pass

    resDir = root / 'PackIt' / 'res'
    if resDir.is_dir():
        for whlFile in resDir.glob('packutil-*.whl'):
            try:
                whlFile.unlink()
                if buildLog:
                    buildLog.info(f'cleaned {whlFile.name}')
            except Exception:
                pass

    if cfgPath.is_file():
        try:
            content = cfgPath.read_text(encoding='utf-8')
            pattern = r'(packutil\s*=\s*)"PackIt/res/packutil-[^"]+\.whl"'
            if re.search(pattern, content):
                newContent = re.sub(pattern, rf'\1"{template}"', content)
                cfgPath.write_text(newContent, encoding='utf-8')
                if buildLog:
                    buildLog.info('restored cruel.toml requirements.local template')
        except Exception as e:
            if buildLog:
                buildLog.warn(f'failed to restore cruel.toml: {e}')

    tempBuildDir = root / tempBuildRel
    if tempBuildDir.exists():
        try:
            shutil.rmtree(tempBuildDir)
        except Exception:
            pass
