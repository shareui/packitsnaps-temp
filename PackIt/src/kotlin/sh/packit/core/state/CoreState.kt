package sh.packit.core.state

import de.shareui.exterasdk.metadata.Metadata

object CoreState {
    const val PLUGIN_ID: String = "packit"
    private var customPluginVersion: String? = null

    @JvmStatic
    var pluginVersion: String
        get() = customPluginVersion ?: try {
            Metadata.of(PLUGIN_ID).version
        } catch (_: Throwable) {
            "0.1.0"
        }
        set(value) {
            customPluginVersion = value
        }

    var isCoreInitialized: Boolean = false
    var isComposeAvailable: Boolean = false

    @JvmStatic
    fun markCoreReady() { isCoreInitialized = true }

    @JvmStatic
    fun markComposeReady() { isComposeAvailable = true }

    @JvmStatic
    fun isCoreReady(): Boolean = isCoreInitialized

    @JvmStatic
    fun isComposeReady(): Boolean = isComposeAvailable
}

