package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower

/**
 * A strategy that combines multiple other strategies, executing them in order.
 * Uses the Composite Pattern.
 * @param strategies The list of strategies to execute.
 */
class CompositeAttackStrategy(private val strategies: List<AttackStrategy>) : AttackStrategy {

    override fun execute(targetEnemy: Enemy, tower: Tower) {
        if (targetEnemy.isDead) return

        // Execute each strategy in the list
        strategies.forEach { strategy ->
            // Check if enemy died mid-composite execution
            if (!targetEnemy.isDead) {
                strategy.execute(targetEnemy, tower)
            }
        }
    }
} 