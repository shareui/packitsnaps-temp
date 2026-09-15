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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.composeshell.TelegramTheme
import de.shareui.composeshell.TelegramThemeBridge
import de.shareui.exterasdk.localization.Strings
import de.shareui.exterasdk.ui.BulletinHelper
import org.json.JSONObject
import sh.packit.compose.components.ExpressivePalette
import sh.packit.compose.components.ExpressiveSettingsGroup
import sh.packit.compose.components.SettingsFooter
import sh.packit.compose.components.SettingsItem
import sh.packit.compose.components.SettingsSectionTitle
import sh.packit.compose.components.expressiveShapeFor
import sh.packit.compose.utils.PackItTheme
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Logx
import java.lang.reflect.Method

object SettingsActivity {
    @JvmStatic
    fun createView(context: Context, argsJson: String?): View {
        Logx.logx("SettingsActivity.createView started", isDebug = true)
        val assetsDir: String? = try {
            if (argsJson != null) JSONObject(argsJson).optString("assets_dir", null) else null
        } catch (_: Throwable) {
            null
        }
        return ComposeView(context).apply {
            setContent {
                PackItTheme(assetsDir = assetsDir) {
                    SettingsScreen()
                }
            }
        }
    }

    @JvmStatic
    fun open(context: Context, title: String = "Settings"): Boolean {
        Logx.logx("SettingsActivity.open started: title=$title", isDebug = true)
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
            val result: Any? = callAttr.invoke(module, *arrayOf<Any?>("openSubSettingsScreen", args))
            val success: Boolean = result?.toString() == "True" || result as? Boolean == true
            Logx.logx("openSubSettingsScreen result: $success", isDebug = true)
            success
        } catch (e: Throwable) {
            Logx.logx("failed to open Settings sub-fragment: $e", isDebug = false)
            false
        }
    }
}

@Composable
fun SettingsScreen(
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
            NavigationSection(strings = strings, isDark = isDark, onAction = onAction)
            SettingsFooter()
        }
    }
}

@Composable
private fun NavigationSection(
    strings: Strings,
    isDark: Boolean,
    onAction: ((String) -> Unit)?
) {
    val context: Context = LocalContext.current

    SettingsSectionTitle(title = strings.get("navigation_header", "Navigation"))
    ExpressiveSettingsGroup {
        SettingsItem(
            title = strings.get("appearance", "Appearance"),
            subtitle = strings.get("appearance_sub", "PackIt styling settings"),
            iconName = "msg_theme",
            iconColors = ExpressivePalette.categoryColors("appearance", isDark),
            shape = expressiveShapeFor(0, 2),
            onClick = {
                if (onAction != null) {
                    onAction("appearance")
                } else {
                    AppearanceActivity.open(context, strings.get("appearance", "Appearance"))
                }
            }
        )
        SettingsItem(
            title = strings.get("debug_menu", "Debug menu"),
            iconName = "msg_log",
            iconColors = ExpressivePalette.categoryColors("debug", isDark),
            shape = expressiveShapeFor(1, 2),
            onClick = {
                if (onAction != null) {
                    onAction("debug_menu")
                } else {
                    DebugActivity.open(context, strings.get("debug_menu", "Debug menu"))
                }
            }
        )
    }
}
