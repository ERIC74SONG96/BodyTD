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
    var cellSize by remember { mutableFloatStateOf(0f) }
    var canvasSizePx by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        // Initial game setup if needed (e.g., start game loop via ViewModel)
        Log.d("GameScreen", "GameScreen LaunchedEffect")
        // gameViewModel.startGame() // Example if you have such a method
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top HUD Area
        HUD(currency = currency, lives = lives, wave = wave)

        // Game Area (Canvas)
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f) // Takes up remaining space
                .padding(8.dp)
                .aspectRatio(1f), // Maintain square aspect ratio
            contentAlignment = Alignment.Center
        ) {
            val constraints = this.constraints
            val maxCanvasWidthPx = constraints.maxWidth.toFloat()
            val maxCanvasHeightPx = constraints.maxHeight.toFloat()
            val actualCanvasSizePx = minOf(maxCanvasWidthPx, maxCanvasHeightPx)
            canvasSizePx = Size(actualCanvasSizePx, actualCanvasSizePx) // Update canvas size state
            cellSize = actualCanvasSizePx / GRID_SIZE // Update cell size state

            Canvas(
                modifier = Modifier
                    .size(actualCanvasSizePx.dp) // Use the calculated pixel size converted to DP? Check this. Use size in Px?
                    // Set the size using pixels directly if needed, or calculate DP equivalent
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (cellSize > 0) {
                                val gridX = (offset.x / cellSize).toInt()
                                val gridY = (offset.y / cellSize).toInt()
                                Log.d("GameScreen", "Canvas tapped at Offset: $offset -> Grid: ($gridX, $gridY)")
                                if (gridX in 0 until GRID_SIZE && gridY in 0 until GRID_SIZE) {
                                    gameViewModel.handleTileTap(gridX, gridY)
                                } else {
                                     Log.w("GameScreen", "Tap outside defined grid bounds ignored.")
                                }
                            }
                        }
                    }
            ) {
                drawGrid(cellSize)
                drawMap(mapState, cellSize)
                drawEnemies(enemies, cellSize)
                drawTowers(towers, cellSize)

                // Draw placement indicator if in placement mode
                if (placementMode && selectedTowerForPlacement != null) {
                    // TODO: Improve placement indicator - maybe draw a semi-transparent tower?
                    // For now, just draw a simple marker (e.g., a circle or highlight)
                    // This needs the current pointer location - might need state hoisting or different approach
                }
            }
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

// --- Drawing Functions ---

fun DrawScope.drawGrid(cellSize: Float) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    // Draw vertical lines
    for (i in 0..GRID_SIZE) {
        val x = i * cellSize
        drawLine(
            color = Color.LightGray,
            start = Offset(x, 0f),
            end = Offset(x, canvasHeight),
            strokeWidth = 1f
        )
    }
    // Draw horizontal lines
    for (i in 0..GRID_SIZE) {
        val y = i * cellSize
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 1f
        )
    }
}

fun DrawScope.drawMap(map: Map, cellSize: Float) {
    val pathStart = map.getPathStartPoint()
    val pathEnd = map.getPathEndPoint()

    map.grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, tileInfo ->
            val color = when {
                // Check if current tile is the start of the path
                pathStart?.first == x && pathStart?.second == y -> Color.Blue
                // Check if current tile is the end of the path
                pathEnd?.first == x && pathEnd?.second == y -> Color.Red
                // Check if it's part of the path (and not start/end)
                tileInfo.isPath -> Color(0xFFD2B48C) // Tan/Brownish for path
                // Check if it's placeable
                tileInfo.isPlaceable -> Color(0xFF90EE90) // LightGreen for buildable
                // Default color for non-path, non-placeable tiles (obstacles/empty)
                else -> Color.Gray
            }
            drawRect(
                color = color,
                topLeft = Offset(x * cellSize, y * cellSize),
                size = Size(cellSize, cellSize)
            )
            // Optional: Draw borders for clarity
            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(x * cellSize, y * cellSize),
                size = Size(cellSize, cellSize),
                style = Stroke(width = 0.5f) // Thin border
            )
        }
    }
}

