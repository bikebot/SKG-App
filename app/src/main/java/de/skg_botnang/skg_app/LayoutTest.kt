package de.skg_botnang.skg_app

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme

@Composable
fun Num(num: Int, itemSize: Dp) {
    var offp: Boolean by remember { mutableStateOf(false) }
    val offset by animateOffsetAsState(
        if(offp) Offset(0f, -40f) else Offset.Zero,
        // tween(500, easing = FastOutSlowInEasing)
        // spring()
    )
    Box(
        modifier = Modifier
            .offset{ offset.round() }
            .size(itemSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { offp = !offp },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$num",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun LayoutTestScreen() {
    val numItems = 10
    val itemSize = 70.dp
    val minSpacing = 20.dp
    val coordinates = remember { List(numItems) { mutableStateOf<LayoutCoordinates?>(null) } }
    val highlights = remember { List(numItems) { mutableStateOf(false) } }

    SKGAppTheme(darkTheme = true) {
        RowsWithEqualItems(numItems = numItems, itemSize = itemSize, minSpacing = minSpacing)
    }
}


// Compute the ceiling of the division of two integers
infix fun Int.ceilDiv(b: Int): Int {
    return (this + b - 1) / b
}


@Composable
fun RowsWithEqualItems(numItems: Int, itemSize: Dp, minSpacing: Dp) {
    val sizePlusSpacing = with (LocalDensity.current) {
        (itemSize + minSpacing).roundToPx()
    }
    val itemSizePx = with (LocalDensity.current) { itemSize.roundToPx() }
    val spacingPx = with (LocalDensity.current) { minSpacing.roundToPx() }
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            repeat(numItems) { index -> Num(index, itemSize) }
        }
    ) { measurables, constraints ->
        val constraints1 = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(constraints1) }
        val width = constraints.maxWidth
        val maxItemsPerRow = width / sizePlusSpacing
        val rows = placeables.size ceilDiv maxItemsPerRow
        val itemsPerRow = placeables.size ceilDiv rows
        var leftMargin =
            (width - itemsPerRow * sizePlusSpacing + spacingPx) / 2
        val height = rows * sizePlusSpacing + 2 * leftMargin - spacingPx

        layout(width, height) {
            var currentY = leftMargin

            placeables.forEachIndexed { index, placeable ->
                // Placeable position in the current row
                val rowPos = index % itemsPerRow

                placeable.placeRelative(
                    x = leftMargin + rowPos * sizePlusSpacing,
                    y = currentY
                )

                // If we are the last item of the row, move to the next row
                if (rowPos == itemsPerRow - 1) {
                    // Move to the next row
                    currentY += sizePlusSpacing

                    // If we are in the last row but one, center the remaining items horizontally
                    val currentRow = index / itemsPerRow + 1 // 1-based
                    if (currentRow == rows - 1) {
                        val itemsInLastRow = placeables.size - (rows - 1) * itemsPerRow
                        leftMargin = (width - itemsInLastRow * sizePlusSpacing + spacingPx) / 2
                    }
                }
            }
        }
    }
}
