# pyright: reportMissingImports=false
from android_utils import log
from ui.bulletin import BulletinHelper
from ..core.DexLoader import getComposeDexPath, getCoreLoader, ensureReadOnly, getAssetsDir

def openSettingsScreen(parentFragment=None, title: str = "PackIt") -> bool:
    try:
        from ElyxPlugins.composeshell import ComposeFragment
    except ImportError:
        BulletinHelper.show_error("ComposeShell plugin is required to display settings")
        log("[ktpackit] ComposeShell plugin not found")
        return False

    composeDex = getComposeDexPath()
    if not composeDex.exists():
        BulletinHelper.show_error("Compose.dex not found in assets")
        log(f"[ktpackit] Compose.dex not found at {composeDex}")
        return False

    ensureReadOnly(composeDex)
    coreLoader = getCoreLoader()
    if coreLoader is None:
        BulletinHelper.show_error("Failed to initialize Core.dex")
        log("[ktpackit] coreLoader is None")
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
        log(f"[ktpackit] failed to open ComposeFragment: {e}")
        return False
