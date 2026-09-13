package sh.packit.core.utils

import android.content.Context
import android.util.Log
import de.shareui.exterasdk.utils.AndroidUtils
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import java.io.File
import java.io.FileOutputStream
import java.nio.channels.FileLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logx {
    private const val TAG: String = "PackIt"
    private val writeLock: Any = Any()
    @Volatile
    private var activeLogFile: File? = null

    private fun getContext(): Context? {
        return try {
            ApplicationLoader.applicationContext
        } catch (e: Throwable) {
            Log.d(TAG, "failed to get ApplicationLoader context: $e")
            null
        }
    }

    private fun getFilesDir(): String? {
        val ctx: Context = getContext() ?: return null
        return ctx.filesDir.absolutePath
    }

    private fun getPluginsDir(): String? {
        val filesDir: String = getFilesDir() ?: return null
        return "$filesDir/plugins"
    }

    private fun getLogsDir(): File? {
        val filesDir: String = getFilesDir() ?: return null
        return File(filesDir, "packit/var/logs")
    }

    @JvmStatic
    fun initLogSession(force: Boolean = false): File? {
        if (!force) {
            activeLogFile?.let { return it }
        }
        val logsDir: File = getLogsDir() ?: return null
        val historyDir = File(logsDir, "history")
        if (!historyDir.exists()) {
            historyDir.mkdirs()
        }
        val fileName: String = SimpleDateFormat("yy:MM:dd-HH:mm:ss", Locale.US).format(Date()) + ".txt"
        val sessionFile = File(historyDir, fileName)
        try {
            if (!sessionFile.exists()) {
                sessionFile.createNewFile()
            }
        } catch (e: Throwable) {
            AndroidUtils.log("[packit] failed to create history file: $e")
        }
        val latestFile = File(logsDir, "latest.txt")
        try {
            android.system.Os.remove(latestFile.absolutePath)
        } catch (e: Throwable) {
            Log.d(TAG, "remove old latest.txt notice: $e")
        }
        try {
            android.system.Os.symlink(sessionFile.absolutePath, latestFile.absolutePath)
        } catch (e: Throwable) {
            AndroidUtils.log("[packit] failed to symlink latest.txt: $e")
        }
        val resolved = if (latestFile.exists()) latestFile else sessionFile
        activeLogFile = resolved
        return resolved
    }

    private fun getLogFile(): File? {
        activeLogFile?.let { return it }
        val logsDir: File = getLogsDir() ?: return null
        val latestFile = File(logsDir, "latest.txt")
        if (latestFile.exists()) {
            activeLogFile = latestFile
            return latestFile
        }
        return initLogSession()
    }

    private fun isWriteLogsEnabled(): Boolean {
        val pluginsDir: String = getPluginsDir() ?: return false
        return try {
            val file = File(pluginsDir, "plugin_settings.json")
            if (!file.exists()) return false
            val json = JSONObject(file.readText())
            val pluginObj: JSONObject? = json.optJSONObject("packit") ?: json.optJSONObject("shareui_packit")
            pluginObj?.optBoolean("write_logs", false) ?: false
        } catch (e: Throwable) {
            AndroidUtils.log("[packit] failed to read write_logs setting: $e")
            false
        }
    }

    private fun isDebugEnabled(): Boolean {
        val pluginsDir: String = getPluginsDir() ?: return true
        return try {
            val file = File(pluginsDir, "plugin_settings.json")
            if (!file.exists()) return true
            val json = JSONObject(file.readText())
            val pluginObj: JSONObject? = json.optJSONObject("packit") ?: json.optJSONObject("shareui_packit")
            if (pluginObj != null && pluginObj.has("debug_logs")) {
                pluginObj.optBoolean("debug_logs", true)
            } else {
                true
            }
        } catch (e: Throwable) {
            AndroidUtils.log("[packit] failed to read debug_logs setting: $e")
            true
        }
    }

    private fun writeToFile(msg: String) {
        val file: File = getLogFile() ?: return
        val ts: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val fullLine: String = "$ts ~> $msg\n"
        val bytes: ByteArray = fullLine.toByteArray(Charsets.UTF_8)
        synchronized(writeLock) {
            try {
                FileOutputStream(file, true).use { fos ->
                    val channel = fos.channel
                    var fileLock: FileLock? = null
                    try {
                        fileLock = channel.lock()
                    } catch (e: Throwable) {
                        Log.d(TAG, "channel.lock notice: $e")
                    }
                    try {
                        fos.write(bytes)
                        fos.flush()
                    } finally {
                        try {
                            fileLock?.release()
                        } catch (e: Throwable) {
                            Log.d(TAG, "channel lock release notice: $e")
                        }
                    }
                }
            } catch (e: Throwable) {
                AndroidUtils.log("[packit] failed to write log: $e")
            }
        }
    }

    @JvmStatic
    fun logx(msg: String, isDebug: Boolean = false) {
        val formatted: String = if (msg.startsWith("[packit]")) msg else "[packit] $msg"
        if (isDebug && !isDebugEnabled()) {
            return
        }
        try {
            AndroidUtils.log(formatted)
        } catch (e: Throwable) {
            Log.d(TAG, "AndroidUtils.log fallback: $e")
            Log.d(TAG, formatted)
        }
        if (isWriteLogsEnabled()) {
            writeToFile(formatted)
        }
    }
}
