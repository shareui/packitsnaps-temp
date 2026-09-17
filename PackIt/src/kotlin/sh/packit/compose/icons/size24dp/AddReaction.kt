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
public val add_reaction: ImageVector
  get() {
    if (_add_reaction != null) {
      return _add_reaction!!
    }
    _add_reaction =
      ImageVector.Builder(
          name = "add_reaction",
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
            moveTo(12f, 12f)
            close()
            moveToRelative(0f, 10f)
            quadTo(9.93f, 22f, 8.1f, 21.21f)
            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
            reflectiveQuadTo(2f, 12f)
            quadTo(2f, 9.92f, 2.79f, 8.1f)
            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
            quadTo(9.93f, 2f, 12f, 2f)
            quadToRelative(1.08f, 0f, 2.08f, 0.21f)
            reflectiveQuadTo(16f, 2.82f)
            verticalLineTo(5.07f)
            quadTo(15.13f, 4.57f, 14.11f, 4.29f)
            reflectiveQuadTo(12f, 4f)
            quadTo(8.68f, 4f, 6.34f, 6.34f)
            reflectiveQuadTo(4f, 12f)
            reflectiveQuadToRelative(2.34f, 5.66f)
            reflectiveQuadTo(12f, 20f)
            reflectiveQuadToRelative(5.66f, -2.34f)
            reflectiveQuadTo(20f, 12f)
            quadToRelative(0f, -0.8f, -0.16f, -1.55f)
            reflectiveQuadTo(19.4f, 9f)
            horizontalLineToRelative(2.15f)
            quadToRelative(0.23f, 0.72f, 0.34f, 1.46f)
            reflectiveQuadTo(22f, 12f)
            quadToRelative(0f, 2.07f, -0.79f, 3.9f)
            reflectiveQuadToRelative(-2.14f, 3.17f)
            quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
            reflectiveQuadTo(12f, 22f)
            close()
            moveTo(20f, 7f)
            verticalLineTo(5f)
            horizontalLineTo(18f)
            verticalLineTo(3f)
            horizontalLineToRelative(2f)
            verticalLineTo(1f)
            horizontalLineToRelative(2f)
            verticalLineTo(3f)
            horizontalLineToRelative(2f)
            verticalLineTo(5f)
            horizontalLineTo(22f)
            verticalLineTo(7f)
            horizontalLineTo(20f)
            close()
            moveToRelative(-4.5f, 4f)
            quadToRelative(0.63f, 0f, 1.06f, -0.44f)
            reflectiveQuadTo(17f, 9.5f)
            reflectiveQuadTo(16.56f, 8.44f)
            reflectiveQuadTo(15.5f, 8f)
            reflectiveQuadTo(14.44f, 8.44f)
            reflectiveQuadTo(14f, 9.5f)
            reflectiveQuadToRelative(0.44f, 1.06f)
            reflectiveQuadTo(15.5f, 11f)
            close()
            moveToRelative(-7f, 0f)
            quadToRelative(0.63f, 0f, 1.06f, -0.44f)
            reflectiveQuadTo(10f, 9.5f)
            reflectiveQuadTo(9.56f, 8.44f)
            reflectiveQuadTo(8.5f, 8f)
            reflectiveQuadTo(7.44f, 8.44f)
            reflectiveQuadTo(7f, 9.5f)
            reflectiveQuadToRelative(0.44f, 1.06f)
            reflectiveQuadTo(8.5f, 11f)
            close()
            moveToRelative(6.59f, 5.54f)
            quadTo(16.48f, 15.58f, 17.1f, 14f)
            horizontalLineTo(6.9f)
            quadToRelative(0.63f, 1.57f, 2.01f, 2.54f)
            reflectiveQuadTo(12f, 17.5f)
            reflectiveQuadToRelative(3.09f, -0.96f)
            close()
          }
        }
        .build()
    return _add_reaction!!
  }

private var _add_reaction: ImageVector? = null

public val addReaction: ImageVector
  get() = add_reaction
