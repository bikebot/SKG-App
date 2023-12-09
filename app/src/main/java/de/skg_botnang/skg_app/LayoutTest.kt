package de.skg_botnang.skg_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme

@Composable
fun Num(num: Int, itemSize: Dp) {
    Box(
        modifier = Modifier
            .size(itemSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$num",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            //fontSize = MaterialTheme.typography.titleSmall.fontSize
        )
    }
}


@Preview
@Composable
fun LayoutTestScreen() {
    val numItems = 9
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
        val leftMargin =
            (width - (itemsPerRow * itemSizePx) - (itemsPerRow - 1) * spacingPx) / 2
        val height = rows * sizePlusSpacing + 2 * leftMargin - spacingPx

        layout(width, height) {
            var currentY = leftMargin

            placeables.forEachIndexed { index, placeable ->
                // Position each item in the calculated row
                val xPos = index % itemsPerRow
                placeable.placeRelative(
                    x = leftMargin + xPos * sizePlusSpacing,
                    y = currentY
                )

                if (xPos == itemsPerRow - 1) {
                    // Move to the next row
                    currentY += sizePlusSpacing
                }
            }
        }
    }
}
