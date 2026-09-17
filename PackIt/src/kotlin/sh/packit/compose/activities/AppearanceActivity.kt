package sh.packit.compose.activities

import android.content.Context
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import de.shareui.composeshell.TelegramColors
import de.shareui.composeshell.TelegramTheme
import de.shareui.composeshell.TelegramThemeBridge
import de.shareui.exterasdk.localization.Strings
import de.shareui.exterasdk.settings.PluginSettings
import de.shareui.exterasdk.ui.BulletinHelper
import org.json.JSONObject
import sh.packit.compose.components.ExpressivePalette
import sh.packit.compose.components.ExpressiveSettingsGroup
import sh.packit.compose.components.SettingsBottomSelector
import sh.packit.compose.components.SettingsDottedSlider
import sh.packit.compose.components.SettingsFooter
import sh.packit.compose.components.SettingsItem
import sh.packit.compose.components.SettingsSectionTitle
import sh.packit.compose.components.expressiveShapeFor
import sh.packit.compose.icons.size24dp.restartAlt
import sh.packit.compose.utils.FontHelper
import sh.packit.compose.utils.LoadedFont
import sh.packit.compose.utils.LocalMonochromeIcons
import sh.packit.compose.utils.ThemeHelper
import sh.packit.compose.utils.rememberScreenScrollState
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Logx
import java.lang.reflect.Method

object AppearanceActivity {
    @JvmStatic
    fun createView(context: Context, argsJson: String?): View {
        Logx.logx("AppearanceActivity.createView started", isDebug = true)
        val assetsDir: String? = try {
            if (argsJson != null) {
                val json = JSONObject(argsJson)
                val dir = json.optString("assets_dir", null)
                if (!dir.isNullOrEmpty()) CoreState.assetsDir = dir
                dir
            } else null
        } catch (_: Throwable) {
            null
        }

        return ComposeView(context).apply {
            setContent {
                sh.packit.compose.utils.PackItTheme(assetsDir = assetsDir) {
                    AppearanceScreen(assetsDir = assetsDir)
                }
            }
        }
    }

    @JvmStatic
    fun open(context: Context, title: String = "Appearance"): Boolean {
        Logx.logx("AppearanceActivity.open started: title=$title", isDebug = true)
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
            val result: Any? = callAttr.invoke(module, *arrayOf<Any?>("openAppearanceScreen", args))
            val success: Boolean = result?.toString() == "True" || result as? Boolean == true
            Logx.logx("openAppearanceScreen result: $success", isDebug = true)
            success
        } catch (e: Throwable) {
            Logx.logx("failed to open Appearance sub-fragment: $e", isDebug = false)
            false
        }
    }
}

@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier,
    assetsDir: String? = null,
    onAction: ((String) -> Unit)? = null
) {
    val strings: Strings = Strings.of(CoreState.PLUGIN_ID)
    val scrollState: ScrollState = rememberScreenScrollState("appearance")
    val isDark: Boolean = TelegramThemeBridge.isDark || isSystemInDarkTheme()

    val resolvedAssetsDir: String? = assetsDir ?: CoreState.assetsDir
    val fonts: List<LoadedFont> = remember(resolvedAssetsDir) {
        FontHelper.loadFonts(resolvedAssetsDir)
    }

    val savedFontName: String = remember {
        PluginSettings.getSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_NAME, FontHelper.DEFAULT_FONT_NAME)
    }
    var selectedFontName: String by remember { mutableStateOf(savedFontName) }

    val savedFontSize: Float = remember {
        val raw: Any? = PluginSettings.getSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_SIZE, FontHelper.DEFAULT_FONT_SIZE)
        when (raw) {
            is Number -> raw.toFloat()
            is String -> raw.toFloatOrNull() ?: FontHelper.DEFAULT_FONT_SIZE
            else -> FontHelper.DEFAULT_FONT_SIZE
        }
    }
    var currentFontSize: Float by remember { mutableFloatStateOf(savedFontSize) }

    val savedIconStyle: String = remember {
        PluginSettings.getSetting(CoreState.PLUGIN_ID, ThemeHelper.KEY_MONOCHROME_ICONS, ThemeHelper.DEFAULT_MONOCHROME_ICONS)
    }
    var selectedIconStyle: String by remember { mutableStateOf(savedIconStyle) }
    val isMonochrome: Boolean = remember(selectedIconStyle) {
        ThemeHelper.resolveIsMonochrome(selectedIconStyle)
    }

    val selectedFontFamily: FontFamily = remember(selectedFontName, fonts) {
        FontHelper.getFontFamily(selectedFontName, fonts)
    }
    val fontOptions: Map<String, String> = remember(fonts) {
        fonts.associate { it.name to it.displayName }
    }
    val fontFamilies: Map<String, FontFamily> = remember(fonts) {
        fonts.associate { it.name to it.fontFamily }
    }
    val iconStyleOptions: Map<String, String> = remember(strings) {
        mapOf(
            ThemeHelper.MODE_AUTO to strings.get("monochrome_auto", "Auto"),
            ThemeHelper.MODE_COLORED to strings.get("monochrome_colored", "Colored"),
            ThemeHelper.MODE_MONOCHROME to strings.get("monochrome_monochrome", "Monochrome")
        )
    }

    val customTypography = FontHelper.createTypography(
        base = MaterialTheme.typography,
        fontFamily = selectedFontFamily,
        scale = currentFontSize / FontHelper.DEFAULT_FONT_SIZE
    )

    CompositionLocalProvider(LocalMonochromeIcons provides isMonochrome) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = customTypography
        ) {
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
                    FontSection(
                        strings = strings,
                        isDark = isDark,
                        selectedFontName = selectedFontName,
                        currentFontSize = currentFontSize,
                        fontOptions = fontOptions,
                        fontFamilies = fontFamilies,
                        onFontChange = { newFont ->
                            selectedFontName = newFont
                            onAction?.invoke("font:$newFont")
                        },
                        onFontSizeChange = { newSize ->
                            currentFontSize = newSize
                            onAction?.invoke("fontSize:$newSize")
                        },
                        onReset = {
                            selectedFontName = FontHelper.DEFAULT_FONT_NAME
                            currentFontSize = FontHelper.DEFAULT_FONT_SIZE
                            PluginSettings.setSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_NAME, FontHelper.DEFAULT_FONT_NAME, reloadSettings = true)
                            PluginSettings.setSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_SIZE, FontHelper.DEFAULT_FONT_SIZE, reloadSettings = true)
                            BulletinHelper.showSuccess(strings.get("reset", "Reset"))
                            onAction?.invoke("reset")
                        }
                    )
                    MiscSection(
                        strings = strings,
                        isDark = isDark,
                        selectedIconStyle = selectedIconStyle,
                        iconStyleOptions = iconStyleOptions,
                        onIconStyleChange = { newStyle ->
                            selectedIconStyle = newStyle
                            onAction?.invoke("iconStyle:$newStyle")
                        }
                    )
                    SettingsFooter()
                }
            }
        }
    }
}

