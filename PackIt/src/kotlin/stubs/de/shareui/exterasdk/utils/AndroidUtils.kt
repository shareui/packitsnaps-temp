package de.shareui.exterasdk.utils

object AndroidUtils {
    @JvmStatic
    fun runOnUIThread(runnable: () -> Unit) {}

    @JvmStatic
    fun runOnUIThread(delay: Long, runnable: () -> Unit) {}

    @JvmStatic
    fun log(message: String) {}
}
