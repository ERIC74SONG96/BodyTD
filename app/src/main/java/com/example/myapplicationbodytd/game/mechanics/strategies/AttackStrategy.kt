package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower

// Placeholder - Replace with actual Tower class from Task 7
// Need properties accessed by strategies (e.g., damage)
// data class TowerPlaceholder(val damage: Float = 10f, val position: Pair<Int, Int> = Pair(0,0))

/**
 * **Strategy Pattern:** Defines a common interface for various tower attack algorithms.
 * Each concrete implementation (e.g., `SingleTargetAttack`, `SlowEffectAttack`)
 * represents a different attack behavior that can be assigned to a `Tower`,
 * allowing the attack logic to be varied independently from the tower itself.
 */
interface AttackStrategy {
    /**
     * Executes the specific attack behavior defined by this strategy.
     *
     * @param targetEnemy The enemy being targeted.
     * @param tower The tower performing the attack (providing context like damage, position).
     */
    fun execute(targetEnemy: Enemy, tower: Tower)
} 