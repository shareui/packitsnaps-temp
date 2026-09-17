package sh.packit.core.utils

import android.content.Context
import de.shareui.exterasdk.cruel.PluginPaths
import org.telegram.messenger.ApplicationLoader
import sh.packit.core.state.CoreState
import java.io.File

object Paths {
    private fun getContext(): Context? {
        return try {
            ApplicationLoader.applicationContext
        } catch (e: Throwable) {
            Logx.logx("failed to get ApplicationLoader context: $e", isDebug = false)
            null
        }
    }

    fun getFilesDir(): File? = getContext()?.filesDir

    fun getPluginsDir(): File? {
        val filesDir: File = getFilesDir() ?: return null
        return File(filesDir, "plugins")
    }

    fun getPluginSettingsFile(): File? {
        val pluginsDir: File = getPluginsDir() ?: return null
        return File(pluginsDir, "plugin_settings.json")
    }

    fun getPackitDir(): File? {
        val filesDir: File = getFilesDir() ?: return null
        return File(filesDir, "packit")
    }

    fun getPackitVarDir(): File? {
        val packitDir: File = getPackitDir() ?: return null
        return File(packitDir, "var")
    }

    fun getLogsDir(): File? {
        val varDir: File = getPackitVarDir() ?: return null
        return File(varDir, "logs")
    }

    fun getHistoryDir(): File? {
        val logsDir: File = getLogsDir() ?: return null
        return File(logsDir, "history")
    }

    fun getLatestLogFile(): File? {
        val logsDir: File = getLogsDir() ?: return null
        return File(logsDir, "latest.txt")
    }

    fun getAssetsDir(pluginId: String = CoreState.PLUGIN_ID, explicitAssetsDir: String? = null): File? {
        if (!explicitAssetsDir.isNullOrEmpty()) {
            return File(explicitAssetsDir)
        }
        val stateDir: String? = CoreState.assetsDir
        if (!stateDir.isNullOrEmpty()) {
            return File(stateDir)
        }
        return try {
            PluginPaths.getAssetsDir(pluginId)
        } catch (e: Throwable) {
            Logx.logx("failed to resolve assets dir: $e", isDebug = false)
            null
        }
    }

    fun getFontsDir(pluginId: String = CoreState.PLUGIN_ID, explicitAssetsDir: String? = null): File? {
        val assets: File = getAssetsDir(pluginId, explicitAssetsDir) ?: return null
        return if (assets.name == "fonts") assets else File(assets, "fonts")
    }
}
