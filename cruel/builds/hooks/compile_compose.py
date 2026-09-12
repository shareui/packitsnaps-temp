import hashlib
import json
import os
import shutil
import subprocess
from pathlib import Path
import compile_core

nameSpace = 'compile_compose'
composeSrcRel = Path('PackIt') / 'src' / 'kotlin' / 'sh' / 'packit' / 'compose'
coreSrcRel = Path('PackIt') / 'src' / 'kotlin' / 'sh' / 'packit' / 'core'
stubsSrcRel = Path('PackIt') / 'src' / 'kotlin' / 'stubs'
assetsDexRel = Path('PackIt') / 'res' / 'assets' / 'Compose.dex'
cacheRel = Path('cruel') / 'local' / 'cache' / 'kotlin'
toolsCompileRel = Path('tools') / 'compile'

def hashFile(filePath: Path) -> str:
    h = hashlib.sha256()
    h.update(filePath.read_bytes())
    return h.hexdigest()[:16]

def hashDir(dirPath: Path, pattern: str = '*') -> str:
    if not dirPath.is_dir():
        return ''
    files = sorted(p for p in dirPath.rglob(pattern) if p.is_file())
    parts = [f'{p.relative_to(dirPath).as_posix()}:{hashFile(p)}' for p in files]
    return hashlib.sha256('|'.join(parts).encode('utf-8')).hexdigest()[:16]

def syncDirectory(srcDir: Path, dstDir: Path) -> None:
    if not srcDir.is_dir():
        return
    dstDir.mkdir(parents=True, exist_ok=True)
    srcFiles = {p.relative_to(srcDir): p for p in srcDir.rglob('*') if p.is_file()}
    dstFiles = {p.relative_to(dstDir): p for p in dstDir.rglob('*') if p.is_file()}
    for relPath, dstFile in dstFiles.items():
        if relPath not in srcFiles:
            dstFile.unlink()
    for relPath, srcFile in srcFiles.items():
        targetPath = dstDir / relPath
        if not targetPath.is_file() or hashFile(targetPath) != hashFile(srcFile):
            targetPath.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(srcFile, targetPath)

def clean_compose_dex(projectRoot, buildLog=None) -> None:
    root = Path(projectRoot).resolve()
    targetDex = root / assetsDexRel
    if targetDex.is_file():
        try:
            targetDex.unlink()
            if buildLog:
                buildLog.info('cleaned temporary Compose.dex from assets')
        except Exception as e:
            if buildLog:
                buildLog.warn(f'failed to clean Compose.dex: {e}')

def findComposeDexFiles(outDir: Path) -> list[Path]:
    if not outDir.is_dir():
        return []
    return sorted(outDir.rglob('*.dex'))

def mergeDexFiles(d8Jar: str, androidJar: str, dexFiles: list[Path], outputDir: Path) -> bool:
    outputDir.mkdir(parents=True, exist_ok=True)
    d8Cmd = [
        'java', '-cp', d8Jar, 'com.android.tools.r8.D8',
        '--release',
        '--min-api', '26',
        '--lib', androidJar,
        '--output', str(outputDir),
        *(str(p) for p in dexFiles),
    ]
    res = subprocess.run(d8Cmd, capture_output=True, text=True)
    return res.returncode == 0

def syncAllSources(root: Path, toolsCompileDir: Path) -> None:
    composeSrc = root / composeSrcRel
    coreSrc = root / coreSrcRel
    stubsSrc = root / stubsSrcRel
    appJavaDir = toolsCompileDir / 'app' / 'src' / 'main' / 'java'
    syncDirectory(composeSrc, appJavaDir / 'sh' / 'packit' / 'compose')
    syncDirectory(coreSrc, appJavaDir / 'sh' / 'packit' / 'core')
    if (stubsSrc / 'org').is_dir():
        syncDirectory(stubsSrc / 'org', appJavaDir / 'org')
    if (stubsSrc / 'de').is_dir():
        syncDirectory(stubsSrc / 'de', appJavaDir / 'de')

