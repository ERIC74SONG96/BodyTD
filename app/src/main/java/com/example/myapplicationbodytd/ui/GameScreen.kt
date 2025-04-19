package com.example.myapplicationbodytd.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationbodytd.viewmodels.GameViewModel
import com.example.myapplicationbodytd.viewmodels.GameViewModelFactory
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.map.TileInfo
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.util.Constants
import com.example.myapplicationbodytd.util.CoordinateConverter
//import com.example.myapplicationbodytd.game.map.TileType
import com.example.myapplicationbodytd.R

// TODO: Define these properly, maybe in a constants file
const val GRID_SIZE = 15 // Example: 10x10 grid

//enum class TowerType { MUCUS, MACROPHAGE, COUGH } // Define if not already elsewhere

/**
 * The main screen for the game, integrating the HUD, GameCanvas, and TowerSelectionPanel.
 */
@Composable
fun GameScreen(viewModelFactory: GameViewModelFactory) {
    // Get the ViewModel using the factory
    val gameViewModel: GameViewModel = viewModel(factory = viewModelFactory)

    // Observe ViewModel state using delegates
    val mapState by gameViewModel.map
    val enemies by gameViewModel.enemies
    val towers by gameViewModel.towers
    val currency by gameViewModel.currency
    val lives by gameViewModel.lives
    val wave by gameViewModel.wave
    val placementMode by gameViewModel.placementMode
    val selectedTowerForPlacement by gameViewModel.selectedTowerForPlacement

    // Determine cell size dynamically based on available space
    // Note: cellSize calculation is now handled within GameCanvas based on its constraints
    // We don't need to calculate it explicitly here anymore.
    // var cellSize by remember { mutableFloatStateOf(0f) }
    // var canvasSizePx by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        // Initial game setup if needed (e.g., start game loop via ViewModel)
        Log.d("GameScreen", "GameScreen LaunchedEffect")
        // gameViewModel.startGame() // Example if you have such a method
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top HUD Area
        HUD(
            currency = currency, 
            lives = lives, 
            wave = wave, 
            canStartWave = gameViewModel.canStartNextWave, // Pass the computed property from ViewModel
            onStartWaveClick = {
                Log.d("GameScreen", "Start Wave button clicked, calling viewModel.requestNextWave()")
                gameViewModel.requestNextWave() // Call the RENAMED ViewModel function
            }
        )

        // Game Area (Using GameCanvas)
        Box(
            modifier = Modifier
                .weight(1f) // Takes up remaining space
                .padding(8.dp)
                .aspectRatio(1f), // Maintain square aspect ratio
            contentAlignment = Alignment.Center
        ) {
            // Pass all necessary state to GameCanvas
            GameCanvas(
                modifier = Modifier.fillMaxSize(), // Let GameCanvas fill the Box
                map = mapState,
                enemies = enemies,
                towers = towers,
                placementMode = placementMode,
                selectedTowerType = selectedTowerForPlacement,
                onTileTap = { x, y -> 
                    Log.d("GameScreen", "Tile tap relayed to ViewModel: ($x, $y)")
                    gameViewModel.handleTileTap(x, y) 
                },
                onCellSizeCalculated = { newSize -> 
                    gameViewModel.updateCellSize(newSize) 
                }
            )
        }

        // Bottom Control Area (Tower Selection)
        TowerSelectionPanel(
            selectedTower = selectedTowerForPlacement,
            onTowerSelect = { towerType ->
                gameViewModel.togglePlacementMode(towerType)
            }
        )
    }
}

// --- UI Components ---

@Composable
fun HUD(
    currency: Int, 
    lives: Int, 
    wave: Int, 
    canStartWave: Boolean, // Parameter added previously
    onStartWaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Money: $currency", style = MaterialTheme.typography.bodyLarge)
        Text("Wave: $wave", style = MaterialTheme.typography.bodyLarge)
        Text("Lives: $lives", style = MaterialTheme.typography.bodyLarge)
        
        Button(
            onClick = onStartWaveClick,
            enabled = canStartWave // Enabled state set previously
        ) {
            val buttonText = if (wave == 0) "Start Game" else "Start Wave ${wave + 1}" 
            Text(buttonText)
        }
    }
}

@Composable
fun TowerSelectionPanel(
    selectedTower: TowerType?,
    onTowerSelect: (TowerType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TowerType.entries.forEach { towerType ->
            TowerButton(
                towerType = towerType,
                isSelected = selectedTower == towerType,
                onClick = { onTowerSelect(towerType) }
            )
        }
    }
}

@Composable
fun TowerButton(
    towerType: TowerType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Basic Button implementation - enhance with icons, cost display etc.
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
        // Highlight if selected
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(towerType.name.take(3)) // Short name for button
        // TODO: Add cost display? (e.g., Text("$${getTowerCost(towerType)}")) - requires cost logic access
    }
}

// Helper to get cost (might need ViewModel access or pass costs down)
fun getTowerCost(towerType: TowerType): Int {
     return when (towerType) {
         TowerType.MUCUS -> com.example.myapplicationbodytd.game.entities.MucusTower.COST // Replace with actual cost access
         TowerType.MACROPHAGE -> com.example.myapplicationbodytd.game.entities.MacrophageTower.COST
         TowerType.COUGH -> com.example.myapplicationbodytd.game.entities.CoughTower.COST
     }
}

// Remove the old @Preview as it likely won't work without a Factory
// @Preview(showBackground = true)
// @Composable
// fun GameScreenPreview() {
//    MaterialTheme {
//        // Preview might need adjustments if ViewModel provides initial state
//        // or requires specific setup.
//        // GameScreen() // Keeping simple preview for now - THIS WON'T WORK
//    }
// } 