# pyright: reportMissingImports=false

import fcntl
import json
import os
import threading
import time
from android_utils import log as _log

_writeLock = threading.Lock()
_activeLogPath: str | None = None

def _getFilesDir() -> str:
    try:
        from org.telegram.messenger import ApplicationLoader
        return ApplicationLoader.applicationContext.getFilesDir().getAbsolutePath()
    except Exception as e:
        _log(f"[packit] failed to get filesDir: {e}")
        return "/data/data/org.telegram.messenger.web/files"

def _getPluginsDir() -> str:
    return _getFilesDir() + "/plugins"

def _getLogsDir() -> str:
    return _getFilesDir() + "/packit/var/logs"

def _getHistoryDir() -> str:
    return _getLogsDir() + "/history"

def initLogSession(force: bool = False) -> str:
    global _activeLogPath
    if _activeLogPath is not None and not force:
        return _activeLogPath
    logsDir = _getLogsDir()
    historyDir = _getHistoryDir()
    try:
        os.makedirs(historyDir, exist_ok=True)
    except Exception as e:
        _log(f"[packit] failed to create history dir: {e}")
    fileName = time.strftime("%y:%m:%d-%H:%M:%S.txt")
    historyFile = os.path.join(historyDir, fileName)
    try:
        if not os.path.exists(historyFile):
            open(historyFile, "a", encoding="utf-8").close()
    except Exception as e:
        _log(f"[packit] failed to touch history file: {e}")
    latestFile = os.path.join(logsDir, "latest.txt")
    try:
        if os.path.lexists(latestFile):
            os.unlink(latestFile)
        os.symlink(historyFile, latestFile)
    except Exception as e:
        _log(f"[packit] failed to symlink latest.txt: {e}")
    _activeLogPath = latestFile if os.path.exists(latestFile) else historyFile
    return _activeLogPath

def _isWriteLogsEnabled() -> bool:
    try:
        from elyx import settings as _s
        val = _s.get("write_logs", None)
        if val is not None:
            return bool(val)
    except Exception as e:
        _log(f"[packit] write_logs settings lookup notice: {e}")
    try:
        path = _getPluginsDir() + "/plugin_settings.json"
        if not os.path.exists(path):
            return False
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
        pdata = data.get("packit") or data.get("shareui_packit") or {}
        return bool(pdata.get("write_logs", False))
    except Exception as e:
        _log(f"[packit] write_logs json lookup notice: {e}")
        return False

def _isDebugLogsEnabled() -> bool:
    try:
        from elyx import settings as _s
        val = _s.get("debug_logs", None)
        if val is not None:
            return bool(val)
    except Exception as e:
        _log(f"[packit] debug_logs settings lookup notice: {e}")
    try:
        path = _getPluginsDir() + "/plugin_settings.json"
        if not os.path.exists(path):
            return True
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
        pdata = data.get("packit") or data.get("shareui_packit") or {}
        return bool(pdata.get("debug_logs", True))
    except Exception as e:
        _log(f"[packit] debug_logs json lookup notice: {e}")
        return True

def _writeToFile(msg: str):
    global _activeLogPath
    path = _activeLogPath
    if path is None:
        path = initLogSession()
    if path is None:
        return
    ts = time.strftime("%Y-%m-%d %H:%M:%S")
    fullLine = f"{ts} ~> {msg}\n"
    with _writeLock:
        try:
            with open(path, "a", encoding="utf-8") as f:
                try:
                    fcntl.lockf(f.fileno(), fcntl.LOCK_EX)
                except Exception as e:
                    _log(f"[packit] failed to acquire lockf: {e}")
                try:
                    f.write(fullLine)
                    f.flush()
                finally:
                    try:
                        fcntl.lockf(f.fileno(), fcntl.LOCK_UN)
                    except Exception as e:
                        _log(f"[packit] failed to release lockf: {e}")
        except Exception as e:
            _log(f"[packit] failed to write log: {e}")

def logx(msg: str, isDebug: bool):
    formatted = str(msg)
    if not formatted.startswith("[packit]"):
        formatted = f"[packit] {formatted}"
    if isDebug and not _isDebugLogsEnabled():
        return
    _log(formatted)
    if _isWriteLogsEnabled():
        _writeToFile(formatted)