def build_compose_dex(projectRoot, buildLog, cache=None, cruelBin=None) -> bool:
    root = Path(projectRoot).resolve()
    composeSrc = root / composeSrcRel
    targetDex = root / assetsDexRel
    cacheDir = root / cacheRel
    toolsCompileDir = root / toolsCompileRel
    if not composeSrc.is_dir():
        buildLog.error(f'compose source directory not found at {composeSrc}')
        return False
    deps = compile_core.check_kotlin_deps(cacheDir, buildLog)
    if deps is None:
        return False
    composeDigest = hashDir(composeSrc, '*.kt')
    coreDigest = hashDir(root / coreSrcRel, '*.kt')
    stubsDigest = hashDir(root / stubsSrcRel)
    totalDigest = f'{composeDigest}::{coreDigest}::{stubsDigest}'
    manifestPath = cacheDir / 'compose_manifest.json'
    cachedDex = cacheDir / 'Compose.dex'
    manifest = {}
    if manifestPath.is_file():
        try:
            manifest = json.loads(manifestPath.read_text(encoding='utf-8'))
        except Exception:
            manifest = {}
    if cachedDex.is_file() and manifest.get('digest') == totalDigest:
        buildLog.info('  = Compose.dex (cached)')
        targetDex.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(cachedDex, targetDex)
        return True
    try:
        with buildLog.task(nameSpace, 'compile_compose') as t:
            buildLog.info('building Compose.dex via tools/compile')
            composeFiles = sorted(p for p in composeSrc.rglob('*.kt') if p.is_file())
            buildLog.info(f'  compiling {len(composeFiles)} compose files:')
            for p in composeFiles:
                buildLog.info(f'    * {p.relative_to(composeSrc).as_posix()}')
            syncAllSources(root, toolsCompileDir)
            t.progress(20)
            gradlew = toolsCompileDir / 'gradlew'
            if not os.access(gradlew, os.X_OK):
                gradlew.chmod(0o755)
            gradleCmd = [str(gradlew), '--no-daemon', ':app:dexBuilderRelease']
            res = subprocess.run(gradleCmd, cwd=str(toolsCompileDir), capture_output=True, text=True)
            if res.returncode != 0:
                buildLog.error(f'gradle dexBuilderRelease failed:\n{res.stderr.strip() or res.stdout.strip()}')
                return False
            t.progress(60)
            composeOutDir = toolsCompileDir / 'app' / 'build' / 'intermediates' / 'project_dex_archive' / 'release' / 'dexBuilderRelease' / 'out' / 'sh' / 'packit' / 'compose'
            themeOutDir = toolsCompileDir / 'app' / 'build' / 'intermediates' / 'project_dex_archive' / 'release' / 'dexBuilderRelease' / 'out' / 'de' / 'shareui' / 'composeshell'
            dexFiles = findComposeDexFiles(composeOutDir) + findComposeDexFiles(themeOutDir)
            if not dexFiles:
                buildLog.error(f'no compose dex files found in {composeOutDir}')
                return False
            buildLog.info(f'  merging {len(dexFiles)} compose dex files with release D8')
            mergeDir = root / 'cruel' / 'local' / 'temp' / 'kotlin-compose-build' / 'merged'
            if mergeDir.exists():
                shutil.rmtree(mergeDir)
            mergeOk = mergeDexFiles(deps['d8_jar'], deps['android_jar'], dexFiles, mergeDir)
            if not mergeOk:
                buildLog.error('d8 release merge failed for Compose.dex')
                return False
            finalDex = mergeDir / 'classes.dex'
            if not finalDex.is_file():
                buildLog.error('d8 release merge produced no classes.dex')
                return False
            t.progress(85)
            cacheDir.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(finalDex, cachedDex)
            targetDex.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(cachedDex, targetDex)
            manifest['digest'] = totalDigest
            manifestPath.write_text(json.dumps(manifest, indent=2), encoding='utf-8')
            sizeKb = targetDex.stat().st_size // 1024
            buildLog.info(f'built Compose.dex ({sizeKb} KB)')
            primaryClasses, _ = compile_core.read_dex_classes(targetDex)
            buildLog.info(f'  classes in Compose.dex ({len(primaryClasses)}):')
            for cls in primaryClasses:
                buildLog.info(f'    * {cls}')
            t.progress(100)
            return True
    except Exception as e:
        buildLog.error(f'compile_compose error: {e}')
        clean_compose_dex(projectRoot, buildLog)
        return False
