package sh.packit.core.bridge

import android.content.Context
import android.content.Intent
import android.net.Uri
import sh.packit.core.utils.Logx

object NavigationBridge {
    const val URL_CHANNEL: String = "https://t.me/shareui"
    const val URL_FORUM: String = "https://t.me/packitGround"
    const val URL_SOURCE: String = "https://github.com/shareui/packit-source"

    @JvmStatic
    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Logx.logx("Opened URL: $url", isDebug = true)
        } catch (e: Throwable) {
            Logx.logx("Failed to open URL $url: $e", isDebug = false)
        }
    }
}
