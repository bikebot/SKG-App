package de.skg_botnang.skg_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.skg_botnang.skg_app.ui.theme.SKGAppTheme

@Composable
fun ColorPair(name: String, background: Color, foreground: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        val fontSize = MaterialTheme.typography.titleSmall.fontSize
        Text(
            name,
            color = foreground,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            lineHeight = fontSize.times(1.2)
        )
    }
}


@Composable
fun ColorPalette(darkTheme: Boolean) {
    SKGAppTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.width(320.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp)
                ) {
    //                items(boxItems) { item ->
    //                    BoxWithText(item, Modifier.fillMaxWidth())
    //                }
                    item {
                        ColorPair(
                            "Primary",
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    item {
                        ColorPair(
                            "Secondary",
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    item {
                        ColorPair(
                            "Tertiary",
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    item {
                        ColorPair(
                            "Surface",
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.onSurface
                        )
                    }
                   item {
                        ColorPair(
                            "Surface Variant",
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                   item {
                        ColorPair(
                            "Primary Container",
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                   item {
                        ColorPair(
                            "Secondary Container",
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                   item {
                        ColorPair(
                            "Tertiary Container",
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                   item {
                        ColorPair(
                            "Error Container",
                            MaterialTheme.colorScheme.errorContainer,
                            MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Button(onClick = {}) {
                    Text("Test")
                }

                Slider(value = 0.5F, valueRange = 0f.rangeTo(1f), onValueChange = {})

                Switch(checked = false, onCheckedChange = {})

                ElevatedCard(
                    modifier = Modifier.padding(12.dp),
                    colors = CardDefaults.cardColors(),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Text("Card", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun LightPalette() { ColorPalette(darkTheme = false)}

@Preview
@Composable
fun DarkPalette() { ColorPalette(darkTheme = true)}