package il.co.galex.compose.tooltips

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.tooltip(
    tooltipHostState: TooltipHostState,
    text: String
) = composed {
    var layoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    if (layoutCoordinates != null) {
        LaunchedEffect(layoutCoordinates) {
            Log.d("tooltip", "LaunchedEffect = $layoutCoordinates")
            tooltipHostState.showTooltip(text, layoutCoordinates!!)
        }
    }

    onGloballyPositioned {
        layoutCoordinates = it
        Log.d("tooltip", "onGloballyPositioned = $layoutCoordinates")
    }
}