package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Attack strategy that deals heavy damage but has a slow attack speed
 */
class HeavyDamageAttack : AttackStrategy {
    private val damage: Int = 10
    private val attackSpeed: Float = 0.5f // 0.5 attacks per second
    private val range: Int = 3
    
    override fun attack(target: Enemy) {
        target.takeDamage(damage)
    }
    
    override fun getDamage(): Int = damage
    
    override fun getAttackSpeed(): Float = attackSpeed
    
    override fun getRange(): Int = range
}
