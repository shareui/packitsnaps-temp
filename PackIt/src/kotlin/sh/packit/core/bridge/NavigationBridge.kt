package sh.packit.core.bridge

import android.content.Context
import sh.packit.core.utils.OpenUrl

object NavigationBridge {
    const val URL_CHANNEL: String = "https://t.me/shareui"
    const val URL_FORUM: String = "https://t.me/packitGround"
    const val URL_SOURCE: String = "https://github.com/shareui/packit-source"

    @JvmStatic
    fun openUrl(context: Context, url: String) {
        OpenUrl.openUrlInApp(context, url)
    }
}
