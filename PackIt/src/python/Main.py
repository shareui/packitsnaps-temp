# pyright: reportMissingImports=false
import threading
from packutil import logx, initLogSession
from .core.DexLoader import getCoreLoader, loadCoreClass, callStatic, resetLoaders

def checkRestartRequired(plugin):
    try:
        from cruel.plugins import is_updated
        pluginId = getattr(plugin, "id", None) or "packit"
        updatedCount = is_updated(pluginId)
        logx(f"checkRestartRequired: is_updated={updatedCount}", isDebug=True)
        if updatedCount > 0:
            restartCls = loadCoreClass("sh.packit.core.ui.RestartRequired")
            if restartCls is not None:
                text = "Client restart required"
                button = "Restart"
                try:
                    from elyx import strings
                    text = str(getattr(strings, "restart_required", getattr(strings, "bulletin_restart_required", text)))
                    button = str(getattr(strings, "restart_button", getattr(strings, "bulletin_restart_button", button)))
                except Exception as e:
                    logx(f"strings lookup notice: {e}", isDebug=True)
                callStatic(restartCls, "show", text, button)
                logx("RestartRequired bulletin scheduled", isDebug=True)
    except Exception as e:
        logx(f"checkRestartRequired error: {e}", isDebug=False)

def initCoreDex(plugin=None):
    loader = getCoreLoader()
    if loader is None:
        return
    cls = loadCoreClass("sh.packit.core.state.CoreState")
    if cls is not None:
        try:
            callStatic(cls, "markCoreReady")
            logx("CoreState marked ready", isDebug=True)
        except Exception as e:
            logx(f"markCoreReady error: {e}", isDebug=False)
    if plugin is not None:
        checkRestartRequired(plugin)

def load(plugin):
    initLogSession()
    logx("loading plugin", isDebug=True)
    threading.Thread(target=initCoreDex, args=(plugin,), name="KtPackitCoreInit", daemon=True).start()

def unload(plugin):
    logx("unloading plugin", isDebug=True)
    resetLoaders()
