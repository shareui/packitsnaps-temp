package de.shareui.composeshell

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TelegramThemeBridge {
    private val themeClass by lazy {
        try {
            Class.forName("org.telegram.ui.ActionBar.Theme")
        } catch (_: Throwable) {
            null
        }
    }
    private val getColorMethod by lazy {
        try {
            themeClass?.getMethod("getColor", Int::class.javaPrimitiveType)
        } catch (_: Throwable) {
            null
        }
    }
    private val isCurrentThemeDarkMethod by lazy {
        try {
            themeClass?.getMethod("isCurrentThemeDark")
        } catch (_: Throwable) {
            null
        }
    }
    private val keysCache = mutableMapOf<String, Int>()

    fun getKey(name: String): Int {
        return keysCache.getOrPut(name) {
            try {
                themeClass?.getField(name)?.getInt(null) ?: -1
            } catch (_: Throwable) {
                -1
            }
        }
    }

    fun getColor(keyName: String, fallback: Color = Color.Unspecified): Color {
        val key = getKey(keyName)
        if (key < 0) return fallback
        return try {
            val colorInt = (getColorMethod?.invoke(null, key) as? Int) ?: return fallback
            Color(colorInt)
        } catch (_: Throwable) {
            fallback
        }
    }

    val isDark: Boolean
        get() = try {
            (isCurrentThemeDarkMethod?.invoke(null) as? Boolean) ?: false
        } catch (_: Throwable) {
            false
        }
}

object TelegramColors {
    val windowBackgroundWhite: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhite")
    val windowBackgroundGray: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundGray")
    val windowBackgroundWhiteBlackText: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteBlackText")
    val windowBackgroundWhiteGrayText: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteGrayText")
    val windowBackgroundWhiteGrayText2: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteGrayText2")
    val windowBackgroundWhiteBlueHeader: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteBlueHeader")
    val windowBackgroundWhiteBlueText: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteBlueText")
    val windowBackgroundWhiteBlueIcon: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteBlueIcon")
    val windowBackgroundWhiteGrayIcon: Color get() = TelegramThemeBridge.getColor("key_windowBackgroundWhiteGrayIcon")
    val divider: Color get() = TelegramThemeBridge.getColor("key_divider")
    val listSelector: Color get() = TelegramThemeBridge.getColor("key_listSelector")

    val switchTrack: Color get() = TelegramThemeBridge.getColor("key_switchTrack")
    val switchTrackChecked: Color get() = TelegramThemeBridge.getColor("key_switchTrackChecked")
    val switchThumb: Color get() = TelegramThemeBridge.getColor("key_switchTrackBlueThumb")
    val switchThumbChecked: Color get() = TelegramThemeBridge.getColor("key_switchTrackBlueThumbChecked")

    val DEFAULT_PLUGINSETTINGS_BG: Color get() = windowBackgroundGray
    val DEFAULT_PLUGINSETTINGS_CELL_BG: Color get() = windowBackgroundWhite
    val DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT: Color get() = windowBackgroundWhiteBlackText
    val DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT: Color get() = windowBackgroundWhiteGrayText2
    val DEFAULT_PLUGINSETTINGS_DIVIDER: Color get() = divider
}

private fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
    val inverse = 1f - ratio
    return Color(
        red = color1.red * inverse + color2.red * ratio,
        green = color1.green * inverse + color2.green * ratio,
        blue = color1.blue * inverse + color2.blue * ratio,
        alpha = 1f
    )
}

private fun darkerTone(color: Color, factor: Float = 0.22f): Color {
    return Color(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor,
        alpha = 1f
    )
}

@Composable
fun TelegramTheme(
    darkTheme: Boolean = TelegramThemeBridge.isDark || isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val bg: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_BG.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFF0E1621) else Color(0xFFF0F2F5)
    val surface: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFF1C242F) else Color(0xFFFFFFFF)
    val onSurface: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFFFFFFFF) else Color(0xFF222222)
    val onSurfaceVariant: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFF828E99) else Color(0xFF808692)
    val dividerColor: Color = TelegramColors.DEFAULT_PLUGINSETTINGS_DIVIDER.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFF2B3542) else Color(0xFFE0E0E0)

    val primaryColor: Color = TelegramColors.windowBackgroundWhiteBlueText.takeIf { it != Color.Unspecified }
        ?: if (darkTheme) Color(0xFF70B4E6) else Color(0xFF2481CC)

    val surfaceVariant: Color = blendColors(surface, onSurface, if (darkTheme) 0.12f else 0.08f)
    val primaryLuminance: Float = 0.2126f * primaryColor.red + 0.7152f * primaryColor.green + 0.0722f * primaryColor.blue
    val onPrimaryColor: Color = if (darkTheme && primaryLuminance > 0.35f) {
        darkerTone(primaryColor, factor = 0.22f)
    } else {
        Color.White
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            background = bg,
            surface = surface,
            surfaceVariant = surfaceVariant,
            onBackground = onSurface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = blendColors(surface, primaryColor, 0.25f),
            onPrimaryContainer = primaryColor,
            secondary = primaryColor,
            secondaryContainer = blendColors(surface, primaryColor, 0.25f),
            onSecondary = onPrimaryColor,
            onSecondaryContainer = primaryColor,
            outline = dividerColor
        )
    } else {
        lightColorScheme(
            background = bg,
            surface = surface,
            surfaceVariant = surfaceVariant,
            onBackground = onSurface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = blendColors(surface, primaryColor, 0.15f),
            onPrimaryContainer = primaryColor,
            secondary = primaryColor,
            secondaryContainer = blendColors(surface, primaryColor, 0.15f),
            onSecondary = onPrimaryColor,
            onSecondaryContainer = primaryColor,
            outline = dividerColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
