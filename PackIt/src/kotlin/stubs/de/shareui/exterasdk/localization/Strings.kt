package de.shareui.exterasdk.localization

class Strings private constructor(val pluginId: String) {
    operator fun get(key: String): String = key
    fun get(key: String, default: String): String = default
    fun getCurrentLanguage(): String = "en"
    fun setLocale(locale: String?, reloadPlugin: Boolean = false) {}

    companion object {
        const val KEY_LANGUAGE: String = "language"
        const val ELYX_ID: String = "elyxcore"

        @JvmStatic
        fun of(pluginId: String): Strings = Strings(pluginId)

        @JvmStatic
        @JvmOverloads
        fun setLocale(pluginId: String, locale: String?, reloadPlugin: Boolean = false) {}

        @JvmStatic
        fun getCurrentLanguage(pluginId: String? = null): String = "en"
    }
}
