package de.gathok.bookoverview.ui.swipeContainer

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.overview.BookItem
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
    var isLeftOptionsRevealed by remember {
        mutableStateOf(isLeftOptionsRevealedInnit)
    }
    var isRightOptionsRevealed by remember {
        mutableStateOf(isRightOptionsRevealedInnit)
    }
    var leftContextMenuWidth by remember {
        mutableFloatStateOf(0f)
    }
    var rightContextMenuWidth by remember {
        mutableFloatStateOf(0f)
    }
    val offset = remember {
        Animatable(initialValue = 0f)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isLeftOptionsRevealed, key2 = isRightOptionsRevealed) {
        if(isLeftOptionsRevealed) {
            offset.animateTo(leftContextMenuWidth)
        } else if(isRightOptionsRevealed) {
            offset.animateTo(-rightContextMenuWidth)
        } else {
            offset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .onSizeChanged {
                        leftContextMenuWidth = it.width.toFloat()
                    },
                horizontalAlignment = Alignment.Start
            ) {
                if (leftOptions != null) {
                    leftOptions()
                }
            }
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                // Empty Column
            }
            Column(
                modifier = Modifier
                    .onSizeChanged {
                        rightContextMenuWidth = it.width.toFloat()
                    },
                horizontalAlignment = Alignment.End
            ) {
                if (rightOptions != null) {
                    rightOptions()
                }
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
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
                            println(offset.value)
                            if (offset.value >= leftContextMenuWidth / 2f) {
                                scope.launch {
                                    offset.animateTo(leftContextMenuWidth)
                                    if (!isLeftOptionsRevealed) {
                                        isLeftOptionsRevealed = true
                                    }
                                }
                            } else if (offset.value <= -rightContextMenuWidth / 2f) {
                                scope.launch {
                                    offset.animateTo(-rightContextMenuWidth)
                                    if (!isRightOptionsRevealed) {
                                        isRightOptionsRevealed = true
                                    }
                                }
                            } else {
                                scope.launch {
                                    offset.animateTo(0f)
                                    if (isLeftOptionsRevealed) {
                                        isLeftOptionsRevealed = false
                                    }
                                    if (isRightOptionsRevealed) {
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

@Preview
@Composable
private fun SwipeItemPreview() {
    SwipeItem (
        leftOptions = {
            ActionIcon(
                onClick = {
                },
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                icon = Icons.Default.Info,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        rightOptions = {
            ActionIcon(
                onClick = {
                },
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                icon = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        },
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row (
                modifier = Modifier
                    .padding(12.dp, 0.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                BookItem(Book(
                    title = "Title",
                    author = "Author",
                    isbn = "ISBN",
                    rating = 5,
                ))
            }
            Row (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                )
            }
        }
    }
}