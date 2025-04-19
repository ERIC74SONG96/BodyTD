package com.example.myapplicationbodytd.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Object providing utility functions for converting between grid coordinates and world coordinates.
 * This centralizes coordinate conversion logic used throughout the game.
 */
object CoordinateConverter {
    // Default tile size if not specified (can be removed or kept as fallback)
    const val DEFAULT_TILE_SIZE = Constants.DEFAULT_TILE_SIZE // KEEPING for now

    /**
     * Converts grid coordinates to world coordinates (center of the tile).
     * Uses the provided tileSize.
     */
    fun gridToWorld(
        gridX: Int,
        gridY: Int,
        tileSize: Float, // Remove default, force caller to provide it
        offsetX: Float = 0f,
        offsetY: Float = 0f
    ): Offset {
        return Offset(
            x = gridX * tileSize + tileSize / 2 + offsetX,
            y = gridY * tileSize + tileSize / 2 + offsetY
        )
    }

    /**
     * Converts grid coordinates to world coordinates (top-left corner).
     * Uses the provided tileSize.
     */
    fun gridToWorldTopLeft(
        gridX: Int,
        gridY: Int,
        tileSize: Float, // Remove default
        offsetX: Float = 0f,
        offsetY: Float = 0f
    ): Offset {
        return Offset(
            x = gridX * tileSize + offsetX,
            y = gridY * tileSize + offsetY
        )
    }

    /**
     * Converts world coordinates to grid coordinates.
     * Uses the provided tileSize.
     */
    fun worldToGrid(
        worldX: Float,
        worldY: Float,
        tileSize: Float, // Remove default
        offsetX: Float = 0f,
        offsetY: Float = 0f
    ): Pair<Int, Int> {
        // Avoid division by zero if tileSize is somehow invalid
        if (tileSize <= 0f) {
             // Log error or return default/invalid coords
             android.util.Log.e("CoordinateConverter", "worldToGrid called with invalid tileSize: $tileSize")
             return Pair(-1, -1)
        }
        return Pair(
            ((worldX - offsetX) / tileSize).toInt(),
            ((worldY - offsetY) / tileSize).toInt()
        )
    }

    /**
     * Converts a grid dimension to a world dimension.
     * Uses the provided tileSize.
     */
    fun gridToWorldSize(
        gridSize: Float,
        tileSize: Float // Remove default
    ): Float {
        return gridSize * tileSize
    }

    /**
     * Calculates the squared distance between two points in world coordinates.
     * Useful for efficient range checking without square root.
     */
    fun distanceSquared(point1: Offset, point2: Offset): Float {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        return dx * dx + dy * dy
    }
} 