@Composable
private fun FontSection(
    strings: Strings,
    isDark: Boolean,
    selectedFontName: String,
    currentFontSize: Float,
    fontOptions: Map<String, String>,
    fontFamilies: Map<String, FontFamily>,
    onFontChange: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onReset: () -> Unit
) {
    SettingsSectionTitle(title = strings.get("font_header", "Font"))
    ExpressiveSettingsGroup {
        SettingsBottomSelector(
            title = strings.get("plugin_font", "Plugin font"),
            subtitle = strings.get("plugin_font_desc", "Select typeface for plugin UI"),
            settingKey = FontHelper.KEY_FONT_NAME,
            selectedKey = selectedFontName,
            defaultKey = FontHelper.DEFAULT_FONT_NAME,
            options = fontOptions,
            optionsFonts = fontFamilies,
            iconName = "msg_theme",
            iconColors = ExpressivePalette.categoryColors("appearance", isDark),
            shape = expressiveShapeFor(0, 3),
            onSelectionChanged = onFontChange
        )
        SettingsDottedSlider(
            title = strings.get("font_size", "Font size"),
            subtitle = strings.get("font_size_desc", "Adjust typography text size"),
            settingKey = FontHelper.KEY_FONT_SIZE,
            value = currentFontSize,
            defaultValue = FontHelper.DEFAULT_FONT_SIZE,
            valueRange = FontHelper.MIN_FONT_SIZE..FontHelper.MAX_FONT_SIZE,
            steps = FontHelper.FONT_STEPS,
            iconName = "msg_text",
            iconColors = ExpressivePalette.categoryColors("appearance", isDark),
            shape = expressiveShapeFor(1, 3),
            onValueChange = onFontSizeChange
        )
        SettingsItem(
            title = strings.get("reset", "Reset"),
            subtitle = strings.get("reset_font_desc", "Restore default font and size"),
            imageVector = restartAlt,
            iconColors = ExpressivePalette.categoryColors("updates", isDark),
            shape = expressiveShapeFor(2, 3),
            mini = true,
            showChevron = false,
            onClick = onReset
        )
    }
}

@Composable
private fun MiscSection(
    strings: Strings,
    isDark: Boolean,
    selectedIconStyle: String,
    iconStyleOptions: Map<String, String>,
    onIconStyleChange: (String) -> Unit
) {
    SettingsSectionTitle(title = strings.get("misc_header", "Misc"))
    ExpressiveSettingsGroup {
        SettingsBottomSelector(
            title = strings.get("monochrome_icons", "Monochrome icons"),
            subtitle = strings.get("monochrome_icons_desc", "Choose icon styling for settings items"),
            settingKey = ThemeHelper.KEY_MONOCHROME_ICONS,
            selectedKey = selectedIconStyle,
            defaultKey = ThemeHelper.DEFAULT_MONOCHROME_ICONS,
            options = iconStyleOptions,
            iconName = "msg_palette",
            iconColors = ExpressivePalette.categoryColors("appearance", isDark),
            shape = expressiveShapeFor(0, 1),
            onSelectionChanged = onIconStyleChange
        )
    }
}
