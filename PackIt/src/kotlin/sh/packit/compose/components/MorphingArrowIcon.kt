package sh.packit.compose.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MorphingArrowIcon(
    isExpanded: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    val progress: Float by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "arrowMorphProgress"
    )

    val path: Path = remember { Path() }

    Canvas(modifier = modifier.size(size)) {
        val scale: Float = this.size.minDimension / 20f
        path.reset()

        fun lerp(start: Float, stop: Float): Float = start + (stop - start) * progress

        val p0x: Float = 10f * scale
        val p0y: Float = lerp(13.06f, 6.94f) * scale

        val p1x: Float = 5f * scale
        val p1y: Float = lerp(8.06f, 11.94f) * scale

        val p2x: Float = 6.06f * scale
        val p2y: Float = lerp(7f, 13f) * scale

        val p3x: Float = 10f * scale
        val p3y: Float = lerp(10.94f, 9.06f) * scale

        val p4x: Float = 13.94f * scale
        val p4y: Float = lerp(7f, 13f) * scale

        val p5x: Float = 15f * scale
        val p5y: Float = lerp(8.06f, 11.94f) * scale

        path.moveTo(p0x, p0y)
        path.lineTo(p1x, p1y)
        path.lineTo(p2x, p2y)
        path.lineTo(p3x, p3y)
        path.lineTo(p4x, p4y)
        path.lineTo(p5x, p5y)
        path.close()

        drawPath(path = path, color = tint)
    }
}
