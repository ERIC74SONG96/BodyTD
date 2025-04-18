package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import android.util.Log

/**
 * Attack strategy that deals damage and pushes the enemy back along its path.
 * @param pushBackDistance The distance (in path segments) to push the enemy back.
 * @param damageMultiplier Optional multiplier for the tower's base damage.
 */
class PushBackAttack(
    private val pushBackDistance: Float = 1.0f,
    private val damageMultiplier: Float = 1.0f // Can optionally deal damage too
) : AttackStrategy {

    override fun execute(targetEnemy: Enemy, tower: Tower) {
        if (targetEnemy.isDead) return

        Log.d("AttackStrategy", "PushBackAttack executing on ${targetEnemy.getType()}.")
        targetEnemy.onHit()

        // Apply pushback effect
        targetEnemy.applyPushBack(pushBackDistance)

        // Optionally apply damage
        if (damageMultiplier > 0f) {
            val damage = tower.baseDamage * damageMultiplier // Use tower's baseDamage
            targetEnemy.takeDamage(damage)
        }
    }
} 