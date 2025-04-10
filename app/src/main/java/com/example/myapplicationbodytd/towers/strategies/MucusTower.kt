package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Mucus Tower - slows enemies but deals low damage
 */
class MucusTower(position: Int = 0) : Turret(10, SlowEffectAttack(), position) {
    private var level: Int = 1
    
    override fun upgrade() {
        level++
        // In a real implementation, we would upgrade the strategy
        println("Mucus Tower upgraded to level $level")
    }
    
    override fun getUpgradeCost(): Int = 5 * level

    override fun attackEnemy(enemy: Enemy) = strategy.attack(enemy)
}
