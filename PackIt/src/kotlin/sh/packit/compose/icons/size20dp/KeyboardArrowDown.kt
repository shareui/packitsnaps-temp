package sh.packit.compose.icons.size20dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val keyboard_arrow_down: ImageVector
  get() {
    if (_keyboard_arrow_down != null) {
      return _keyboard_arrow_down!!
    }
    _keyboard_arrow_down =
      ImageVector.Builder(
          name = "keyboard_arrow_down",
          defaultWidth = 20.dp,
          defaultHeight = 20.dp,
          viewportWidth = 20f,
          viewportHeight = 20f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(10f, 13.06f)
            lineToRelative(-5f, -5f)
            lineTo(6.06f, 7f)
            lineTo(10f, 10.94f)
            lineTo(13.94f, 7f)
            lineTo(15f, 8.06f)
            lineToRelative(-5f, 5f)
            close()
          }
        }
        .build()
    return _keyboard_arrow_down!!
  }

private var _keyboard_arrow_down: ImageVector? = null
