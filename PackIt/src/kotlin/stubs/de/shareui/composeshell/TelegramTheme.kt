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
    val switchThumb: Color get() = TelegramThemeBridge.getColor("key_switchThumb")
    val switchThumbChecked: Color get() = TelegramThemeBridge.getColor("key_switchThumbChecked")

    val DEFAULT_PLUGINSETTINGS_BG: Color get() = windowBackgroundGray
    val DEFAULT_PLUGINSETTINGS_CELL_BG: Color get() = windowBackgroundWhite
    val DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT: Color get() = windowBackgroundWhiteBlackText
    val DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT: Color get() = windowBackgroundWhiteGrayText2
    val DEFAULT_PLUGINSETTINGS_DIVIDER: Color get() = divider
}

@Composable
fun TelegramTheme(
    darkTheme: Boolean = TelegramThemeBridge.isDark || isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val primaryColor = TelegramColors.windowBackgroundWhiteBlueText
    val onPrimaryColor = Color.White

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            background = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
            surface = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
            surfaceVariant = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
            onBackground = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            onSurface = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            onSurfaceVariant = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = primaryColor,
            onPrimaryContainer = onPrimaryColor,
            secondary = primaryColor,
            secondaryContainer = primaryColor,
            onSecondary = onPrimaryColor,
            onSecondaryContainer = onPrimaryColor,
            outline = TelegramColors.DEFAULT_PLUGINSETTINGS_DIVIDER
        )
    } else {
        lightColorScheme(
            background = TelegramColors.DEFAULT_PLUGINSETTINGS_BG,
            surface = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
            surfaceVariant = TelegramColors.DEFAULT_PLUGINSETTINGS_CELL_BG,
            onBackground = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            onSurface = TelegramColors.DEFAULT_PLUGINSETTINGS_PRIMARY_TEXT,
            onSurfaceVariant = TelegramColors.DEFAULT_PLUGINSETTINGS_SECONDARY_TEXT,
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = primaryColor,
            onPrimaryContainer = onPrimaryColor,
            secondary = primaryColor,
            secondaryContainer = primaryColor,
            onSecondary = onPrimaryColor,
            onSecondaryContainer = onPrimaryColor,
            outline = TelegramColors.DEFAULT_PLUGINSETTINGS_DIVIDER
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
