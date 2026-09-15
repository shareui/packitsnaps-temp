package sh.packit.compose.activities

import android.content.Context
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.composeshell.TelegramTheme
import de.shareui.composeshell.TelegramThemeBridge
import de.shareui.exterasdk.localization.Strings
import org.json.JSONObject
import sh.packit.compose.components.ExpressivePalette
import sh.packit.compose.components.ExpressiveSettingsGroup
import sh.packit.compose.components.SettingsFooter
import sh.packit.compose.components.SettingsSectionTitle
import sh.packit.compose.components.SettingsSwitchItem
import sh.packit.compose.components.expressiveShapeFor
import sh.packit.compose.utils.PackItTheme
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Logx
import java.lang.reflect.Method

object DebugActivity {
    @JvmStatic
    fun createView(context: Context, argsJson: String?): View {
        Logx.logx("DebugActivity.createView started", isDebug = true)
        val assetsDir: String? = try {
            if (argsJson != null) JSONObject(argsJson).optString("assets_dir", null) else null
        } catch (_: Throwable) {
            null
        }
        return ComposeView(context).apply {
            setContent {
                PackItTheme(assetsDir = assetsDir) {
                    DebugScreen()
                }
            }
        }
    }

    @JvmStatic
    fun open(context: Context, title: String = "Debug"): Boolean {
        Logx.logx("DebugActivity.open started: title=$title", isDebug = true)
        return try {
            val pyClass: Class<*> = Class.forName("com.chaquo.python.Python")
            val py: Any? = pyClass.getMethod("getInstance").invoke(null)
            val getModule: Method = pyClass.getMethod("getModule", String::class.java)
            val moduleName: String = "ElyxPlugins." + CoreState.PLUGIN_ID + ".ui.activities.PluginSettings"
            val module: Any? = getModule.invoke(py, moduleName)
            if (module == null) {
                Logx.logx("Python module $moduleName not found", isDebug = false)
                return false
            }
            val pyObjClass: Class<*> = Class.forName("com.chaquo.python.PyObject")
            val callAttr: Method = pyObjClass.getMethod("callAttr", String::class.java, Array<Any?>::class.java)
            val args: Array<Any?> = arrayOf(null, title)
            val result: Any? = callAttr.invoke(module, *arrayOf<Any?>("openDebugScreen", args))
            val success: Boolean = result?.toString() == "True" || result as? Boolean == true
            Logx.logx("openDebugScreen result: $success", isDebug = true)
            success
        } catch (e: Throwable) {
            Logx.logx("failed to open Debug sub-fragment: $e", isDebug = false)
            false
        }
    }
    @JvmStatic
    fun syncLogConfig(key: String, value: Boolean) {
        if (key == "debug_logs") {
            Logx.setDebugLogs(value)
        } else if (key == "write_logs") {
            Logx.setWriteLogs(value)
        }
        try {
            val pyClass: Class<*> = Class.forName("com.chaquo.python.Python")
            val py: Any? = pyClass.getMethod("getInstance").invoke(null)
            val getModule: Method = pyClass.getMethod("getModule", String::class.java)
            val moduleName: String = "ElyxPlugins." + CoreState.PLUGIN_ID + ".ui.activities.PluginSettings"
            val module: Any? = getModule.invoke(py, moduleName)
            if (module != null) {
                val pyObjClass: Class<*> = Class.forName("com.chaquo.python.PyObject")
                val callAttr: Method = pyObjClass.getMethod("callAttr", String::class.java, Array<Any?>::class.java)
                val args: Array<Any?> = arrayOf(key, value)
                callAttr.invoke(module, *arrayOf<Any?>("updateLogConfig", args))
            }
        } catch (e: Throwable) {
            Logx.logx("syncLogConfig failed for $key: $e", isDebug = false)
        }
    }
}

@Composable
fun DebugScreen(
    modifier: Modifier = Modifier,
    onAction: ((String) -> Unit)? = null
) {
    val strings: Strings = Strings.of(CoreState.PLUGIN_ID)
    val scrollState = rememberScrollState()
    val isDark: Boolean = TelegramThemeBridge.isDark || isSystemInDarkTheme()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            DebugLoggingSection(strings = strings, isDark = isDark, onAction = onAction)
            SettingsFooter()
        }
    }
}

@Composable
private fun DebugLoggingSection(
    strings: Strings,
    isDark: Boolean,
    onAction: ((String) -> Unit)?
) {
    SettingsSectionTitle(title = strings.get("debug_menu", "Debug menu"))
    ExpressiveSettingsGroup {
        SettingsSwitchItem(
            title = strings.get("debug_logs", "Debug logs"),
            subtitle = strings.get("debug_logs_desc", "Informational logs will be displayed"),
            settingKey = "debug_logs",
            default = true,
            iconName = "msg_log",
            iconColors = ExpressivePalette.categoryColors("debug", isDark),
            shape = expressiveShapeFor(0, 2),
            onSwitch = { enabled ->
                DebugActivity.syncLogConfig("debug_logs", enabled)
                Logx.logx("debug_logs setting changed: $enabled", isDebug = false)
                onAction?.invoke("debug_logs:$enabled")
            }
        )
        SettingsSwitchItem(
            title = strings.get("log_history", "Log history"),
            subtitle = strings.get("log_history_desc", "Duplicates logs to a file on the device"),
            settingKey = "write_logs",
            default = false,
            iconName = "msg_edit",
            iconColors = ExpressivePalette.categoryColors("debug", isDark),
            shape = expressiveShapeFor(1, 2),
            onSwitch = { enabled ->
                DebugActivity.syncLogConfig("write_logs", enabled)
                Logx.logx("write_logs setting changed: $enabled", isDebug = false)
                onAction?.invoke("write_logs:$enabled")
            }
        )
    }
}

