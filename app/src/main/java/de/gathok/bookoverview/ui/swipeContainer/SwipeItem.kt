package de.gathok.bookoverview.ui.swipeContainer

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeItem(
    modifier: Modifier = Modifier,
    leftOptions: @Composable (() -> Unit)? = null,
    isLeftOptionsRevealedInnit: Boolean = false,
    rightOptions: @Composable (() -> Unit)? = null,
    isRightOptionsRevealedInnit: Boolean = false,
    content: @Composable () -> Unit
) {
    var isLeftOptionsRevealed by remember { mutableStateOf(isLeftOptionsRevealedInnit) }
    var isRightOptionsRevealed by remember { mutableStateOf(isRightOptionsRevealedInnit) }
    var leftContextMenuWidth by remember { mutableFloatStateOf(0f) }
    var rightContextMenuWidth by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isLeftOptionsRevealed, key2 = isRightOptionsRevealed) {
        when {
            isLeftOptionsRevealed -> offset.animateTo(leftContextMenuWidth)
            isRightOptionsRevealed -> offset.animateTo(-rightContextMenuWidth)
            else -> offset.animateTo(0f)
        }
    }

    Box(modifier = modifier) {
        // Background row that holds the left/right action icons.
        // matchParentSize() makes its height match the parent's (which is determined by the Surface below)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.onSizeChanged { leftContextMenuWidth = it.width.toFloat() },
                horizontalAlignment = Alignment.Start
            ) {
                leftOptions?.let { it() }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.onSizeChanged { rightContextMenuWidth = it.width.toFloat() },
                horizontalAlignment = Alignment.End
            ) {
                rightOptions?.let { it() }
            }
        }
        // The Surface containing your content. Its size (wrapContentSize) now drives the overall height.
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(listOf(leftContextMenuWidth, rightContextMenuWidth)) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offset.value + dragAmount)
                                    .coerceIn(-rightContextMenuWidth, leftContextMenuWidth)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value >= leftContextMenuWidth / 2f -> {
                                    scope.launch {
                                        offset.animateTo(leftContextMenuWidth)
                                        if (!isLeftOptionsRevealed) {
                                            isLeftOptionsRevealed = true
                                            isRightOptionsRevealed = false
                                        }
                                    }
                                }
                                offset.value <= -rightContextMenuWidth / 2f -> {
                                    scope.launch {
                                        offset.animateTo(-rightContextMenuWidth)
                                        if (!isRightOptionsRevealed) {
                                            isRightOptionsRevealed = true
                                            isLeftOptionsRevealed = false
                                        }
                                    }
                                }
                                else -> {
                                    scope.launch {
                                        offset.animateTo(0f)
                                        isLeftOptionsRevealed = false
                                        isRightOptionsRevealed = false
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
//@Preview
private fun SwipeItemPreview() {
    SwipeItem(
        leftOptions = {
            ActionIcon(
                onClick = { },
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                icon = Icons.Default.Info,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        rightOptions = {
            ActionIcon(
                onClick = { },
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                icon = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Swipe me",
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
