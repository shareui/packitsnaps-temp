package de.shareui.exterasdk.cruel

import java.io.File

object PluginPaths {
    fun getPluginsDir(): File {
        return File("/dummy")
    }

    fun getSourceDir(pluginId: String): File {
        return File(getPluginsDir(), "source/$pluginId")
    }

    fun getShareDir(pluginId: String): File {
        return File(getPluginsDir(), "share/$pluginId")
    }

    fun getWheelsDir(pluginId: String): File {
        return File(getPluginsDir(), "wheels/$pluginId")
    }

    fun getLocaleDir(pluginId: String): File {
        return File(getPluginsDir(), "strings/$pluginId")
    }

    fun getAssetsDir(pluginId: String): File {
        return File(getPluginsDir(), "assets/$pluginId")
    }

    fun assetsDir(pluginId: String): File = getAssetsDir(pluginId)

    fun sourceDir(pluginId: String): File = getSourceDir(pluginId)

    fun shareDir(pluginId: String): File = getShareDir(pluginId)

    fun wheelsDir(pluginId: String): File = getWheelsDir(pluginId)

    fun localeDir(pluginId: String): File = getLocaleDir(pluginId)

    fun pluginsDir(): File = getPluginsDir()
}
