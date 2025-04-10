package com.example.myapplicationbodytd.enemies

/**
 * Enum representing different types of enemies
 */
enum class EnemyType {
    VIRUS,
    BACTERIA,
    FINE_PARTICLE
}

/**
 * Abstract base class for all enemies in the game.
 * Each enemy has health, speed, damage, reward, and position properties.
 */
abstract class Enemy(
    var health: Int,
    val speed: Float,
    val damage: Int,
    val reward: Int,
    var position: Int = 0 // Position on the path
) {
    // Path properties
    private val pathLength: Int = 100 // Total length of the path
    private var hasReachedEndFlag: Boolean = false
    
    /**
     * Abstract method for enemy-specific attack behavior
     */
    abstract fun attack()
    
    /**
     * Moves the enemy along the path
     */
    open fun move() {
        position += speed.toInt()
        
        // Check if enemy has reached the end of the path
        if (position >= pathLength) {
            hasReachedEndFlag = true
        }
    }
    
    /**
     * Applies damage to the enemy
     */
    fun takeDamage(amount: Int) {
        health -= amount
    }
    
    /**
     * Checks if the enemy is dead
     */
    fun isDead(): Boolean {
        return health <= 0
    }
    
    /**
     * Checks if the enemy has reached the end of the path
     */
    fun hasReachedEnd(): Boolean {
        return hasReachedEndFlag
    }
    
    /**
     * Gets the current health percentage (0-100)
     */
    fun getHealthPercentage(): Int {
        return (health.toFloat() / getMaxHealth().toFloat() * 100).toInt()
    }
    
    /**
     * Gets the maximum health for this enemy type
     */
    abstract fun getMaxHealth(): Int
}


