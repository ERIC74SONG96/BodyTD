package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower

// Placeholder - Replace with actual Tower class from Task 7
// Need properties accessed by strategies (e.g., damage)
// data class TowerPlaceholder(val damage: Float = 10f, val position: Pair<Int, Int> = Pair(0,0))

/**
 * Interface defining the contract for different tower attack behaviors (Strategy Pattern).
 */
interface AttackStrategy {
    /**
     * Executes the attack behavior of this strategy.
     *
     * @param targetEnemy The enemy being targeted.
     * @param tower The tower performing the attack.
     */
    fun execute(targetEnemy: Enemy, tower: Tower)
} 