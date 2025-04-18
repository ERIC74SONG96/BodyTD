package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplicationbodytd.ui.TowerType

// Remove redundant cost map (should use centralized costs eventually)
/*
val towerCosts = mapOf(
    TowerType.MUCUS to 10,
    TowerType.MACROPHAGE to 20,
    TowerType.COUGH to 10
)
*/

/**
 * Composable panel for selecting towers to place.
 *
 * @param currentCurrency The player's current currency (as Int).
 * @param selectedTowerType The currently selected tower type for placement (as TowerType?).
 * @param onTowerSelected Callback function when a tower button is clicked.
 */
@Composable
fun TowerSelectionPanel(
    modifier: Modifier = Modifier,
    currentCurrency: Int,
    selectedTowerType: TowerType?,
    onTowerSelected: (TowerType) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Build Towers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TowerType.entries.forEach { towerType ->
                    // TODO: Get cost from centralized source (Task 5)
                    val cost = getTowerCostStatically(towerType) // Use helper

                    TowerButton(
                        towerType = towerType,
                        cost = cost,
                        currentCurrency = currentCurrency,
                        isSelected = selectedTowerType == towerType,
                        onClick = { onTowerSelected(towerType) }
                    )
                }
            }
        }
    }
}

// Helper to get static costs (until Task 5 fully integrated)
// This should ideally live in ViewModel or a shared utility
private fun getTowerCostStatically(towerType: TowerType): Int {
    return when (towerType) {
        TowerType.MUCUS -> 10 // Hardcoded for now
        TowerType.MACROPHAGE -> 20
        TowerType.COUGH -> 10
    }
}

@Composable
private fun TowerButton(
    towerType: TowerType,
    cost: Int,
    currentCurrency: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val canAfford = currentCurrency >= cost
    val border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    Button(
        onClick = onClick,
        enabled = canAfford,
        border = border,
        colors = ButtonDefaults.buttonColors(
            // Optional: Different color when selected?
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(towerType.name.replaceFirstChar { it.titlecase() }) // Basic name formatting
            Spacer(Modifier.height(4.dp))
            Text("Cost: $cost", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TowerSelectionPanelPreview() {
    // Use plain values for preview state
    var selectedTowerPreview by remember { mutableStateOf<TowerType?>(TowerType.MUCUS) }

    MaterialTheme {
        TowerSelectionPanel(
            currentCurrency = 15,
            selectedTowerType = selectedTowerPreview,
            onTowerSelected = { selectedTowerPreview = if (selectedTowerPreview == it) null else it }
        )
    }
} 