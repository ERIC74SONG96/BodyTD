package com.example.myapplicationbodytd.towers.strategies

import com.example.myapplicationbodytd.enemies.Enemy

/**
 * Cough Tower - pushes enemies back but deals low damage
 */
class CoughTower(position: Int = 0, pushBackForce: Int = 1) : Turret(10, PushBackAttack(pushBackForce), position) {
    private var level: Int = 1
    
    override fun upgrade() {
        level++
        // In a real implementation, we would upgrade the strategy
        println("Cough Tower upgraded to level $level")
    }
    
    override fun getUpgradeCost(): Int = 5 * level
}
