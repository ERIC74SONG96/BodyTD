package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import android.util.Log

/**
 * Basic attack strategy that deals direct damage to a single target.
 * @param damageMultiplier Optional multiplier for the tower's base damage.
 */
class SingleTargetAttack(private val damageMultiplier: Float = 1.0f) : AttackStrategy {

    override fun execute(targetEnemy: Enemy, tower: Tower) {
        if (targetEnemy.isDead) return // Don't attack dead enemies

        val damage = tower.baseDamage * damageMultiplier // Use tower's baseDamage
        Log.d("AttackStrategy", "SingleTargetAttack executing on ${targetEnemy.getType()} for $damage damage.")
        targetEnemy.onHit() // Trigger any on-hit effects
        targetEnemy.takeDamage(damage) // Apply damage
    }
} 