package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import android.util.Log

/**
 * Attack strategy that deals damage and applies a slow effect.
 * @param slowFactor The speed multiplier (e.g., 0.5 for 50% slow).
 * @param slowDuration The duration of the slow effect in seconds.
 * @param damageMultiplier Optional multiplier for the tower's base damage.
 */
class SlowEffectAttack(
    private val slowFactor: Float = 0.5f,
    private val slowDuration: Float = 2.0f,
    private val damageMultiplier: Float = 1.0f // Can optionally deal damage too
) : AttackStrategy {

    override fun execute(targetEnemy: Enemy, tower: Tower) {
        if (targetEnemy.isDead) return

        Log.d("AttackStrategy", "SlowEffectAttack executing on ${targetEnemy.getType()}.")
        targetEnemy.onHit()

        // Apply slow effect
        targetEnemy.applySlow(duration = slowDuration, factor = slowFactor)

        // Optionally apply damage
        if (damageMultiplier > 0f) {
            val damage = tower.baseDamage * damageMultiplier // Use tower's baseDamage
            targetEnemy.takeDamage(damage)
        }
    }
} 