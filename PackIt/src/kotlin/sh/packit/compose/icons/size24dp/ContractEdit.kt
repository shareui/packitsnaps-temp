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
public val contract_edit: ImageVector
  get() {
    if (_contract_edit != null) {
      return _contract_edit!!
    }
    _contract_edit =
      ImageVector.Builder(
          name = "contract_edit",
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
            moveTo(9f, 9f)
            verticalLineTo(7f)
            horizontalLineToRelative(9f)
            verticalLineTo(9f)
            horizontalLineTo(9f)
            close()
            moveToRelative(0f, 3f)
            verticalLineTo(10f)
            horizontalLineToRelative(9f)
            verticalLineToRelative(2f)
            horizontalLineTo(9f)
            close()
            moveToRelative(3f, 8f)
            horizontalLineTo(6f)
            quadTo(5.58f, 20f, 5.29f, 20f)
            reflectiveQuadTo(5f, 20f)
            horizontalLineToRelative(7f)
            close()
            moveToRelative(0f, 2f)
            horizontalLineTo(6f)
            quadTo(4.75f, 22f, 3.88f, 21.13f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(16f)
            horizontalLineTo(6f)
            verticalLineTo(2f)
            horizontalLineTo(21f)
            verticalLineToRelative(9.02f)
            quadToRelative(-0.5f, -0.05f, -1.01f, 0.04f)
            reflectiveQuadTo(19f, 11.38f)
            verticalLineTo(4f)
            horizontalLineTo(8f)
            verticalLineTo(16f)
            horizontalLineToRelative(6f)
            lineToRelative(-2f, 2f)
            horizontalLineTo(5f)
            verticalLineToRelative(1f)
            quadToRelative(0f, 0.43f, 0.29f, 0.71f)
            reflectiveQuadTo(6f, 20f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(2f)
            close()
            moveToRelative(2f, 0f)
            verticalLineTo(18.93f)
            lineToRelative(5.53f, -5.5f)
            quadToRelative(0.22f, -0.22f, 0.5f, -0.32f)
            reflectiveQuadTo(20.58f, 13f)
            quadToRelative(0.3f, 0f, 0.57f, 0.11f)
            quadToRelative(0.27f, 0.11f, 0.5f, 0.34f)
            lineToRelative(0.93f, 0.93f)
            quadToRelative(0.2f, 0.22f, 0.31f, 0.5f)
            reflectiveQuadTo(23f, 15.43f)
            reflectiveQuadToRelative(-0.1f, 0.56f)
            reflectiveQuadTo(22.58f, 16.5f)
            lineTo(17.08f, 22f)
            horizontalLineTo(14f)
            close()
            moveToRelative(7.5f, -6.58f)
            lineTo(20.58f, 14.5f)
            lineToRelative(0.92f, 0.92f)
            close()
            moveToRelative(-6f, 5.08f)
            horizontalLineToRelative(0.95f)
            lineToRelative(3.03f, -3.05f)
            lineTo(19.03f, 16.98f)
            lineTo(18.55f, 16.52f)
            lineTo(15.5f, 19.55f)
            verticalLineTo(20.5f)
            close()
            moveToRelative(3.53f, -3.52f)
            lineTo(18.55f, 16.52f)
            lineToRelative(0.93f, 0.93f)
            lineTo(19.03f, 16.98f)
            close()
          }
        }
        .build()
    return _contract_edit!!
  }

private var _contract_edit: ImageVector? = null

public val contractEdit: ImageVector
  get() = contract_edit
