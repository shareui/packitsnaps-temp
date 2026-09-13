package sh.packit.core.info

import android.content.Context
import sh.packit.core.utils.Logx

object ClientInfo {
    private const val EXTERA_PACKAGE: String = "com.exteragram.messenger"
    private const val AYU_PACKAGE: String = "com.radolyn.ayugram"
    private const val OFFICIAL_PACKAGE: String = "org.telegram.messenger"

    @JvmStatic
    fun getClientName(context: Context?): String {
        val pkg = context?.packageName ?: return "Universal"
        return when {
            pkg.contains("exteragram") -> "exteraGram"
            pkg.contains("ayugram") -> "AyuGram"
            pkg.contains("telegram") -> "Telegram"
            else -> "Universal"
        }
    }

    @JvmStatic
    fun getClientVersion(context: Context?): String {
        if (context == null) return ""
        return try {
            val pm = context.packageManager
            val info = pm.getPackageInfo(context.packageName, 0)
            info.versionName ?: ""
        } catch (e: Throwable) {
            Logx.logx("getClientVersion error: $e", isDebug = false)
            ""
        }
    }

    @JvmStatic
    fun getClientLabel(context: Context?): String {
        val name = getClientName(context)
        val ver = getClientVersion(context)
        return if (ver.isNotEmpty()) "$name $ver" else name
    }
}
