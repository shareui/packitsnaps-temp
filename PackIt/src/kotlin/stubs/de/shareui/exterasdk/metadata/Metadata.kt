package de.shareui.exterasdk.metadata

data class Metadata(
    val id: String,
    val title: String,
    val version: String,
    val versionNum: Long,
    val author: String,
    val appVersion: String,
    val sdkVersion: String,
    val priority: String,
    val buildHash: String,
    val isRelease: Boolean,
    val extra: Map<String, String>,
    val entry: String,
    val pysrc: String,
    val descriptions: Map<String, String>,
    val raw: Map<String, Any?>
) {
    operator fun get(key: String): String? = raw[key]?.toString()

    fun description(locale: String? = null): String = ""

    companion object {
        @JvmStatic
        fun of(pluginId: String): Metadata = Metadata(
            id = pluginId,
            title = pluginId,
            version = "0.1.0",
            versionNum = 0L,
            author = "",
            appVersion = "",
            sdkVersion = "",
            priority = "app",
            buildHash = "",
            isRelease = false,
            extra = emptyMap(),
            entry = "",
            pysrc = "",
            descriptions = emptyMap(),
            raw = emptyMap()
        )

        @JvmStatic
        fun clearCache(pluginId: String? = null) {}
    }
}
