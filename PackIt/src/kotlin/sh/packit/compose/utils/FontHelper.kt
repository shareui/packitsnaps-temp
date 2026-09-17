package sh.packit.compose.utils

import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import de.shareui.composeshell.TelegramTheme
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Paths
import java.io.File

data class LoadedFont(
    val name: String,
    val displayName: String,
    val fontFamily: FontFamily
)

object FontHelper {
    const val KEY_FONT_NAME: String = "plugin_font"
    const val KEY_FONT_SIZE: String = "plugin_font_size"
    const val DEFAULT_FONT_NAME: String = "Default"
    const val DEFAULT_FONT_SIZE: Float = 16f
    const val MIN_FONT_SIZE: Float = 8f
    const val MAX_FONT_SIZE: Float = 24f
    const val FONT_STEPS: Int = 15

    fun formatDisplayName(rawName: String): String {
        return when (rawName) {
            "Default" -> "Default"
            "DMSans" -> "DM Sans"
            "GoogleSansFlex_9pt" -> "Google Sans Flex"
            "PlusJakartaSans" -> "Plus Jakarta Sans"
            "Quicksand" -> "Quicksand"
            "Sora" -> "Sora"
            else -> rawName.replace("_9pt", "").replace("([a-z])([A-Z])".toRegex(), "$1 $2")
        }
    }

    fun resolveAssetsDir(explicitDir: String?): String? {
        return Paths.getAssetsDir(CoreState.PLUGIN_ID, explicitDir)?.absolutePath
    }

    fun loadFonts(assetsDir: String?): List<LoadedFont> {
        val list = mutableListOf<LoadedFont>()
        list.add(LoadedFont(DEFAULT_FONT_NAME, "Default", FontFamily.Default))
        val fontsDir: File? = Paths.getFontsDir(CoreState.PLUGIN_ID, assetsDir)
        if (fontsDir != null && fontsDir.exists() && fontsDir.isDirectory) {
            fontsDir.listFiles()?.filter { it.name.contains("Regular") }?.forEach { file ->
                val rawName = file.name.substringBefore("-").replace(".ttf", "")
                val family = loadFontFamilyFromFile(file)
                if (family != null) {
                    list.add(LoadedFont(rawName, formatDisplayName(rawName), family))
                }
            }
        }
        return list.distinctBy { it.name }.sortedWith { a, b ->
            if (a.name == DEFAULT_FONT_NAME) -1 else if (b.name == DEFAULT_FONT_NAME) 1 else a.displayName.compareTo(b.displayName)
        }
    }

    fun getFontFamily(name: String, fonts: List<LoadedFont>): FontFamily {
        return fonts.find { it.name == name }?.fontFamily ?: FontFamily.Default
    }

    fun createTypography(base: Typography, fontFamily: FontFamily, scale: Float): Typography {
        val boundedScale = scale.coerceIn(0.5f, 1.5f)
        return base.copy(
            headlineLarge = base.headlineLarge.copy(fontFamily = fontFamily, fontSize = base.headlineLarge.fontSize * boundedScale),
            headlineMedium = base.headlineMedium.copy(fontFamily = fontFamily, fontSize = base.headlineMedium.fontSize * boundedScale),
            headlineSmall = base.headlineSmall.copy(fontFamily = fontFamily, fontSize = base.headlineSmall.fontSize * boundedScale),
            titleLarge = base.titleLarge.copy(fontFamily = fontFamily, fontSize = base.titleLarge.fontSize * boundedScale),
            titleMedium = base.titleMedium.copy(fontFamily = fontFamily, fontSize = base.titleMedium.fontSize * boundedScale),
            titleSmall = base.titleSmall.copy(fontFamily = fontFamily, fontSize = base.titleSmall.fontSize * boundedScale),
            bodyLarge = base.bodyLarge.copy(fontFamily = fontFamily, fontSize = base.bodyLarge.fontSize * boundedScale),
            bodyMedium = base.bodyMedium.copy(fontFamily = fontFamily, fontSize = base.bodyMedium.fontSize * boundedScale),
            bodySmall = base.bodySmall.copy(fontFamily = fontFamily, fontSize = base.bodySmall.fontSize * boundedScale),
            labelLarge = base.labelLarge.copy(fontFamily = fontFamily, fontSize = base.labelLarge.fontSize * boundedScale),
            labelMedium = base.labelMedium.copy(fontFamily = fontFamily, fontSize = base.labelMedium.fontSize * boundedScale),
            labelSmall = base.labelSmall.copy(fontFamily = fontFamily, fontSize = base.labelSmall.fontSize * boundedScale)
        )
    }

    private fun loadFontFamilyFromFile(file: File): FontFamily? {
        return try {
            val tf = Typeface.createFromFile(file)
            FontFamily(androidx.compose.ui.text.font.Typeface(tf))
        } catch (_: Throwable) {
            null
        }
    }
}

@Composable
fun PackItTheme(
    assetsDir: String? = null,
    content: @Composable () -> Unit
) {
    val resolvedAssetsDir: String? = FontHelper.resolveAssetsDir(assetsDir)
    val fonts: List<LoadedFont> = remember(resolvedAssetsDir) {
        FontHelper.loadFonts(resolvedAssetsDir)
    }
    val savedFontName: String = remember {
        PluginSettings.getSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_NAME, FontHelper.DEFAULT_FONT_NAME)
    }
    val savedFontSize: Float = remember {
        val raw: Any? = PluginSettings.getSetting(CoreState.PLUGIN_ID, FontHelper.KEY_FONT_SIZE, FontHelper.DEFAULT_FONT_SIZE)
        when (raw) {
            is Number -> raw.toFloat()
            is String -> raw.toFloatOrNull() ?: FontHelper.DEFAULT_FONT_SIZE
            else -> FontHelper.DEFAULT_FONT_SIZE
        }
    }
    val fontFamily: FontFamily = remember(savedFontName, fonts) {
        FontHelper.getFontFamily(savedFontName, fonts)
    }

    TelegramTheme {
        val baseTypography: Typography = MaterialTheme.typography
        val customTypography = remember(baseTypography, fontFamily, savedFontSize) {
            FontHelper.createTypography(
                base = baseTypography,
                fontFamily = fontFamily,
                scale = savedFontSize / FontHelper.DEFAULT_FONT_SIZE
            )
        }
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = customTypography,
            content = content
        )
    }
}
