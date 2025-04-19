package com.example.myapplicationbodytd.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationbodytd.game.entities.Bacteria
import com.example.myapplicationbodytd.game.entities.CoughTower
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.FineParticle
import com.example.myapplicationbodytd.game.entities.MacrophageTower
import com.example.myapplicationbodytd.game.entities.MucusTower
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.entities.Virus
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.game.states.GameState
import com.example.myapplicationbodytd.util.Constants
import com.example.myapplicationbodytd.util.CoordinateConverter
import com.example.myapplicationbodytd.viewmodels.GameViewModel
import com.example.myapplicationbodytd.viewmodels.GameViewModelFactory
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

//enum class TowerType { MUCUS, MACROPHAGE, COUGH } // Define if not already elsewhere

/**
 * The main screen for the game, integrating the HUD, GameCanvas, and TowerSelectionPanel.
 */
@Composable
fun GameScreen(viewModelFactory: GameViewModelFactory) {
    // Get the ViewModel using the factory
    val gameViewModel: GameViewModel = viewModel(factory = viewModelFactory)

    // --- Observe ViewModel State --- 
    val enemies = gameViewModel.enemies
    val towers = gameViewModel.towers
    val lives by gameViewModel.lives.collectAsState()
    val currency by gameViewModel.currency.collectAsState()
    val currentWave by gameViewModel.currentWave.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState()
    val drawTick by gameViewModel.drawTick.collectAsState() 
    val isGameOver by gameViewModel.isGameOver.collectAsState() // Observe Game Over state
    val waveClearMessage by gameViewModel.waveClearMessage.collectAsState() // Observe message

    val placementMode by gameViewModel.placementMode
    val selectedTowerType by gameViewModel.selectedTowerType
    val map = gameViewModel.gameMap
    val maxWaves = gameViewModel.maxWaves
    var lastReportedCellSize by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        Log.d("GameScreen", "GameScreen LaunchedEffect")
    }

    // Use a Box to allow overlaying the Game Over UI
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top HUD Area
            HUD(
                currency = currency, 
                lives = lives, 
                wave = currentWave, 
                gameState = gameState, 
                onStartWaveClick = { 
                    gameViewModel.requestNextWave()
                }
            )

            // Game Area (Canvas)
            Box(
                modifier = Modifier
                    .weight(1f) // Takes up remaining space
                    .padding(8.dp)
                    .aspectRatio(1f), // Maintain square aspect ratio
                contentAlignment = Alignment.Center
            ) {
                Canvas( 
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { 
                            detectTapGestures { tapOffset ->
                                val canvasWidth = size.width.toFloat()
                                val canvasHeight = size.height.toFloat()
                                val tileSizeWidth = canvasWidth / map.width
                                val tileSizeHeight = canvasHeight / map.height
                                val tileSize = minOf(tileSizeWidth, tileSizeHeight)
                                val totalGridWidth = tileSize * map.width
                                val totalGridHeight = tileSize * map.height
                                val offsetX = (canvasWidth - totalGridWidth) / 2f
                                val offsetY = (canvasHeight - totalGridHeight) / 2f

                                if (tileSize > 0f) {
                                    val gridCoords = CoordinateConverter.worldToGrid(
                                        tapOffset.x,
                                        tapOffset.y,
                                        tileSize,
                                        offsetX,
                                        offsetY
                                    )

                                    Log.d("GameScreen", "Canvas tapped at Offset: $tapOffset -> Grid: $gridCoords")
                                    if (gridCoords.first in 0 until map.width && 
                                        gridCoords.second in 0 until map.height) {
                                        gameViewModel.handleTileTap(gridCoords.first, gridCoords.second)
                                    } else {
                                        Log.w("GameScreen", "Tap outside defined grid bounds ignored.")
                                    }
                                }
                            }
                        }
                ) {
                    val currentTick = drawTick
                    Log.v("GameScreenCanvas", "Redrawing canvas on Tick: $currentTick")

                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val tileSizeWidth = canvasWidth / map.width
                    val tileSizeHeight = canvasHeight / map.height
                    val tileSize = minOf(tileSizeWidth, tileSizeHeight)
                    val totalGridWidth = tileSize * map.width
                    val totalGridHeight = tileSize * map.height
                    val offsetX = (canvasWidth - totalGridWidth) / 2f
                    val offsetY = (canvasHeight - totalGridHeight) / 2f

                    if (tileSize > 0f && tileSize != lastReportedCellSize) {
                        Log.d("GameScreenCanvas", "Reporting new cell size: $tileSize (was $lastReportedCellSize)")
                        gameViewModel.updateCellSize(tileSize)
                        lastReportedCellSize = tileSize 
                    }

                    drawGridInternal(map, tileSize, offsetX, offsetY)
                    if (placementMode && selectedTowerType != null) {
                        drawPlacementHighlightsInternal(map, towers, tileSize, offsetX, offsetY)
                    }
                    drawTowersInternal(towers, tileSize, offsetX, offsetY)
                    drawEnemiesInternal(enemies, tileSize, offsetX, offsetY)
                    drawAttackEffectsInternal(towers, offsetX, offsetY)
                }
            }

            // Bottom Control Area (Tower Selection)
            TowerSelectionPanel(
                currentCurrency = currency,
                selectedTowerType = selectedTowerType,
                onTowerSelected = { towerType: TowerType -> gameViewModel.togglePlacementMode(towerType) },
                gameViewModel = gameViewModel
            )
        }

        // Wave Cleared Message Overlay (Conditionally displayed)
        waveClearMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp), // Adjust padding to position above tower panel
                contentAlignment = Alignment.Center
            ) {
                 // Box with semi-transparent background for better readability
                 Box(
                     modifier = Modifier
                         .wrapContentSize()
                         .background(Color.Black.copy(alpha = 0.6f), shape = MaterialTheme.shapes.medium)
                         .padding(horizontal = 16.dp, vertical = 8.dp)
                 ) {
                    Text(
                        text = message,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                 }
            }
        }

        // Game Over / Win Overlay (Conditionally displayed)
        if (isGameOver) {
            val overlayText = when (gameState) {
                is com.example.myapplicationbodytd.game.states.WonState -> "You Won!"
                is com.example.myapplicationbodytd.game.states.LostState -> "Game Over"
                else -> "Game Finished" // Fallback, should not happen if isGameOver is true
            }
            val overlayColor = when (gameState) {
                is com.example.myapplicationbodytd.game.states.WonState -> Color.Green
                is com.example.myapplicationbodytd.game.states.LostState -> Color.Red
                else -> Color.White
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)), // Semi-transparent background
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = overlayText,
                        style = MaterialTheme.typography.headlineLarge,
                        color = overlayColor // Use dynamic color
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { gameViewModel.restartGame() }) {
                        Text("Restart")
                    }
                    // Add Quit button if needed
                }
            }
        }
    }
}

