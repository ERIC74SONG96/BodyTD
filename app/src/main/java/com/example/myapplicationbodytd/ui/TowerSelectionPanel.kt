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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import com.example.myapplicationbodytd.game.entities.TowerType // Assuming TowerType enum exists

// Define costs here temporarily, ideally fetch from game data
val towerCosts = mapOf(
    TowerType.MUCUS to 10,
    TowerType.MACROPHAGE to 20,
    TowerType.COUGH to 10
)

/**
 * Composable panel for selecting towers to place.
 *
 * @param currentCurrencyState The player's current currency.
 * @param selectedTowerTypeState The currently selected tower type for placement.
 * @param onTowerSelected Callback function when a tower button is clicked.
 */
@Composable
fun TowerSelectionPanel(
    modifier: Modifier = Modifier,
    currentCurrencyState: State<Int>,
    selectedTowerTypeState: State<TowerType?>,
    onTowerSelected: (TowerType) -> Unit
) {
    val currentCurrency = currentCurrencyState.value
    val selectedTowerType = selectedTowerTypeState.value

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
                TowerType.values().forEach { towerType ->
                    TowerButton(
                        towerType = towerType,
                        cost = towerCosts[towerType] ?: 0,
                        currentCurrency = currentCurrency,
                        isSelected = selectedTowerType == towerType,
                        onClick = { onTowerSelected(towerType) }
                    )
                }
            }
        }
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

// Preview requires a dummy TowerType enum if it doesn't exist
enum class TowerType { MUCUS, MACROPHAGE, COUGH }

@Preview(showBackground = true)
@Composable
fun TowerSelectionPanelPreview() {
    val currency = remember { mutableIntStateOf(15) }
    val selectedTower = remember { mutableStateOf<TowerType?>(TowerType.MUCUS) }

    MaterialTheme {
        TowerSelectionPanel(
            currentCurrencyState = currency,
            selectedTowerTypeState = selectedTower,
            onTowerSelected = { selectedTower.value = if (selectedTower.value == it) null else it }
        )
    }
} 