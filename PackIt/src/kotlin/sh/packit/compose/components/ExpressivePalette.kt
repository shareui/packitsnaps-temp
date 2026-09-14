package sh.packit.compose.components

import androidx.compose.ui.graphics.Color

object ExpressivePalette {
    fun categoryColors(key: String, isDark: Boolean): Pair<Color, Color> {
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
                else -> Color(0xFFD7E3FF) to Color(0xFF005AC1)
            }
        }
    }
}
