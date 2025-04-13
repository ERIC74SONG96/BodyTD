package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Represents a cell in the game grid
 */
enum class CellType {
    PATH,      // Part of the enemy path
    BUILDABLE, // Can place towers here
    BLOCKED    // Cannot place towers here
}

/**
 * Main map class that handles the game grid and path
 */
class GameMap(
    val width: Int = 18,    // Grid width in cells
    val height: Int = 8,    // Grid height in cells
    val cellSize: Float = 60f // Size of each cell in pixels
) {
    // The game grid
    private val grid: Array<Array<CellType>> = Array(height) { Array(width) { CellType.BUILDABLE } }
    
    // Path waypoints (will be used by enemies)
    private val waypoints: MutableList<Pair<Int, Int>> = mutableListOf()
    
    init {
        // Create a simple default path
        createDefaultPath()
    }

    private fun createDefaultPath() {
        // Simple path from left to right
        val pathPoints = listOf(
            0 to 4,  // Start
            2 to 4,
            2 to 2,
            6 to 2,
            6 to 6,
            9 to 6,
            9 to 3,
            17 to 3
            // End
        )
        
        // Set path points
        pathPoints.forEach { (x, y) ->
            if (x in 0 until width && y in 0 until height) {
                grid[y][x] = CellType.PATH
                waypoints.add(x to y)
            }
        }
        
        // Connect path points
        for (i in 0 until pathPoints.size - 1) {
            val (x1, y1) = pathPoints[i]
            val (x2, y2) = pathPoints[i + 1]
            
            // Fill in cells between waypoints
            if (x1 == x2) {
                // Vertical line
                val minY = minOf(y1, y2)
                val maxY = maxOf(y1, y2)
                for (y in minY..maxY) {
                    grid[y][x1] = CellType.PATH
                }
            } else if (y1 == y2) {
                // Horizontal line
                val minX = minOf(x1, x2)
                val maxX = maxOf(x1, x2)
                for (x in minX..maxX) {
                    grid[y1][x] = CellType.PATH
                }
            }
        }
    }

    /**
     * Check if a tower can be placed at the given grid position
     */
    fun canPlaceTower(x: Int, y: Int): Boolean {
        return x in 0 until width && 
               y in 0 until height && 
               grid[y][x] == CellType.BUILDABLE
    }

    /**
     * Get the cell type at the given position
     */
    fun getCellType(x: Int, y: Int): CellType? {
        return if (x in 0 until width && y in 0 until height) {
            grid[y][x]
        } else null
    }

    /**
     * Get the waypoints for enemy pathfinding
     */
    fun getWaypoints(): List<Pair<Int, Int>> = waypoints.toList()
}

/**
 * Composable that renders the game map
 */
@Composable
fun GameMapView(
    gameMap: GameMap,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawMap(gameMap)
    }
}

/**
 * Extension function to draw the map
 */
private fun DrawScope.drawMap(gameMap: GameMap) {
    val cellSize = minOf(
        size.width / gameMap.width,
        size.height / gameMap.height
    )

    // Draw grid
    for (y in 0 until gameMap.height) {
        for (x in 0 until gameMap.width) {
            val cellType = gameMap.getCellType(x, y)
            val color = when (cellType) {
                CellType.PATH -> Color(0xFF8B4513) // Brown path
                CellType.BUILDABLE -> Color(0xFF90EE90) // Light green
                CellType.BLOCKED -> Color(0xFFDC143C) // Red
                null -> Color.Gray
            }

            drawRect(
                color = color,
                topLeft = Offset(x * cellSize, y * cellSize),
                size = Size(cellSize, cellSize)
            )

            // Draw grid lines
            drawRect(
                color = Color.Black.copy(alpha = 0.1f),
                topLeft = Offset(x * cellSize, y * cellSize),
                size = Size(cellSize, cellSize),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
            )
        }
    }
}