// --- Drawing Functions (moved from GameCanvas.kt to GameScreen.kt) ---
// Make them private or internal if desired

// Colors (copied from GameCanvas.kt)
private val pathColor = Color(0xFFC19A6B) // Tan/Brownish
private val placeableColor = Color(0xFF8BC34A) // Light Green
private val emptyColor = Color(0xFF616161) // Dark Gray
private val placeableHighlightColor = Color.Green.copy(alpha = 0.3f)
private val nonPlaceableHighlightColor = Color.Red.copy(alpha = 0.3f)

private fun DrawScope.drawGridInternal(map: Map, tileSize: Float, offsetX: Float, offsetY: Float) {
    for (y in 0 until map.height) {
        for (x in 0 until map.width) {
            val tile = map.grid[y][x]
            val color = when {
                tile.isPath -> pathColor
                tile.isPlaceable -> placeableColor
                else -> emptyColor
            }
            val topLeft = Offset(offsetX + x * tileSize, offsetY + y * tileSize)
            drawRect(
                color = color,
                topLeft = topLeft,
                size = Size(tileSize, tileSize)
            )
            // Optionally draw grid lines
            // drawRect(color = Color.DarkGray, topLeft = topLeft, size = Size(tileSize, tileSize), style = Stroke(width = 1f))
        }
    }
}

private fun DrawScope.drawEnemiesInternal(enemies: List<Enemy>, tileSize: Float, offsetX: Float, offsetY: Float) {
    // Use Constants for enemy size calculations
    val enemyRadius = tileSize * Constants.ENEMY_RELATIVE_SIZE / 2f 
    val healthBarWidth = tileSize * Constants.ENEMY_RELATIVE_SIZE
    val healthBarHeight = tileSize * 0.1f // Keep height relative to tile size for now
    val healthBarOffsetY = enemyRadius * 1.5f // Position above the enemy circle

    enemies.forEach { enemy ->
        if (enemy.isDead) return@forEach // Don't draw dead enemies

        // Use the enemy's world position directly (relative to top-left of game area)
        // Add canvas offset (offsetX, offsetY) to position it correctly within the centered Canvas
        val center = Offset(enemy.position.x + offsetX, enemy.position.y + offsetY)

        // Determine color based on enemy type
        val enemyColor = when (enemy) {
            is Virus -> Color.Magenta
            is Bacteria -> Color.Red
            is FineParticle -> Color.Gray
            else -> Color.Black
        }

        // Draw enemy circle
        drawCircle(
            color = enemyColor,
            radius = enemyRadius,
            center = center
        )

        // Draw health bar
        if (enemy.health < enemy.maxHealth) {
            val healthPercentage = enemy.health / enemy.maxHealth
            val currentHealthWidth = healthBarWidth * healthPercentage
            val healthBarTopLeft = Offset(
                center.x - healthBarWidth / 2f,
                center.y - healthBarOffsetY - healthBarHeight / 2f
            )
            // Draw health bar background (e.g., dark red)
            drawRect(
                color = Color.Red.copy(alpha = 0.5f),
                topLeft = healthBarTopLeft,
                size = Size(healthBarWidth, healthBarHeight)
            )
            // Draw current health (e.g., green)
            drawRect(
                color = Color.Green,
                topLeft = healthBarTopLeft,
                size = Size(currentHealthWidth, healthBarHeight)
            )
        }
    }
}

