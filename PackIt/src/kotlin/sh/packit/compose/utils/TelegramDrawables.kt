package sh.packit.compose.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import sh.packit.core.utils.Logx

object TelegramDrawables {
    private val idCache = mutableMapOf<String, Int>()

    fun getResId(context: Context, name: String): Int {
        return idCache.getOrPut(name) {
            try {
                val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
                if (resId != 0) return@getOrPut resId
            } catch (e: Exception) {
                Logx.logx("getIdentifier failed for $name: $e", isDebug = false)
            }
            try {
                val clazz = Class.forName("org.telegram.messenger.R")
                clazz.getField(name).getInt(null)
            } catch (e: Exception) {
                Logx.logx("R field lookup failed for $name: $e", isDebug = false)
                0
            }
        }
    }

    fun getDrawable(context: Context, name: String): Drawable? {
        val id = getResId(context, name)
        if (id == 0) return null
        return try {
            ContextCompat.getDrawable(context, id)
        } catch (e: Exception) {
            Logx.logx("getDrawable by id failed for $name: $e", isDebug = false)
            null
        }
    }

    fun getDrawable(context: Context, resId: Int): Drawable? {
        if (resId == 0) return null
        return try {
            ContextCompat.getDrawable(context, resId)
        } catch (e: Exception) {
            Logx.logx("getDrawable by resId failed for $resId: $e", isDebug = false)
            null
        }
    }

    fun toPainter(drawable: Drawable): Painter {
        val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 48
        val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 48
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return BitmapPainter(bitmap.asImageBitmap())
    }
}

@Composable
fun rememberTelegramPainter(name: String?): Painter? {
    if (name.isNullOrEmpty()) return null
    val context = LocalContext.current
    return remember(name, context) {
        val drawable = TelegramDrawables.getDrawable(context, name) ?: return@remember null
        TelegramDrawables.toPainter(drawable)
    }
}

@Composable
fun rememberTelegramPainter(resId: Int?): Painter? {
    if (resId == null || resId == 0) return null
    val context = LocalContext.current
    return remember(resId, context) {
        val drawable = TelegramDrawables.getDrawable(context, resId) ?: return@remember null
        TelegramDrawables.toPainter(drawable)
    }
}
