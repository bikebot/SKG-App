package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TestAnima() {
    var offp: Boolean by remember { mutableStateOf(false) }
    val col1 = MaterialTheme.colorScheme.primary
    val col2 = MaterialTheme.colorScheme.secondary
    var col: Color by remember { mutableStateOf(col1) }
//    val color by animateColorAsState(
//        targetValue = if(offp) MaterialTheme.colorScheme.primary
//        else MaterialTheme.colorScheme.secondary
//    )
    Button(
        onClick = {
            offp = !offp
            col = if (offp) col1 else col2
        },
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(col)
    ) {
        Text(
            "Col",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Circle(num: Int, itemSize: Dp) {
    var offp: Boolean by remember { mutableStateOf(false) }
    val offset by animateOffsetAsState(
        if(offp) Offset(0f, -40f) else Offset.Zero,
        tween(500, easing = FastOutSlowInEasing)
        // spring()
    )
    Box(
        modifier = Modifier
            .offset { offset.round() }
            .size(itemSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                offp = !offp
                Log.d("SKG-App", "Num: $num, $offp")
            },
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
    val numItems = 1
    val itemSize = 70.dp
    val minSpacing = 20.dp
    val coordinates = remember { List(numItems) { mutableStateOf<LayoutCoordinates?>(null) } }
    val highlights = remember { List(numItems) { mutableStateOf(false) } }

    SKGAppTheme(darkTheme = true) {
        // RowsWithEqualItems(numItems = numItems, itemSize = itemSize, spacing = minSpacing)
        CirclesLayout40(numItems, itemSize, minSpacing)
        // CirclesLayout35(numItems, itemSize, minSpacing, 320.dp)
    }
}


// Compute the ceiling of the division of two integers
infix fun Int.ceilDiv(b: Int): Int {
    return (this + b - 1) / b
}


@Composable
fun RowsWithEqualItems(numItems: Int, itemSize: Dp, spacing: Dp) {
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            repeat(numItems) { index -> Circle(index, itemSize) }
        }
    ) { measurables, constraints ->
        val constraints1 = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(constraints1) }
        val width = constraints.maxWidth
        val sizePlusSpacing = itemSize.roundToPx() + spacing.roundToPx()
        val maxItemsPerRow = (width + spacing.roundToPx()) / sizePlusSpacing
        val rows = placeables.size ceilDiv maxItemsPerRow
        val itemsPerRow = placeables.size ceilDiv rows
        var leftMargin =
            (width + spacing.roundToPx() - itemsPerRow * sizePlusSpacing) / 2
        val height = rows * sizePlusSpacing + 2 * leftMargin - spacing.roundToPx()

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
                        leftMargin = (width - itemsInLastRow * sizePlusSpacing + spacing.roundToPx()) / 2
                    }
                }
            }
        }
    }
}


// Solution of ChatGPT 4

// Hi ChatGPT, let's develop a JetPack Compose function that lays out a number of circles in rows.
// Use the minimum necessary number of rows. Distribute the circles as equally as possible into the
// rows. The circles shall be spaced by a specific distance and they shall be centered in their
// row. The circles are to produced with this composable function: Circle...
//
// Rewrite CirclesLayout using the Layout composable instead of Column.
@Composable
fun CirclesLayout40(totalCircles: Int, itemSize: Dp, spacing: Dp) {
    Layout(
        content = {
            for (i in 0 until totalCircles) {
                Circle(num = i, itemSize = itemSize)
            }
        }
    ) { measurables, constraints ->
        // Calculate the maximum number of circles per row based on the screen width
        val maxCirclesPerRow =
            ((constraints.maxWidth - spacing.roundToPx()) / (itemSize.toPx() + spacing.toPx())).toInt()
        //** Nicht korrekt. Korrekt wäre + statt -, da das Spacing für den letzen Item nicht benötigt wird.
        val maxCirclesPerRowCorrected =
            ((constraints.maxWidth + spacing.roundToPx()) / (itemSize.toPx() + spacing.toPx())).toInt()

        // Define the size of each item
        val itemConstraints =
            constraints.copy(minWidth = itemSize.roundToPx(), minHeight = itemSize.roundToPx()-1)
        //** Rounding error: minHeight=193, maxHeight=192

        // Measure each child
        val placeables = measurables.map { measurable ->
            measurable.measure(itemConstraints)
        }

        // Calculate the size of the layout
        val numRows = ceil(totalCircles.toFloat() / maxCirclesPerRow).toInt()
        val layoutWidth = constraints.maxWidth
        val layoutHeight = (itemSize.toPx() * numRows + spacing.toPx() * (numRows - 1)).toInt()
        //** Korrekt aber nicht schön. Besser wäre eine zusätzliche Marge oben und unten

        // Define the layout
        layout(layoutWidth, layoutHeight) {
            var yPosition = 0

            placeables.chunked(maxCirclesPerRow).forEach { row ->
                //** Hier wird der Startwert für die Zentrierung berechnet. Gut!
                var xPosition =
                    (layoutWidth - (row.size * itemSize.toPx() + (row.size - 1) * spacing.toPx())) / 2f

                row.forEach { placeable ->
                    placeable.placeRelative(x = xPosition.roundToInt(), y = yPosition)
                    xPosition += itemSize.toPx() + spacing.toPx()
                }

                yPosition += itemSize.roundToPx() + spacing.roundToPx()
            }
        }
    }
}


// Solution of ChatGPT 3.5
// Zahlreiche Fehler. Funktioniert so nicht.

@Composable
fun CirclesLayout35(circleCount: Int, itemSize: Dp, rowSpacing: Dp, availableWidth: Dp) {
    val circlesPerRow = max(1, (availableWidth / (itemSize + rowSpacing)).toInt())
    val rows = (circleCount + circlesPerRow - 1) / circlesPerRow // Calculate the number of rows

    Layout(
        content = {
            for (rowIndex in 0 until rows) {
                val start = rowIndex * circlesPerRow
                val end = min(start + circlesPerRow, circleCount)

                val row = (start until end).map { num ->
                    key(num) {
                        Circle(num = num, itemSize = itemSize)
                    }
                }

                // row.forEach { composable -> composable() }
                //** Fehler

                if (rowIndex < rows - 1) {
                    Spacer(modifier = Modifier.height(rowSpacing))
                }
            }
        }
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 }
        val rowHeights = IntArray(rows) { 0 }

        for (i in 0 until rows) {
            val start = i * circlesPerRow
            val end = min(start + circlesPerRow, circleCount)

            for (j in start until end) {
                val placeable = measurables[j].measure(constraints)
                //** Mehrfache Berechnung der Größe derselben Composables
                rowWidths[i] += placeable.width
                rowHeights[i] = max(rowHeights[i], placeable.height)
            }

            if (i < rows - 1) {
                rowHeights[i] += rowSpacing.roundToPx() //** .toIntPx() gibt es nicht
            }
        }

        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        val height = rowHeights.sumBy { it }.coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width, height) {
            var currentX = 0
            var currentY = 0

            for (i in 0 until rows) {
                val rowWidth = rowWidths[i]
                for (j in 0 until circlesPerRow) {
                    if (j + i * circlesPerRow >= circleCount) break
                    val placeable = measurables[j + i * circlesPerRow].measure(constraints)
                    val xPos = currentX + (rowWidth - placeable.width) / 2
                    val yPos = currentY
                    placeable.place(x = xPos, y = yPos)
                    currentX += rowWidth
                }
                currentX = 0
                currentY += rowHeights[i]
            }
        }
    }
}