private fun DrawScope.drawTowersInternal(towers: List<Tower>, tileSize: Float, offsetX: Float, offsetY: Float) {
    // Use Constants for tower size
    val towerSize = tileSize * Constants.TOWER_RELATIVE_SIZE 
    val towerOffset = (tileSize - towerSize) / 2f // Center it in the tile

    towers.forEach { tower ->
        val towerColor = when (tower) {
            is MucusTower -> Color.Blue
            is MacrophageTower -> Color.Cyan
            is CoughTower -> Color.Yellow
            else -> Color.White
        }
        // Tower position is grid-based, convert to world/canvas coordinates
        val topLeft = Offset(
            offsetX + tower.position.first * tileSize + towerOffset,
            offsetY + tower.position.second * tileSize + towerOffset
        )

        // Draw tower square
        drawRect(
            color = towerColor,
            topLeft = topLeft,
            size = Size(towerSize, towerSize)
        )

        // Draw tower range indicator (optional)
        drawCircle(
            color = towerColor.copy(alpha = 0.3f),
            radius = tower.range, // Use tower's range property (already in world units)
            center = Offset(topLeft.x + towerSize / 2f, topLeft.y + towerSize / 2f),
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawPlacementHighlightsInternal(
    map: Map,
    towers: List<Tower>, // Need tower list to check occupied tiles
    tileSize: Float,
    offsetX: Float,
    offsetY: Float
) {
    val occupiedTiles = towers.map { it.position }.toSet()

    for (y in 0 until map.height) {
        for (x in 0 until map.width) {
            val tile = map.grid[y][x]
            val position = Pair(x, y)
            val isOccupied = occupiedTiles.contains(position)

            val highlightColor = when {
                tile.isPlaceable && !isOccupied -> placeableHighlightColor
                else -> nonPlaceableHighlightColor // Highlight non-placeable or occupied tiles red
            }

            // Only draw highlight if the tile is relevant for placement (placeable or not)
            if (tile.isPlaceable || isOccupied) { // Or adjust condition as needed
                 val topLeft = Offset(offsetX + x * tileSize, offsetY + y * tileSize)
                 drawRect(
                     color = highlightColor,
                     topLeft = topLeft,
                     size = Size(tileSize, tileSize)
                 )
             }
        }
    }
}

private fun DrawScope.drawAttackEffectsInternal(towers: List<Tower>, offsetX: Float, offsetY: Float) {
    towers.forEach { tower ->
        tower.currentTarget?.let { target ->
            // Draw only if the attack effect timer is active and target is still valid
            if (!target.isDead && tower.attackEffectTimer > 0f) {
                // Use world positions (relative to game area top-left)
                // Add canvas offsets to position correctly within the centered Canvas
                val startPoint = Offset(tower.worldPosition.x + offsetX, tower.worldPosition.y + offsetY)
                val endPoint = Offset(target.position.x + offsetX, target.position.y + offsetY)

                val lineColor = when (tower) {
                    is MucusTower -> Color.Cyan
                    is MacrophageTower -> Color.Red
                    is CoughTower -> Color.White
                    else -> Color.LightGray
                }

                drawLine(
                    color = lineColor,
                    start = startPoint,
                    end = endPoint,
                    strokeWidth = 4f, // Make line thicker
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// --- UI Components ---

@Composable
fun HUD(
    currency: Int, 
    lives: Int, 
    wave: Int,
    gameState: GameState?, // Receive gameState
    onStartWaveClick: () -> Unit // Receive callback
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

        // Add the Start Wave Button back
        Button(
            onClick = onStartWaveClick,
            // Enable only when the game state allows starting a wave (e.g., after clearing one)
            // Or potentially in an initial ready state before wave 1
            enabled = gameState is com.example.myapplicationbodytd.game.states.WaveClearedState || (wave == 0 && gameState is com.example.myapplicationbodytd.game.states.InitializingState) // Adjust based on your state machine logic
        ) {
            val buttonText = if (wave == 0) "Start Game" else "Start Wave ${wave + 1}"
            Text(buttonText)
        }
    }
}

@Composable
fun TowerSelectionPanel(
    currentCurrency: Int,
    selectedTowerType: TowerType?,
    onTowerSelected: (TowerType) -> Unit,
    gameViewModel: GameViewModel
) {
    Card(
        modifier = Modifier
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
                    val cost = gameViewModel.getTowerCost(towerType) ?: 0

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