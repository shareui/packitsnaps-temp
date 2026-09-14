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
        val context: Context = ApplicationLoader.applicationContext ?: run {
            Logx.logx("restartApp context is null", isDebug = false)
            return
        }
        try {
            val packageManager = context.packageManager
            val intent: Intent? = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent?.component
            if (componentName != null) {
                val restartIntent = Intent.makeRestartActivityTask(componentName)
                restartIntent.setPackage(context.packageName)
                context.startActivity(restartIntent)
            } else {
                Logx.logx("restartApp componentName is null", isDebug = false)
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
            Logx.logx("failed to resolve R.raw.info: $e", isDebug = false)
            0
        }
    }

    private fun resolveString(key: String, fallback: String): String {
        return try {
            de.shareui.exterasdk.localization.Strings.of("packit").get(key, fallback)
        } catch (e: Throwable) {
            Logx.logx("resolveString notice: $e", isDebug = false)
            fallback
        }
    }

    private fun getActiveFragment(): org.telegram.ui.ActionBar.BaseFragment? {
        return try {
            val cls = Class.forName("org.telegram.ui.LaunchActivity")
            val getSafe = cls.getMethod("getSafeLastFragment")
            val safe = getSafe.invoke(null) as? org.telegram.ui.ActionBar.BaseFragment
            safe ?: (cls.getMethod("getLastFragment").invoke(null) as? org.telegram.ui.ActionBar.BaseFragment)
        } catch (e: Throwable) {
            Logx.logx("getActiveFragment error: $e", isDebug = false)
            null
        }
    }

    private fun scheduleShow(text: String, buttonText: String, delayMs: Long, attempt: Int) {
        AndroidUtilities.runOnUIThread({
            try {
                val fragment: org.telegram.ui.ActionBar.BaseFragment? = getActiveFragment()
                val hasVisibleDialog: Boolean = fragment?.visibleDialog != null
                val factory: BulletinFactory = if (fragment != null && !hasVisibleDialog && BulletinFactory.canShowBulletin(fragment)) {
                    BulletinFactory.of(fragment)
                } else {
                    BulletinFactory.global()
                }

                val iconId: Int = getInfoIconId()
                val bulletin = factory.createSimpleBulletin(
                    iconId,
                    text,
                    buttonText,
                    5000,
                    Runnable { restartApp() }
                )
                if (bulletin != null) {
                    bulletin.show()
                    Logx.logx("RestartRequired bulletin displayed successfully", isDebug = false)
                    return@runOnUIThread
                }

                if (attempt < 15) {
                    Logx.logx("RestartRequired bulletin is null on attempt $attempt, retrying", isDebug = false)
                    scheduleShow(text, buttonText, 400L, attempt + 1)
                } else {
                    Logx.logx("RestartRequired failed after 15 attempts: bulletin null", isDebug = false)
                }
            } catch (e: Throwable) {
                Logx.logx("RestartRequired show error on attempt $attempt: $e", isDebug = false)
                if (attempt < 15) {
                    scheduleShow(text, buttonText, 400L, attempt + 1)
                } else {
                    Logx.logx("RestartRequired retries exhausted with error: $e", isDebug = false)
                }
            }
        }, delayMs)
    }

    @JvmStatic
    fun restart() {
        restartApp()
    }

    @JvmStatic
    @JvmOverloads
    fun show(
        text: String = resolveString("restart_required", "Client restart required"),
        buttonText: String = resolveString("restart_button", "Restart"),
        delayMs: Long = 1200L
    ) {
        scheduleShow(text, buttonText, delayMs, 0)
    }
}
