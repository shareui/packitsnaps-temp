# pyright: reportMissingImports=false
import threading
from android_utils import log
from .core.DexLoader import getCoreLoader, loadCoreClass, callStatic, resetLoaders

def initCoreDex():
    loader = getCoreLoader()
    if loader is None:
        return
    cls = loadCoreClass("sh.packit.core.state.CoreState")
    if cls is not None:
        try:
            callStatic(cls, "markCoreReady")
            log("[ktpackit] CoreState marked ready")
        except Exception as e:
            log(f"[ktpackit] markCoreReady error: {e}")

def load(plugin):
    log("[ktpackit] loading ktpackit plugin")
    threading.Thread(target=initCoreDex, name="KtPackitCoreInit", daemon=True).start()

def unload(plugin):
    log("[ktpackit] unloading ktpackit plugin")
    resetLoaders()
