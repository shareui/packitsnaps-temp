package sh.packit.compose.utils

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import java.lang.reflect.Method

val LocalMonochromeIcons: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

object ThemeHelper {
    const val KEY_MONOCHROME_ICONS: String = "monochrome_icons"
    const val DEFAULT_MONOCHROME_ICONS: String = "auto"

    const val MODE_AUTO: String = "auto"
    const val MODE_COLORED: String = "colored"
    const val MODE_MONOCHROME: String = "monochrome"

    private val themeClass: Class<*>? by lazy {
        try {
            Class.forName("org.telegram.ui.ActionBar.Theme")
        } catch (_: Throwable) {
            null
        }
    }

    private val isCurrentThemeMonetMethod: Method? by lazy {
        try {
            themeClass?.getMethod("isCurrentThemeMonet")
        } catch (_: Throwable) {
            null
        }
    }

    private val isCurrentAccentMonetMethod: Method? by lazy {
        try {
            themeClass?.getMethod("isCurrentAccentMonet")
        } catch (_: Throwable) {
            null
        }
    }

    @JvmStatic
    fun isMonet(): Boolean {
        return try {
            val themeMonet: Boolean = (isCurrentThemeMonetMethod?.invoke(null) as? Boolean) == true
            if (themeMonet) return true
            val accentMonet: Boolean = (isCurrentAccentMonetMethod?.invoke(null) as? Boolean) == true
            accentMonet
        } catch (_: Throwable) {
            false
        }
    }

    @JvmStatic
    fun resolveIsMonochrome(mode: String): Boolean {
        return when (mode) {
            MODE_MONOCHROME -> true
            MODE_COLORED -> false
            else -> isMonet()
        }
    }
}
