package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Attack strategy that pushes enemies back but deals low damage
 */
class PushBackAttack(private val pushBackForce: Int = 1) : AttackStrategy {
    private val damage: Int = 1
    private val attackSpeed: Float = 0.8f // 0.8 attacks per second
    private val range: Int = 3
    
    override fun attack(target: Enemy) {
        // Apply damage
        target.takeDamage(damage)
        
        // Push enemy back
        target.position = (target.position - pushBackForce).coerceAtLeast(0)
        
        println("Enemy pushed back by $pushBackForce units")
    }
    
    override fun getDamage(): Int = damage
    
    override fun getAttackSpeed(): Float = attackSpeed
    
    override fun getRange(): Int = range
}