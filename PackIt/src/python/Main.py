# pyright: reportMissingImports=false
import threading
from packutil import logx, initLogSession, reloadConfig
from .core.DexLoader import getCoreLoader, loadCoreClass, callStatic, resetLoaders

def checkRestartRequired(plugin):
    try:
        from cruel.plugins import is_updated
        pluginId = getattr(plugin, "id", None) or "packit"
        updatedCount = is_updated(pluginId)
        if updatedCount == 0 and pluginId != "packit":
            updatedCount = is_updated("packit")
        logx(f"checkRestartRequired: pluginId={pluginId}, is_updated={updatedCount}", isDebug=False)
        if updatedCount > 0:
            try:
                from .ui.sheets.RestartRequired import showRestartBottomSheet
                showRestartBottomSheet()
                logx("RestartRequired bottomsheet scheduled", isDebug=False)
            except Exception as e:
                logx(f"showRestartBottomSheet call error: {e}", isDebug=False)

            try:
                restartCls = loadCoreClass("sh.packit.core.ui.RestartRequired")
                if restartCls is not None:
                    text = "Client restart required"
                    button = "Restart"
                    try:
                        pstrings = getattr(plugin, "strings", None)
                        if pstrings is not None:
                            if hasattr(pstrings, "get"):
                                text = str(pstrings.get("restart_required", text))
                                button = str(pstrings.get("restart_button", button))
                            elif isinstance(pstrings, dict):
                                enDict = pstrings.get("en", pstrings)
                                text = str(enDict.get("restart_required", text))
                                button = str(enDict.get("restart_button", button))
                        else:
                            from elyx import strings
                            text = str(getattr(strings, "restart_required", getattr(strings, "bulletin_restart_required", text)))
                            button = str(getattr(strings, "restart_button", getattr(strings, "bulletin_restart_button", button)))
                    except Exception as e:
                        logx(f"strings lookup notice: {e}", isDebug=False)
                    callStatic(restartCls, "show", text, button)
                    logx("RestartRequired bulletin scheduled", isDebug=False)
                else:
                    logx("checkRestartRequired: restartCls is None", isDebug=False)
            except Exception as e:
                logx(f"RestartRequired bulletin show error: {e}", isDebug=False)
    except Exception as e:
        logx(f"checkRestartRequired error: {e}", isDebug=False)

def initCoreDex(plugin=None):
    try:
        loader = getCoreLoader()
        if loader is None:
            logx("initCoreDex: loader is None", isDebug=False)
            return
        cls = loadCoreClass("sh.packit.core.state.CoreState")
        if cls is not None:
            try:
                callStatic(cls, "markCoreReady")
                logx("CoreState marked ready", isDebug=False)
            except Exception as e:
                logx(f"markCoreReady error: {e}", isDebug=False)
        else:
            logx("initCoreDex: CoreState cls is None", isDebug=False)

        logxCls = loadCoreClass("sh.packit.core.utils.Logx")
        if logxCls is not None:
            try:
                callStatic(logxCls, "reloadConfig")
            except Exception as e:
                logx(f"Logx reloadConfig error: {e}", isDebug=False)

        if plugin is not None:
            checkRestartRequired(plugin)
    except Exception as e:
        logx(f"initCoreDex error: {e}", isDebug=False)

def load(plugin):
    initLogSession()
    reloadConfig()
    logx(f"loading plugin {getattr(plugin, 'id', None)}", isDebug=False)
    threading.Thread(target=initCoreDex, args=(plugin,), name="KtPackitCoreInit", daemon=True).start()

def unload(plugin):
    logx(f"unloading plugin {getattr(plugin, 'id', None)}", isDebug=False)
    resetLoaders()
