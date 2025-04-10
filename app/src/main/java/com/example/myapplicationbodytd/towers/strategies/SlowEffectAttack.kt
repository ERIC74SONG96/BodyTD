package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Attack strategy that slows enemies but deals low damage
 */
class SlowEffectAttack : AttackStrategy {
    private val damage: Int = 2
    private val attackSpeed: Float = 1.0f // 1 attack per second
    private val range: Int = 3
    private val slowEffect: Float = 0.5f // 50% slow
    
    override fun attack(target: Enemy) {
        // Apply damage
        target.takeDamage(damage)
        
        // Apply slow effect (would be implemented in the Enemy class)
        // For now, we'll just print a message
        println("Enemy slowed by ${slowEffect * 100}%")
    }
    
    override fun getDamage(): Int = damage
    
    override fun getAttackSpeed(): Float = attackSpeed
    
    override fun getRange(): Int = range
}