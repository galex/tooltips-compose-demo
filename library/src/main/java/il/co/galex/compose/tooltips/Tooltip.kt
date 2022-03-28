package il.co.galex.compose.tooltips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import il.co.galex.compose.tooltips.library.R

@Composable
fun Tooltip(
    tooltipData: TooltipData,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 6.dp
) {
    Surface(
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = tooltipData.message,
            modifier = Modifier
                .padding(20.dp)
                .clickable { tooltipData.dismiss() }
        )
    }
}


