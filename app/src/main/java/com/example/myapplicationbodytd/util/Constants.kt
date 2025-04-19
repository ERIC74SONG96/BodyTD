package com.example.myapplicationbodytd.util

/**
 * Object holding constants used throughout the application.
 */
object Constants {
    // Grid and Map Constants
    const val GRID_SIZE = 15 // Default grid size for the game map
    const val DEFAULT_TILE_SIZE = 50f // Default size of a tile in world units
    
    // Game Rules
    const val MAX_LIVES = 3 // Number of enemies that can reach the end before game over
    const val STARTING_CURRENCY = 100 // Starting currency for the player
    
    // Wave Constants
    const val WAVES_TO_WIN = 3 // Number of waves to complete to win the game
    const val WAVE_COOLDOWN = 5f // Time between waves in seconds
    
    // Visual Constants
    const val ATTACK_EFFECT_DURATION = 0.15f // Duration of attack visual effects in seconds
    const val ENEMY_RELATIVE_SIZE = 0.6f // Enemy size relative to tile size
    const val TOWER_RELATIVE_SIZE = 0.8f // Tower size relative to tile size
} 