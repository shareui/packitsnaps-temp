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
public val check: ImageVector
  get() {
    if (_check != null) {
      return _check!!
    }
    _check =
      ImageVector.Builder(
          name = "check",
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
            moveTo(8.1f, 14.44f)
            lineTo(4.06f, 10.42f)
            lineTo(5.13f, 9.33f)
            lineTo(8.1f, 12.31f)
            lineTo(14.88f, 5.56f)
            lineToRelative(1.06f, 1.06f)
            lineTo(8.1f, 14.44f)
            close()
          }
        }
        .build()
    return _check!!
  }

private var _check: ImageVector? = null
