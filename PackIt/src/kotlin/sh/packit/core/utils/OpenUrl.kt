package sh.packit.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.telegram.messenger.browser.Browser

object OpenUrl {
    @JvmStatic
    fun openUrlInApp(context: Context, url: String) {
        try {
            Browser.openUrl(context, url)
            Logx.logx("Opened URL in app: $url", isDebug = true)
        } catch (e: Throwable) {
            Logx.logx("Browser.openUrl failed for $url: $e", isDebug = false)
            openUrlInBrowser(context, url)
        }
    }

    @JvmStatic
    fun openUrlInBrowser(context: Context, url: String) {
        try {
            Browser.openUrlInSystemBrowser(context, url)
            Logx.logx("Opened URL in system browser: $url", isDebug = true)
        } catch (e: Throwable) {
            Logx.logx("Browser.openUrlInSystemBrowser failed for $url: $e", isDebug = false)
            try {
                val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                Logx.logx("Opened URL via Intent: $url", isDebug = true)
            } catch (err: Throwable) {
                Logx.logx("Intent openUrl failed for $url: $err", isDebug = false)
            }
        }
    }
}

fun openUrlInApp(context: Context, url: String) {
    OpenUrl.openUrlInApp(context, url)
}

fun openUrlInBrowser(context: Context, url: String) {
    OpenUrl.openUrlInBrowser(context, url)
}
