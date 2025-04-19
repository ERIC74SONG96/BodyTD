package com.example.myapplicationbodytd.game.map

/**
 * Represents the game map grid and its properties.
 *
 * @property width The width of the map in tiles.
 * @property height The height of the map in tiles.
 * @property grid The 2D list representing the tiles of the map.
 */
class Map(
    val width: Int = 15,
    val height: Int = 15
) {
    // Initialize the grid with default TileInfo objects
    val grid: List<List<TileInfo>> = List(height) { 
        List(width) { TileInfo() } 
    }

    // Path property
    var path: List<Pair<Int, Int>> = emptyList()
        private set // Allow setting only via setPath method

    init {
        // Initialize map with a default configuration when created
        initializeDefaultMap()
    }

    /**
     * Checks if the given coordinates are within the map boundaries.
     *
     * @param x The x-coordinate (column index).
     * @param y The y-coordinate (row index).
     * @return True if the coordinates are valid, false otherwise.
     */
    fun isValidCoordinate(x: Int, y: Int): Boolean {
        return x >= 0 && x < width && y >= 0 && y < height
    }

    /**
     * Gets the TileInfo object at the specified coordinates.
     *
     * @param x The x-coordinate (column index).
     * @param y The y-coordinate (row index).
     * @return The TileInfo object at the coordinates, or null if coordinates are invalid.
     */
    fun getTileAt(x: Int, y: Int): TileInfo? {
        return if (isValidCoordinate(x, y)) {
            grid[y][x]
        } else {
            null
        }
    }

    /**
     * Validates and sets the enemy path on the map.
     * Updates the isPath property of the corresponding tiles.
     *
     * @param newPath A list of coordinate pairs representing the path.
     * @throws IllegalArgumentException If the path is invalid.
     */
    fun setPath(newPath: List<Pair<Int, Int>>) {
        if (!validatePath(newPath)) {
            throw IllegalArgumentException("Invalid path provided.")
        }

        // Reset old path tiles if any
        path.forEach { (x, y) ->
            getTileAt(x, y)?.setAsEmpty() // Reset to empty or potentially placeable
        }

        // Set new path tiles
        newPath.forEach { (x, y) ->
            getTileAt(x, y)?.setAsPath() // Marks as path, sets isPlaceable to false
        }
        this.path = newPath
    }

    /**
     * Validates a given path.
     * Checks if all coordinates are within bounds and if consecutive points are adjacent.
     *
     * @param pathToCheck The path to validate.
     * @return True if the path is valid, false otherwise.
     */
    fun validatePath(pathToCheck: List<Pair<Int, Int>>): Boolean {
        if (pathToCheck.isEmpty()) return false // Path cannot be empty

        for (i in pathToCheck.indices) {
            val (x, y) = pathToCheck[i]
            if (!isValidCoordinate(x, y)) {
                println("Path validation failed: Coordinate ($x, $y) out of bounds.")
                return false // Coordinate out of bounds
            }

            if (i > 0) {
                val (prevX, prevY) = pathToCheck[i - 1]
                val dx = kotlin.math.abs(x - prevX)
                val dy = kotlin.math.abs(y - prevY)
                // Check if points are adjacent (Manhattan distance of 1)
                if (dx + dy != 1) {
                     println("Path validation failed: Points ($prevX, $prevY) and ($x, $y) are not adjacent.")
                    return false
                }
            }
        }
        return true
    }

    /**
     * Gets the starting point of the current path.
     *
     * @return The first coordinate pair in the path, or null if the path is empty.
     */
    fun getPathStartPoint(): Pair<Int, Int>? {
        return path.firstOrNull()
    }

    /**
     * Gets the ending point of the current path.
     *
     * @return The last coordinate pair in the path, or null if the path is empty.
     */
    fun getPathEndPoint(): Pair<Int, Int>? {
        return path.lastOrNull()
    }

    /**
     * Checks if a tower can be placed at the specified coordinates.
     * Verifies that the coordinates are valid and the tile allows placement.
     *
     * @param x The x-coordinate (column index).
     * @param y The y-coordinate (row index).
     * @return True if a tower can be placed, false otherwise.
     */
    fun canPlaceTowerAt(x: Int, y: Int): Boolean {
        val tile = getTileAt(x, y)
        return tile?.canPlaceTower() ?: false
    }

    /**
     * Initializes the map with a default path and placeable areas.
     * Example: A simple L-shaped path and makes other tiles placeable.
     */
    private fun initializeDefaultMap() {
        // Define a simple example path
        val defaultPath = listOf(
            Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(3, 1), Pair(4, 1),
            Pair(4, 2), Pair(4, 3), Pair(4, 4), Pair(4, 5),
            Pair(5, 5), Pair(6, 5), Pair(7, 5), Pair(8, 5), Pair(9, 5),
            Pair(9, 6), Pair(9, 7), Pair(9, 8), Pair(10, 8), Pair(11, 8),
            Pair(12, 8), Pair(13, 8), Pair(14, 8)
        )

        try {
            setPath(defaultPath)
        } catch (e: IllegalArgumentException) {
            println("Error initializing default map path: ${e.message}")
            // Handle error appropriately, maybe define a fallback path
        }

        // Make all non-path tiles placeable by default
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!grid[y][x].isPath) {
                    grid[y][x].setAsPlaceable()
                }
            }
        }
    }

    // TODO: Consider loading map layouts from files later
} 