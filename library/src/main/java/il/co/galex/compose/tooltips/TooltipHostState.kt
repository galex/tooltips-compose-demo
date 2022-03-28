package il.co.galex.compose.tooltips


import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@Stable
class TooltipHostState {

    private val mutex = Mutex()

    var currentTooltipData by mutableStateOf<TooltipData?>(null)
        private set

    suspend fun showTooltip(
        message: String,
        layoutCoordinates: LayoutCoordinates,
    ): TooltipResult = mutex.withLock {
        try {
            return suspendCancellableCoroutine { continuation ->
                currentTooltipData = TooltipDataImpl(message, layoutCoordinates, continuation)
            }
        } finally {
            currentTooltipData = null
        }
    }

    @Stable
    private class TooltipDataImpl(
        override val message: String,
        override val target: LayoutCoordinates,
        private val continuation: CancellableContinuation<TooltipResult>
    ) : TooltipData {

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(TooltipResult.Dismissed)
        }
    }
}

@Composable
fun TooltipHost(
    hostState: TooltipHostState,
    modifier: Modifier = Modifier,
    tooltip: @Composable (TooltipData) -> Unit = { Tooltip(it) }
) {
    FadeInFadeOutWithScale(
        current = hostState.currentTooltipData,
        modifier = modifier,
        content = tooltip
    )
}

interface TooltipData {
    val message: String
    val target: LayoutCoordinates
    fun dismiss()
}

enum class TooltipResult {
    Dismissed
}

@Composable
private fun FadeInFadeOutWithScale(
    current: TooltipData?,
    modifier: Modifier = Modifier,
    content: @Composable (TooltipData) -> Unit
) {
    val state = remember { FadeInFadeOutState<TooltipData?>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.filterNotNull().mapTo(state.items) { key ->
            FadeInFadeOutAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) TooltipFadeInMillis else TooltipFadeOutMillis
                val delay = TooltipFadeOutMillis + TooltipInBetweenDelayMillis
                val animationDelay = if (isVisible && keys.filterNotNull().size != 1) delay else 0
                val opacity = animatedOpacity(
                    animation = tween(
                        easing = LinearEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible,
                    onAnimationFinish = {
                        if (key != state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key == key }
                            state.scope?.invalidate()
                        }
                    }
                )
                val scale = animatedScale(
                    animation = tween(
                        easing = FastOutSlowInEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible
                )
                Box(
                    Modifier
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = opacity.value
                        )
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            dismiss { key.dismiss(); true }
                        }
                ) {
                    children()
                }
            }
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.forEach { (item, opacity) ->
            key(item) {
                item?.let {
                    opacity {
                        TooltipHostLayout(
                            tooltipData = item,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            content(item)
                        }
                    }
                }
            }
        }
    }
}

private class FadeInFadeOutState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<FadeInFadeOutAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class FadeInFadeOutAnimationItem<T>(
    val key: T,
    val transition: FadeInFadeOutTransition
)

private typealias FadeInFadeOutTransition = @Composable (content: @Composable () -> Unit) -> Unit

@Composable
private fun animatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): State<Float> {
    val alpha = remember { Animatable(if (!visible) 1f else 0f) }
    LaunchedEffect(visible) {
        alpha.animateTo(
            if (visible) 1f else 0f,
            animationSpec = animation
        )
        onAnimationFinish()
    }
    return alpha.asState()
}

@Composable
private fun animatedScale(animation: AnimationSpec<Float>, visible: Boolean): State<Float> {
    val scale = remember { Animatable(if (!visible) 1f else 0.8f) }
    LaunchedEffect(visible) {
        scale.animateTo(
            if (visible) 1f else 0.8f,
            animationSpec = animation
        )
    }
    return scale.asState()
}

private const val TooltipFadeInMillis = 150
private const val TooltipFadeOutMillis = 75
private const val TooltipInBetweenDelayMillis = 0