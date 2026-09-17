package sh.packit.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.shareui.composeshell.TelegramColors
import sh.packit.compose.utils.LocalMonochromeIcons

object ExpressivePalette {
    @Composable
    fun categoryColors(key: String, isDark: Boolean): Pair<Color, Color> {
        val isMonochrome: Boolean = LocalMonochromeIcons.current
        return categoryColors(key, isDark, isMonochrome)
    }

    fun categoryColors(key: String, isDark: Boolean, isMonochrome: Boolean): Pair<Color, Color> {
        if (isMonochrome) {
            return monochromeColors()
        }
        return if (isDark) {
            when (key) {
                "plugins" -> Color(0xFF004A77) to Color(0xFFC2E7FF)
                "icons" -> Color(0xFF633B48) to Color(0xFFFFD8EC)
                "updates" -> Color(0xFF324F34) to Color(0xFFCBEFD0)
                "deeplinks" -> Color(0xFF004D61) to Color(0xFFACEFEE)
                "repositories" -> Color(0xFF6E4E13) to Color(0xFFFFDEAC)
                "profile" -> Color(0xFF7D5260) to Color(0xFFFFD8E4)
                "utilities" -> Color(0xFF3B4869) to Color(0xFFD9E2FF)
                "settings" -> Color(0xFF3F474D) to Color(0xFFDEE3EB)
                "appearance" -> Color(0xFF4A3968) to Color(0xFFEADBFF)
                "debug" -> Color(0xFF5E3B33) to Color(0xFFFFDAD4)
                else -> Color(0xFF004A77) to Color(0xFFC2E7FF)
            }
        } else {
            when (key) {
                "plugins" -> Color(0xFFD7E3FF) to Color(0xFF005AC1)
                "icons" -> Color(0xFFFFD8EC) to Color(0xFF631B4B)
                "updates" -> Color(0xFFCBEFD0) to Color(0xFF042106)
                "deeplinks" -> Color(0xFFACEFEE) to Color(0xFF002022)
                "repositories" -> Color(0xFFFFDEAC) to Color(0xFF281900)
                "profile" -> Color(0xFFFFD8E4) to Color(0xFF631835)
                "utilities" -> Color(0xFFD9E2FF) to Color(0xFF27304E)
                "settings" -> Color(0xFFEFF1F7) to Color(0xFF44474F)
                "appearance" -> Color(0xFFEADBFF) to Color(0xFF4A3968)
                "debug" -> Color(0xFFFFDAD4) to Color(0xFF5E3B33)
                else -> Color(0xFFD7E3FF) to Color(0xFF005AC1)
            }
        }
    }

    fun monochromeColors(): Pair<Color, Color> {
        val container: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG.takeIf { it != Color.Unspecified }
            ?: TelegramColors.windowBackgroundWhiteGrayIcon.copy(alpha = 0.12f)
        val tint: Color = TelegramColors.windowBackgroundWhiteGrayIcon.takeIf { it != Color.Unspecified }
            ?: Color(0xFF828E99)
        return container to tint
    }
}
