package sh.packit.core.ui

import android.content.Context
import android.content.Intent
import android.os.Process
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.ui.Components.BulletinFactory
import sh.packit.core.utils.Logx
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object RestartRequired {
    private fun restartApp() {
        val context: Context = ApplicationLoader.applicationContext ?: return
        try {
            val packageManager = context.packageManager
            val intent: Intent? = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent?.component
            if (componentName != null) {
                val restartIntent = Intent.makeRestartActivityTask(componentName)
                restartIntent.setPackage(context.packageName)
                context.startActivity(restartIntent)
            }
        } catch (e: Throwable) {
            Logx.logx("restartApp launch intent error: $e", isDebug = false)
        }
        thread(isDaemon = true) {
            try {
                Thread.sleep(300L)
                Process.killProcess(Process.myPid())
            } catch (e: Throwable) {
                Logx.logx("restartApp killProcess error: $e", isDebug = false)
                exitProcess(0)
            }
        }
    }

    private fun getInfoIconId(): Int {
        return try {
            val rRawCls: Class<*> = Class.forName("org.telegram.messenger.R\$raw")
            val field = rRawCls.getField("info")
            field.getInt(null)
        } catch (e: Throwable) {
            Logx.logx("failed to resolve R.raw.info: $e", isDebug = true)
            0
        }
    }

    private fun resolveString(key: String, fallback: String): String {
        return try {
            val stringsCls: Class<*> = Class.forName("de.shareui.exterasdk.localization.Strings")
            val ofMethod = stringsCls.getMethod("of", String::class.java)
            val stringsObj = ofMethod.invoke(null, "packit")
            val getMethod = stringsCls.getMethod("get", String::class.java, String::class.java)
            getMethod.invoke(stringsObj, key, fallback) as? String ?: fallback
        } catch (e: Throwable) {
            Logx.logx("resolveString notice: $e", isDebug = true)
            fallback
        }
    }

    @JvmStatic
    @JvmOverloads
    fun show(
        text: String = resolveString("restart_required", "Client restart required"),
        buttonText: String = resolveString("restart_button", "Restart"),
        delayMs: Long = 1200L
    ) {
        AndroidUtilities.runOnUIThread({
            try {
                val factory: BulletinFactory = BulletinFactory.global()
                val iconId: Int = getInfoIconId()
                factory.createSimpleBulletin(
                    iconId,
                    text,
                    buttonText,
                    5000,
                    Runnable { restartApp() }
                ).show()
                Logx.logx("RestartRequired bulletin displayed", isDebug = true)
            } catch (e: Throwable) {
                Logx.logx("RestartRequired show error: $e", isDebug = false)
            }
        }, delayMs)
    }
}
