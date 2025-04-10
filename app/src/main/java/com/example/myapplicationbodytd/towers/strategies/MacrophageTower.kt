package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Macrophage Tower - deals heavy damage but has a slow attack speed
 */
class MacrophageTower(position: Int = 0) : Turret(20, HeavyDamageAttack(), position) {
    private var level: Int = 1
    
    override fun upgrade() {
        level++
        // In a real implementation, we would upgrade the strategy
        println("Macrophage Tower upgraded to level $level")
    }
    
    override fun getUpgradeCost(): Int = 10 * level

    override fun attackEnemy(enemy: Enemy) = strategy.attack(enemy)
}