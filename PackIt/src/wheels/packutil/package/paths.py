# pyright: reportMissingImports=false

from pathlib import Path
from cruel import get_assets_dir, get_locale_dir, get_share_dir, get_source_dir
from .logx import logx

def getFilesDir() -> str:
    try:
        from org.telegram.messenger import ApplicationLoader
        return ApplicationLoader.applicationContext.getFilesDir().getAbsolutePath()
    except Exception as e:
        logx(f"failed to get filesDir: {e}", isDebug=False)
        raise

def getCacheDir() -> str:
    try:
        from org.telegram.messenger import ApplicationLoader
        return ApplicationLoader.applicationContext.getCacheDir().getAbsolutePath()
    except Exception as e:
        logx(f"failed to get cacheDir: {e}", isDebug=False)
        raise

def getPluginsDir() -> str:
    return getFilesDir() + "/plugins"

def getPluginSettingsPath() -> str:
    return getPluginsDir() + "/plugin_settings.json"

def getPackitDir() -> str:
    return getFilesDir() + "/packit"

def getPackitVarDir() -> str:
    return getPackitDir() + "/var"

def getLogsDir() -> str:
    return getPackitVarDir() + "/logs"

def getHistoryDir() -> str:
    return getLogsDir() + "/history"

def getLatestLogPath() -> str:
    return getLogsDir() + "/latest.txt"

def getConfigsDir() -> str:
    return getPackitDir() + "/packitConfigs"

def getReposCacheDir() -> str:
    return getPackitDir() + "/reposCache"

def getTempDir() -> str:
    return getPackitDir() + "/packitTemp"

def getAssetsDir(pluginId: str = "packit") -> Path:
    try:
        return get_assets_dir(pluginId)
    except Exception as e:
        logx(f"failed to get assetsDir for {pluginId}: {e}", isDebug=False)
        raise

def getSourceDir(pluginId: str = "packit") -> Path:
    try:
        return get_source_dir(pluginId)
    except Exception as e:
        logx(f"failed to get sourceDir for {pluginId}: {e}", isDebug=False)
        raise

def getShareDir(pluginId: str = "packit") -> Path:
    try:
        return get_share_dir(pluginId)
    except Exception as e:
        logx(f"failed to get shareDir for {pluginId}: {e}", isDebug=False)
        raise

def getLocaleDir(pluginId: str = "packit") -> Path:
    try:
        return get_locale_dir(pluginId)
    except Exception as e:
        logx(f"failed to get localeDir for {pluginId}: {e}", isDebug=False)
        raise

def getDexPath(name: str = "Core.dex", pluginId: str = "packit") -> Path:
    return getAssetsDir(pluginId) / name

def getCoreDexPath(pluginId: str = "packit") -> Path:
    return getDexPath("Core.dex", pluginId)

def getComposeDexPath(pluginId: str = "packit") -> Path:
    return getDexPath("Compose.dex", pluginId)
