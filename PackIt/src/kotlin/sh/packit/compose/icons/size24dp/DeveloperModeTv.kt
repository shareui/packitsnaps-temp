package sh.packit.compose.icons.size24dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val developer_mode_tv: ImageVector
  get() {
    if (_developer_mode_tv != null) {
      return _developer_mode_tv!!
    }
    _developer_mode_tv =
      ImageVector.Builder(
          name = "developer_mode_tv",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
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
            moveTo(2f, 7f)
            verticalLineTo(5f)
            quadTo(2f, 4.17f, 2.59f, 3.59f)
            reflectiveQuadTo(4f, 3f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(22f, 5f)
            verticalLineTo(7f)
            horizontalLineTo(20f)
            verticalLineTo(5f)
            horizontalLineTo(4f)
            verticalLineTo(7f)
            horizontalLineTo(2f)
            close()
            moveTo(8f, 21f)
            verticalLineTo(19f)
            horizontalLineTo(4f)
            quadTo(3.18f, 19f, 2.59f, 18.41f)
            reflectiveQuadTo(2f, 17f)
            verticalLineTo(15f)
            horizontalLineTo(4f)
            verticalLineToRelative(2f)
            horizontalLineTo(20f)
            verticalLineTo(15f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 19f)
            horizontalLineTo(16f)
            verticalLineToRelative(2f)
            horizontalLineTo(8f)
            close()
            moveTo(12f, 11f)
            close()
            moveTo(4.8f, 11f)
            lineTo(7.4f, 8.4f)
            lineTo(6f, 7f)
            lineTo(2f, 11f)
            lineToRelative(4f, 4f)
            lineTo(7.4f, 13.6f)
            lineTo(4.8f, 11f)
            close()
            moveToRelative(14.4f, 0f)
            lineToRelative(-2.6f, 2.6f)
            lineTo(18f, 15f)
            lineToRelative(4f, -4f)
            lineTo(18f, 7f)
            lineTo(16.6f, 8.4f)
            lineTo(19.2f, 11f)
            close()
          }
        }
        .build()
    return _developer_mode_tv!!
  }

private var _developer_mode_tv: ImageVector? = null

public val developerModeTv: ImageVector
  get() = developer_mode_tv
