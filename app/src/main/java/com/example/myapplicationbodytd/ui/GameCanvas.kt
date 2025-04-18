package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
//import com.example.myapplicationbodytd.ui.theme.MyApplicationBodyTDTheme
import androidx.compose.ui.graphics.StrokeCap
import com.example.myapplicationbodytd.game.entities.Bacteria
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.FineParticle
import com.example.myapplicationbodytd.game.entities.Virus
import com.example.myapplicationbodytd.game.map.Map // Assuming Map provides grid<->world conversion eventually
import com.example.myapplicationbodytd.game.entities.* // Import all entities
import androidx.compose.ui.graphics.drawscope.Stroke
import android.util.Log // Import Log
//import com.example.myapplicationbodytd.game.entities.TowerType // Assuming TowerType enum exists
import androidx.compose.ui.input.pointer.pointerInput
import com.example.myapplicationbodytd.game.map.TileInfo // Import TileInfo
import com.example.myapplicationbodytd.ui.TowerType // Import the correct UI TowerType

// Colors for different tile types
val pathColor = Color(0xFFC19A6B) // Tan/Brownish
val placeableColor = Color(0xFF8BC34A) // Light Green
val emptyColor = Color(0xFF616161) // Dark Gray
val placeableHighlightColor = Color.Green.copy(alpha = 0.3f)
val nonPlaceableHighlightColor = Color.Red.copy(alpha = 0.3f)

/**
 * Composable that renders the main game area, including the grid, towers, and enemies.
 * @param map The current game map layout.
 * @param enemies The list of enemies in the game.
 * @param towers The list of towers in the game.
 * @param placementMode Whether the game is currently in tower placement mode.
 * @param selectedTowerType The type of tower selected for placement (if in placement mode).
 * @param onTileTap Callback function when a grid tile is tapped.
 */
@Composable
fun GameCanvas(
    modifier: Modifier = Modifier,
    map: Map,
    enemies: List<Enemy>,
    towers: List<Tower>,
    placementMode: Boolean,
    selectedTowerType: TowerType?,
    onTileTap: (x: Int, y: Int) -> Unit // Add callback
) {
    Log.d("GameCanvas", "Recomposing GameCanvas")

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { // Add pointerInput for tap detection
                detectTapGestures {
                    tapOffset ->
                    // Calculate tile size and offset again (needed for tap calculation)
                    // TODO: Avoid recalculating this? Pass as state or remember?
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val tileSizeWidth = canvasWidth / map.width
                    val tileSizeHeight = canvasHeight / map.height
                    val tileSize = minOf(tileSizeWidth, tileSizeHeight)
                    val totalGridWidth = tileSize * map.width
                    val totalGridHeight = tileSize * map.height
                    val offsetX = (canvasWidth - totalGridWidth) / 2f
                    val offsetY = (canvasHeight - totalGridHeight) / 2f

                    // Convert tap offset to grid coordinates
                    val gridX = ((tapOffset.x - offsetX) / tileSize).toInt()
                    val gridY = ((tapOffset.y - offsetY) / tileSize).toInt()

                    // Ensure coordinates are within grid bounds before calling callback
                    if (gridX in 0 until map.width && gridY in 0 until map.height) {
                        onTileTap(gridX, gridY)
                    }
                }
            }
    ) { 
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate tile size based on canvas dimensions and map grid
        val tileSizeWidth = canvasWidth / map.width
        val tileSizeHeight = canvasHeight / map.height
        // Use the smaller dimension to ensure tiles fit and maintain aspect ratio (square tiles)
        val tileSize = minOf(tileSizeWidth, tileSizeHeight)

        // Calculate offsets to center the grid if it doesn't fill the canvas perfectly
        val totalGridWidth = tileSize * map.width
        val totalGridHeight = tileSize * map.height
        val offsetX = (canvasWidth - totalGridWidth) / 2f
        val offsetY = (canvasHeight - totalGridHeight) / 2f

        drawGrid(map, tileSize, offsetX, offsetY)

        // Draw highlights if in placement mode
        if (placementMode && selectedTowerType != null) {
            drawPlacementHighlights(map, towers, tileSize, offsetX, offsetY)
        }

        drawTowers(towers, tileSize, offsetX, offsetY)
        drawEnemies(enemies, tileSize, offsetX, offsetY)
        drawAttackEffects(towers, tileSize, offsetX, offsetY)
    }
}

/**
 * Draws the map grid onto the canvas.
 */
private fun DrawScope.drawGrid(map: Map, tileSize: Float, offsetX: Float, offsetY: Float) {
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

/**
 * Draws the enemies onto the canvas.
 */
private fun DrawScope.drawEnemies(enemies: List<Enemy>, tileSize: Float, offsetX: Float, offsetY: Float) {
    val enemyRadius = tileSize * 0.3f // Example radius based on tile size
    val healthBarWidth = tileSize * 0.6f
    val healthBarHeight = tileSize * 0.1f
    val healthBarOffsetY = enemyRadius * 1.5f // Position above the enemy circle

    enemies.forEach { enemy ->
        if (enemy.isDead) return@forEach // Don't draw dead enemies

        // TODO: Use a proper world coordinate system from Enemy or Map
        // For now, assume enemy.position is already scaled world coords (as implemented in Enemy.updatePosition)
        val worldX = enemy.position.x + offsetX // Adjust for canvas centering
        val worldY = enemy.position.y + offsetY
        val center = Offset(worldX, worldY)

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

/**
 * Draws the towers onto the canvas.
 */
private fun DrawScope.drawTowers(towers: List<Tower>, tileSize: Float, offsetX: Float, offsetY: Float) {
    val towerSize = tileSize * 0.8f // Slightly smaller than tile
    val towerOffset = (tileSize - towerSize) / 2f // Center it in the tile

    towers.forEach { tower ->
        val towerColor = when (tower) {
            is MucusTower -> Color.Blue
            is MacrophageTower -> Color.Cyan
            is CoughTower -> Color.Yellow
            else -> Color.White
        }
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
            radius = tower.range, // Use tower's range property
            center = Offset(topLeft.x + towerSize / 2f, topLeft.y + towerSize / 2f),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Draws placement highlights (green/red overlays) on grid tiles when in placement mode.
 */
private fun DrawScope.drawPlacementHighlights(
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
            // Avoid highlighting path tiles unless needed
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

/**
 * Draws visual effects for tower attacks (e.g., lines to targets).
 */
private fun DrawScope.drawAttackEffects(towers: List<Tower>, tileSize: Float, offsetX: Float, offsetY: Float) {
    towers.forEach { tower ->
        tower.currentTarget?.let { target ->
            // Draw only if the attack effect timer is active and target is still valid
            if (!target.isDead && tower.attackEffectTimer > 0f) {
                val startPoint = tower.worldPosition.let { Offset(it.x + offsetX, it.y + offsetY) }
                val endPoint = target.position.let { Offset(it.x + offsetX, it.y + offsetY) }

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

@Preview(showBackground = true, widthDp = 360, heightDp = 240)
@Composable
fun GameCanvasPreview() {
    MaterialTheme {
        val previewMap = Map()
        val previewEnemies = emptyList<Enemy>() 
        val previewTowers = emptyList<Tower>()

        GameCanvas(
            map = previewMap, 
            enemies = previewEnemies, 
            towers = previewTowers,
            placementMode = true,
            selectedTowerType = TowerType.MUCUS,
            onTileTap = { x, y -> Log.d("Preview", "Tapped tile: ($x, $y)") } // Example callback
        )
    }
}
