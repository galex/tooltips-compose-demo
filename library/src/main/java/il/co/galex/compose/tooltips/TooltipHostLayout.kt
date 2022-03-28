package il.co.galex.compose.tooltips

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp

@Composable
fun TooltipHostLayout(
    tooltipData: TooltipData,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        require(measurables.size == 1) { "We can place only one tooltip at the time"}
        val itemConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeable = measurables[0].measure(itemConstraints)

        layout(constraints.maxWidth, constraints.maxHeight) {

            val size = tooltipData.target.size
            val position: Offset = tooltipData.target.positionInRoot()

            // let's place it under the target
            val targetCenterX = position.x + size.width / 2
            val targetBottomY = position.y + size.height

            val x: Float = targetCenterX - placeable.width / 2
            val y: Float = targetBottomY

            placeable.place(x.toInt(), y.toInt())
        }
    }
}
