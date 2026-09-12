package de.shareui.exterasdk.localization

class Strings private constructor(val pluginId: String) {
    operator fun get(key: String): String = key
    fun get(key: String, default: String): String = default

    companion object {
        @JvmStatic
        fun of(pluginId: String): Strings = Strings(pluginId)
    }
}
