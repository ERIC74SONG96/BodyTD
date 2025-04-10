package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Interface defining the attack strategy for towers.
 * This follows the Strategy pattern to allow different attack behaviors.
 */
interface AttackStrategy {
    /**
     * Attacks a single enemy
     */
    fun attack(target: Enemy)
    
    /**
     * Gets the damage amount for this attack strategy
     */
    fun getDamage(): Int
    
    /**
     * Gets the attack speed (attacks per second)
     */
    fun getAttackSpeed(): Float
    
    /**
     * Gets the attack range
     */
    fun getRange(): Int
    
    /**
     * Gets the attack cooldown in seconds
     */
    fun getCooldown(): Float = 1.0f / getAttackSpeed()
}
