package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Heads-Up Display composable. Shows game state information like currency, lives, and wave number.
 *
 * @param currencyState The current player currency as a State object.
 * @param livesState The current player lives as a State object.
 * @param waveState The current wave number as a State object.
 */
@Composable
fun HUD(
    modifier: Modifier = Modifier,
    currencyState: State<Int>,
    livesState: State<Int>,
    waveState: State<Int>
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp // Add some elevation for visibility
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HudElement(icon = Icons.Default.MonetizationOn, value = currencyState.value.toString(), contentDescription = "Currency")
            HudElement(icon = Icons.Default.Favorite, value = livesState.value.toString(), contentDescription = "Lives")
            HudElement(icon = Icons.Default.Label, value = waveState.value.toString(), contentDescription = "Wave")
        }
    }
}

/**
 * Reusable composable for a single element within the HUD (Icon + Value).
 */
@Composable
private fun HudElement(
    icon: ImageVector,
    value: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface // Use theme color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold // Make value bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HUDPreview() {
    val currency = remember { mutableStateOf(100) }
    val lives = remember { mutableStateOf(3) }
    val wave = remember { mutableStateOf(1) }

    MaterialTheme { // Apply MaterialTheme for preview
        HUD(
            currencyState = currency,
            livesState = lives,
            waveState = wave
        )
    }
} 