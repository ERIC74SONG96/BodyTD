package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Abstract base class for all towers in the game.
 * Each tower has a cost, range, attack strategy, and position.
 */
abstract class Turret(
    val cost: Int,
    val strategy: AttackStrategy,
    var position: Int = 0
) {
    // Cooldown tracking
    private var lastAttackTime: Float = 0f
    private var cooldown: Float = strategy.getCooldown()
    
    // Range is determined by the strategy
    val range: Int = strategy.getRange()
    
    /**
     * Attacks an enemy if the cooldown has elapsed
     */
    fun attackEnemy(enemy: Enemy, currentTime: Float) {
        // Check if cooldown has elapsed
        if (currentTime - lastAttackTime >= cooldown) {
            strategy.attack(enemy)
            lastAttackTime = currentTime
        }
    }
    
    /**
     * Abstract method for tower-specific upgrade behavior
     */
    abstract fun upgrade()
    
    /**
     * Gets the upgrade cost for this tower
     */
    abstract fun getUpgradeCost(): Int
}

