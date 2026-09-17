package sh.packit.compose.icons.size28dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val titlecase: ImageVector
  get() {
    if (_titlecase != null) {
      return _titlecase!!
    }
    _titlecase =
      ImageVector.Builder(
          name = "titlecase",
          defaultWidth = 28.dp,
          defaultHeight = 28.dp,
          viewportWidth = 28f,
          viewportHeight = 28f,
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
            moveTo(10.08f, 20.66f)
            verticalLineTo(9.84f)
            horizontalLineTo(6.53f)
            verticalLineTo(8.07f)
            horizontalLineToRelative(8.98f)
            verticalLineTo(9.84f)
            horizontalLineTo(11.97f)
            verticalLineTo(20.66f)
            horizontalLineTo(10.08f)
            close()
            moveToRelative(10.03f, 0.29f)
            quadToRelative(-1.24f, 0f, -1.96f, -0.74f)
            reflectiveQuadTo(17.43f, 18.2f)
            verticalLineTo(13.28f)
            horizontalLineTo(15.85f)
            verticalLineTo(11.69f)
            horizontalLineToRelative(1.58f)
            verticalLineTo(9.15f)
            horizontalLineToRelative(1.82f)
            verticalLineToRelative(2.54f)
            horizontalLineToRelative(2.16f)
            verticalLineToRelative(1.59f)
            horizontalLineTo(19.25f)
            verticalLineTo(17.8f)
            quadToRelative(0f, 0.67f, 0.32f, 1.07f)
            reflectiveQuadToRelative(0.86f, 0.4f)
            quadToRelative(0.25f, 0f, 0.52f, -0.09f)
            reflectiveQuadToRelative(0.52f, -0.25f)
            verticalLineToRelative(1.77f)
            quadToRelative(-0.29f, 0.13f, -0.62f, 0.19f)
            reflectiveQuadToRelative(-0.73f, 0.06f)
            close()
          }
        }
        .build()
    return _titlecase!!
  }

private var _titlecase: ImageVector? = null

public val titleCase: ImageVector
  get() = titlecase