fun DrawScope.drawEnemies(enemies: List<Enemy>, cellSize: Float) {
    enemies.forEach { enemy ->
        // Use enemy's precise position (which should be updated by GameManager)
        // Convert precise position (Offset) to canvas coordinates
        // Assuming enemy.position.x/y are world coordinates already or need scaling
        // TODO: Verify how enemy.position relates to world/canvas coordinates
        val canvasX = enemy.position.x // Access Offset property .x
        val canvasY = enemy.position.y // Access Offset property .y
        val enemySize = cellSize * 0.6f // Make enemy slightly smaller than cell
        // Calculate top-left for drawing shapes if needed, but drawCircle uses center
        // val topLeftX = canvasX - enemySize / 2
        // val topLeftY = canvasY - enemySize / 2

        // TODO: Differentiate enemy types visually
        val color = when (enemy.javaClass.simpleName) { // Example differentiation
             "VirusEnemy" -> Color.Magenta
             "BacteriaEnemy" -> Color.Yellow
             else -> Color(0xFFDC143C) // Default Crimson Red
        }

        drawCircle(
            color = color,
            radius = enemySize / 2,
            center = Offset(canvasX, canvasY) // Use direct position for center
        )
        // Draw health bar (optional)
        if (enemy.maxHealth > 0f) { // Avoid division by zero
            val healthPercentage = enemy.health / enemy.maxHealth // Use enemy.health
            val healthBarWidth = enemySize * 0.8f
            val healthBarHeight = enemySize * 0.1f
            // Calculate top-left offset for the health bar background
            val healthBarTopLeft = Offset(
                x = canvasX - healthBarWidth / 2,
                y = canvasY - enemySize / 2 - healthBarHeight * 1.5f // Position above circle
            )
            // Draw background bar (red)
            drawRect(
                color = Color.Red,
                topLeft = healthBarTopLeft,
                size = Size(healthBarWidth, healthBarHeight),
                style = Stroke(width = 1f)
            )
            // Draw foreground health bar (green)
            drawRect(
                color = Color.Green,
                topLeft = healthBarTopLeft,
                size = Size(healthBarWidth * healthPercentage, healthBarHeight)
            )
        } // else { Log.w("DrawEnemies", "Enemy has zero max health?") }
    }
}

fun DrawScope.drawTowers(towers: List<Tower>, cellSize: Float) {
    towers.forEach { tower ->
        // Use tower.position (Pair<Int, Int>) for grid position
        val centerX = (tower.position.first + 0.5f) * cellSize // Access Pair property .first
        val centerY = (tower.position.second + 0.5f) * cellSize // Access Pair property .second
        val towerSize = cellSize * 0.8f // Make tower slightly smaller than cell

        // Differentiate tower types visually
        val (color, shape) = when (tower) {
            is com.example.myapplicationbodytd.game.entities.MucusTower -> Pair(Color(0xFFADD8E6), "Rect") // Light Blue Rect
            is com.example.myapplicationbodytd.game.entities.MacrophageTower -> Pair(Color.White, "Circle") // White Circle
            is com.example.myapplicationbodytd.game.entities.CoughTower -> Pair(Color.Cyan, "Triangle") // Cyan Triangle (approximate with path)
            else -> Pair(Color.DarkGray, "Rect") // Default
        }

        when (shape) {
             "Rect" -> drawRect(
                 color = color,
                 // Calculate top-left offset for rectangle
                 topLeft = Offset(centerX - towerSize / 2, centerY - towerSize / 2),
                 size = Size(towerSize, towerSize)
             )
             "Circle" -> drawCircle(
                 color = color,
                 radius = towerSize / 2,
                 // Use calculated center directly
                 center = Offset(centerX, centerY)
             )
             // Add other shapes like Triangle if needed
        }

        // Optional: Draw range indicator when selected or always
        // drawCircle(
        //     color = Color(0x330000FF), // Semi-transparent blue
        //     radius = tower.range * cellSize, // Assuming range is in grid units
        //     center = Offset(centerX, centerY),
        //     style = Stroke(width = 1f)
        // )
    }
}

// --- UI Components ---

@Composable
fun HUD(currency: Int, lives: Int, wave: Int) {
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