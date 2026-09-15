from packutil import logx
from ui.bulletin import BulletinHelper
from ...core.DexLoader import getComposeDexPath, getCoreLoader, ensureReadOnly, getAssetsDir

__all__ = ["openSettingsScreen", "openSubSettingsScreen", "openDebugScreen", "openAppearanceScreen", "updateLogConfig"]

def openSettingsScreen(parentFragment=None, title: str = "PackIt") -> bool:
    try:
        from ElyxPlugins.composeshell import ComposeFragment
    except ImportError:
        BulletinHelper.show_error("ComposeShell plugin is required to display settings")
        logx("ComposeShell plugin not found", isDebug=False)
        return False

    composeDex = getComposeDexPath()
    if not composeDex.exists():
        BulletinHelper.show_error("Compose.dex not found in assets")
        logx(f"Compose.dex not found at {composeDex}", isDebug=False)
        return False

    ensureReadOnly(composeDex)
    coreLoader = getCoreLoader()
    if coreLoader is None:
        BulletinHelper.show_error("Failed to initialize Core.dex")
        logx("coreLoader is None", isDebug=False)
        return False

    try:
        fragment = ComposeFragment(
            dex=str(composeDex.absolute()),
            entry="sh.packit.compose.ComposeEntry",
            title=title,
            args={"assets_dir": str(getAssetsDir("packit"))},
            parent_loader=coreLoader
        )
        fragment.show(parentFragment)
        return True
    except Exception as e:
        BulletinHelper.show_error(f"Error opening settings: {e}")
        logx(f"failed to open ComposeFragment: {e}", isDebug=False)
        return False

def openSubSettingsScreen(parentFragment=None, title: str = "Settings") -> bool:
    try:
        from ElyxPlugins.composeshell import ComposeFragment
    except ImportError:
        BulletinHelper.show_error("ComposeShell plugin is required to display settings")
        logx("ComposeShell plugin not found", isDebug=False)
        return False

    composeDex = getComposeDexPath()
    if not composeDex.exists():
        BulletinHelper.show_error("Compose.dex not found in assets")
        logx(f"Compose.dex not found at {composeDex}", isDebug=False)
        return False

    ensureReadOnly(composeDex)
    coreLoader = getCoreLoader()
    if coreLoader is None:
        BulletinHelper.show_error("Failed to initialize Core.dex")
        logx("coreLoader is None", isDebug=False)
        return False

    try:
        fragment = ComposeFragment(
            dex=str(composeDex.absolute()),
            entry="sh.packit.compose.activities.SettingsActivity",
            title=title,
            args={"screen": "settings", "assets_dir": str(getAssetsDir("packit"))},
            parent_loader=coreLoader
        )
        fragment.show(parentFragment)
        return True
    except Exception as e:
        BulletinHelper.show_error(f"Error opening settings: {e}")
        logx(f"failed to open ComposeFragment: {e}", isDebug=False)
        return False

def openDebugScreen(parentFragment=None, title: str = "Debug") -> bool:
    try:
        from ElyxPlugins.composeshell import ComposeFragment
    except ImportError:
        BulletinHelper.show_error("ComposeShell plugin is required to display settings")
        logx("ComposeShell plugin not found", isDebug=False)
        return False

    composeDex = getComposeDexPath()
    if not composeDex.exists():
        BulletinHelper.show_error("Compose.dex not found in assets")
        logx(f"Compose.dex not found at {composeDex}", isDebug=False)
        return False

    ensureReadOnly(composeDex)
    coreLoader = getCoreLoader()
    if coreLoader is None:
        BulletinHelper.show_error("Failed to initialize Core.dex")
        logx("coreLoader is None", isDebug=False)
        return False

    try:
        fragment = ComposeFragment(
            dex=str(composeDex.absolute()),
            entry="sh.packit.compose.activities.DebugActivity",
            title=title,
            args={"screen": "debug", "assets_dir": str(getAssetsDir("packit"))},
            parent_loader=coreLoader
        )
        fragment.show(parentFragment)
        return True
    except Exception as e:
        BulletinHelper.show_error(f"Error opening debug settings: {e}")
        logx(f"failed to open ComposeFragment: {e}", isDebug=False)
        return False

def openAppearanceScreen(parentFragment=None, title: str = "Appearance") -> bool:
    try:
        from ElyxPlugins.composeshell import ComposeFragment
    except ImportError:
        BulletinHelper.show_error("ComposeShell plugin is required to display settings")
        logx("ComposeShell plugin not found", isDebug=False)
        return False
    composeDex = getComposeDexPath()
    if not composeDex.exists():
        BulletinHelper.show_error("Compose.dex not found in assets")
        logx(f"Compose.dex not found at {composeDex}", isDebug=False)
        return False
    ensureReadOnly(composeDex)
    coreLoader = getCoreLoader()
    if coreLoader is None:
        BulletinHelper.show_error("Failed to initialize Core.dex")
        logx("coreLoader is None", isDebug=False)
        return False
    try:
        fragment = ComposeFragment(
            dex=str(composeDex.absolute()),
            entry="sh.packit.compose.activities.AppearanceActivity",
            title=title,
            args={"screen": "appearance", "assets_dir": str(getAssetsDir("packit"))},
            parent_loader=coreLoader
        )
        fragment.show(parentFragment)
        return True
    except Exception as e:
        BulletinHelper.show_error(f"Error opening appearance settings: {e}")
        logx(f"failed to open ComposeFragment: {e}", isDebug=False)
        return False

def updateLogConfig(key: str, value: bool) -> None:
    try:
        from packutil import setDebugLogs, setWriteLogs
        if key == "debug_logs":
            setDebugLogs(value)
        elif key == "write_logs":
            setWriteLogs(value)
    except Exception as e:
        logx(f"updateLogConfig notice: {e}", isDebug=False)
