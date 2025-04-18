package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.viewmodels.GameViewModel

/**
 * The main screen for the game, integrating the HUD, GameCanvas, and TowerSelectionPanel.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel() // Get instance of ViewModel
) {
    // Observe state from ViewModel
    val currency = gameViewModel.currency
    val remainingLives = remember { mutableIntStateOf(3) } // Placeholder - Get from ViewModel later
    val currentWave = remember { mutableIntStateOf(1) } // Placeholder - Get from ViewModel later
    val placementMode = gameViewModel.placementMode
    val selectedTower = gameViewModel.selectedTowerForPlacement

    // Placeholder data for GameCanvas - Replace with actual state from ViewModel/GameManager
    val map = remember { Map() } 
    val enemies = remember { mutableStateOf(emptyList<Enemy>()) }
    val towers = remember { mutableStateOf(emptyList<Tower>()) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Game Canvas fills most space
            GameCanvas(
                modifier = Modifier.weight(1f), // Takes up available space
                map = map,
                enemies = enemies.value,
                towers = towers.value,
                placementMode = placementMode.value,
                selectedTowerType = selectedTower.value,
                onTileTap = { tileX, tileY -> gameViewModel.handleTileTap(tileX, tileY) } // Connect tap handler
            )
            // HUD at the top (or could be overlay)
            HUD(
                currencyState = currency,
                livesState = remainingLives, // Pass placeholder state
                waveState = currentWave // Pass placeholder state
            )
            // Tower Selection Panel at the bottom
            TowerSelectionPanel(
                currentCurrencyState = currency,
                selectedTowerTypeState = selectedTower,
                onTowerSelected = { towerType -> 
                    gameViewModel.togglePlacementMode(towerType) // Connect selection handler
                } 
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MaterialTheme {
        GameScreen()
    }
} 