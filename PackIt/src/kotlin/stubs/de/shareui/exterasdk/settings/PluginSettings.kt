package de.shareui.exterasdk.settings

object PluginSettings {
    fun <T> getSetting(pluginId: String, key: String, default: T): T = default

    fun setSetting(pluginId: String, key: String, value: Any?, reloadSettings: Boolean = false) {}

    fun reloadSettings(pluginId: String) {}

    fun getSettings(pluginId: String): Map<String, Any?> = emptyMap()

    fun importSettings(pluginId: String, settings: Map<String, Any?>, reloadSettings: Boolean = true) {}

    fun clearSettings(pluginId: String) {}
}
