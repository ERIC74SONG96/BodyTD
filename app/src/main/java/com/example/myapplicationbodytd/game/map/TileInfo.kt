package com.example.myapplicationbodytd.game.map

/**
 * Represents the properties of a single tile on the game map.
 *
 * @property isPlaceable Indicates if a tower can be placed on this tile.
 * @property isPath Indicates if this tile is part of the enemy path.
 */
data class TileInfo(
    var isPlaceable: Boolean = false,
    var isPath: Boolean = false
) {
    init {
        // Ensure a tile cannot be both placeable and part of the path
        require(!(isPlaceable && isPath)) { "Tile cannot be both placeable and path." }
    }

    /**
     * Checks if a tower can be placed on this tile.
     * A tower can only be placed if the tile is marked as placeable and is not part of the path.
     */
    fun canPlaceTower(): Boolean {
        return isPlaceable && !isPath
    }

    /**
     * Sets the tile as part of the path.
     * Automatically ensures it's not placeable.
     */
    fun setAsPath() {
        isPath = true
        isPlaceable = false
    }

    /**
     * Sets the tile as placeable for towers.
     * Automatically ensures it's not part of the path.
     */
    fun setAsPlaceable() {
        isPlaceable = true
        isPath = false
    }

    /**
     * Sets the tile as empty (neither path nor placeable).
     */
    fun setAsEmpty(){
        isPlaceable = false
        isPath = false
    }
} 