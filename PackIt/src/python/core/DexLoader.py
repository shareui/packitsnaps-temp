# pyright: reportMissingImports=false
import os
import stat
import threading
from pathlib import Path
from packutil import logx
from packutil.paths import getAssetsDir, getCoreDexPath, getComposeDexPath, getDexPath

_lock = threading.Lock()
_coreLoader = None
_loadedClasses = {}

def ensureReadOnly(dexPath: Path):
    # android 10+ forbids loading writable dex files
    if dexPath.exists():
        try:
            os.chmod(str(dexPath), stat.S_IRUSR | stat.S_IRGRP | stat.S_IROTH)
        except Exception as e:
            logx(f"chmod error for {dexPath.name}: {e}", isDebug=False)

def callStatic(cls, methodName: str, *args):
    argCount = len(args)
    staticModifier = 0x8
    for m in cls.getMethods():
        if m.getName() == methodName and (m.getModifiers() & staticModifier) != 0 and len(m.getParameterTypes()) == argCount:
            return m.invoke(None, *args)
    # fallback for kotlin object singletons
    try:
        instanceField = cls.getField("INSTANCE")
        instance = instanceField.get(None)
        if instance is not None:
            for m in cls.getMethods():
                if m.getName() == methodName and len(m.getParameterTypes()) == argCount:
                    return m.invoke(instance, *args)
    except Exception as e:
        logx(f"singleton fallback error: {e}", isDebug=False)
    raise AttributeError(f"method {methodName} with {argCount} args not found on {cls}")

def hasAncestor(loader, target):
    curr = loader
    while curr is not None:
        if curr == target:
            return True
        try:
            curr = curr.getParent()
        except Exception:
            break
    return False

def getComposeShellLoader(context=None):
    try:
        from ElyxPlugins.composeshell import get_compose_loader
        return get_compose_loader(context)
    except Exception as e:
        logx(f"getComposeShellLoader error: {e}", isDebug=False)
        return None

def initCoreState(loader):
    try:
        cls = loader.loadClass("sh.packit.core.state.CoreState")
        _loadedClasses["sh.packit.core.state.CoreState"] = cls
        callStatic(cls, "markCoreReady")
        logx("CoreState marked ready", isDebug=True)
    except Exception as e:
        logx(f"initCoreState error: {e}", isDebug=False)

def getCoreLoader(context=None, parentLoader=None):
    global _coreLoader
    with _lock:
        if context is None:
            from org.telegram.messenger import ApplicationLoader
            context = ApplicationLoader.applicationContext

        composeLoader = getComposeShellLoader(context)
        desiredParent = parentLoader or composeLoader or context.getClassLoader()

        if _coreLoader is not None:
            if composeLoader is not None and not hasAncestor(_coreLoader, composeLoader):
                logx("resetting _coreLoader to chain composeLoader", isDebug=True)
                _coreLoader = None
                _loadedClasses.clear()
            else:
                return _coreLoader

        coreDex = getCoreDexPath()
        if not coreDex.exists():
            logx(f"Core.dex not found at {coreDex}", isDebug=False)
            return None
        ensureReadOnly(coreDex)

        try:
            from dalvik.system import DelegateLastClassLoader
            _coreLoader = DelegateLastClassLoader(str(coreDex.absolute()), desiredParent)
            logx("successfully initialized Core.dex loader", isDebug=True)
            initCoreState(_coreLoader)
        except Exception as e:
            logx(f"failed to initialize DelegateLastClassLoader: {e}", isDebug=False)
            return None
        return _coreLoader

def loadCoreClass(className: str, context=None):
    cached = _loadedClasses.get(className)
    if cached is not None:
        return cached
    loader = getCoreLoader(context)
    if loader is None:
        return None
    try:
        cls = loader.loadClass(className)
        _loadedClasses[className] = cls
        return cls
    except Exception as e:
        logx(f"failed to load class {className}: {e}", isDebug=False)
        return None

def resetLoaders():
    global _coreLoader
    with _lock:
        _coreLoader = None
        _loadedClasses.clear()
