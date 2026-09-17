package sh.packit.compose.utils

import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import de.shareui.composeshell.TelegramTheme
import de.shareui.exterasdk.settings.PluginSettings
import sh.packit.compose.components.SelectorOption
import sh.packit.compose.components.SelectorSubOption
import sh.packit.core.state.CoreState
import sh.packit.core.utils.Paths
import java.io.File

data class LoadedFont(
    val name: String,
    val displayName: String,
    val fontFamily: FontFamily,
    val familyName: String = name,
    val weight: String = "Regular"
)

object FontHelper {
    const val KEY_FONT_NAME: String = "plugin_font"
    const val KEY_FONT_SIZE: String = "plugin_font_size"
    const val DEFAULT_FONT_NAME: String = "Default"
    const val DEFAULT_FONT_SIZE: Float = 16f
    const val MIN_FONT_SIZE: Float = 8f
    const val MAX_FONT_SIZE: Float = 24f
    const val FONT_STEPS: Int = 15

    private val WEIGHT_ORDER: List<String> = listOf(
        "Thin", "ExtraLight", "Light", "Regular", "Medium", "SemiBold", "Bold", "ExtraBold", "Black"
    )

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

    fun formatWeightDisplayName(weight: String): String {
        return when (weight) {
            "ExtraLight" -> "Extra Light"
            "SemiBold" -> "Semi Bold"
            "ExtraBold" -> "Extra Bold"
            else -> weight
        }
    }

    private fun weightIndex(weight: String): Int {
        val idx = WEIGHT_ORDER.indexOf(weight)
        return if (idx >= 0) idx else 99
    }

    fun resolveAssetsDir(explicitDir: String?): String? {
        return Paths.getAssetsDir(CoreState.PLUGIN_ID, explicitDir)?.absolutePath
    }

    fun loadFonts(assetsDir: String?): List<LoadedFont> {
        val list = mutableListOf<LoadedFont>()
        list.add(LoadedFont(
            name = DEFAULT_FONT_NAME,
            displayName = "Default",
            fontFamily = FontFamily.Default,
            familyName = DEFAULT_FONT_NAME,
            weight = "Regular"
        ))
        val fontsDir: File? = Paths.getFontsDir(CoreState.PLUGIN_ID, assetsDir)
        if (fontsDir != null && fontsDir.exists() && fontsDir.isDirectory) {
            fontsDir.listFiles()?.filter { it.name.endsWith(".ttf") }?.forEach { file ->
                val raw = file.name.removeSuffix(".ttf")
                val rawFamily = raw.substringBefore("-")
                val weight = if (raw.contains("-")) raw.substringAfter("-") else "Regular"
                val family = loadFontFamilyFromFile(file)
                if (family != null) {
                    val displayName = "${formatDisplayName(rawFamily)} ${formatWeightDisplayName(weight)}"
                    list.add(LoadedFont(
                        name = raw,
                        displayName = displayName,
                        fontFamily = family,
                        familyName = rawFamily,
                        weight = weight
                    ))
                }
            }
        }
        return list.distinctBy { it.name }
    }

    fun buildSelectorOptions(fonts: List<LoadedFont>): List<SelectorOption> {
        val result = mutableListOf<SelectorOption>()
        result.add(
            SelectorOption(
                key = DEFAULT_FONT_NAME,
                label = "Default",
                font = FontFamily.Default,
                expandable = false,
                subOptions = emptyList()
            )
        )

        val fontFamilies = fonts
            .filter { it.name != DEFAULT_FONT_NAME }
            .groupBy { it.familyName }
            .toList()
            .sortedBy { (familyKey, _) -> formatDisplayName(familyKey) }

        for ((familyKey, familyFonts) in fontFamilies) {
            val sortedVars = familyFonts.sortedBy { weightIndex(it.weight) }
            val subOptions = sortedVars.map { font ->
                SelectorSubOption(
                    key = font.name,
                    label = formatWeightDisplayName(font.weight),
                    font = font.fontFamily
                )
            }
            val familyFont = familyFonts.find { it.weight == "Regular" }?.fontFamily
                ?: familyFonts.first().fontFamily

            result.add(
                SelectorOption(
                    key = familyKey,
                    label = formatDisplayName(familyKey),
                    font = familyFont,
                    expandable = true,
                    subOptions = subOptions
                )
            )
        }
        return result
    }

    fun getFontFamily(name: String, fonts: List<LoadedFont>): FontFamily {
        if (name == DEFAULT_FONT_NAME) return FontFamily.Default
        val exact = fonts.find { it.name == name }
        if (exact != null) return exact.fontFamily
        val byFamily = fonts.find { it.familyName == name && it.weight == "Regular" }
            ?: fonts.find { it.familyName == name }
        return byFamily?.fontFamily ?: FontFamily.Default
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
    val savedIconStyle: String = remember {
        PluginSettings.getSetting(CoreState.PLUGIN_ID, ThemeHelper.KEY_MONOCHROME_ICONS, ThemeHelper.DEFAULT_MONOCHROME_ICONS)
    }
    val isMonochrome: Boolean = remember(savedIconStyle) {
        ThemeHelper.resolveIsMonochrome(savedIconStyle)
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
        CompositionLocalProvider(LocalMonochromeIcons provides isMonochrome) {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme,
                typography = customTypography,
                content = content
            )
        }
    }
}
