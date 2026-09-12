package sh.packit.core.state

object CoreState {
    const val PLUGIN_ID: String = "packit"
    var pluginVersion: String = "0.1.0"
    var isCoreInitialized: Boolean = false
    var isComposeAvailable: Boolean = false

    @JvmStatic
    fun markCoreReady() {
        isCoreInitialized = true
    }

    @JvmStatic
    fun markComposeReady() {
        isComposeAvailable = true
    }

    @JvmStatic
    fun isCoreReady(): Boolean = isCoreInitialized

    @JvmStatic
    fun isComposeReady(): Boolean = isComposeAvailable
}
