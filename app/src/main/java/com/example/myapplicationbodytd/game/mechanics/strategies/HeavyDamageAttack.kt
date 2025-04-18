package com.example.myapplicationbodytd.game.mechanics.strategies

import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import android.util.Log

/**
 * Attack strategy that deals higher damage than the basic attack.
 * Essentially a SingleTargetAttack with a higher default multiplier.
 * @param damageMultiplier Multiplier for the tower's base damage (defaults to 2.0).
 */
class HeavyDamageAttack(private val damageMultiplier: Float = 2.0f) : AttackStrategy {

    override fun execute(targetEnemy: Enemy, tower: Tower) {
        if (targetEnemy.isDead) return

        val damage = tower.baseDamage * damageMultiplier
        Log.d("AttackStrategy", "HeavyDamageAttack executing on ${targetEnemy.getType()} for $damage damage.")
        targetEnemy.onHit()
        targetEnemy.takeDamage(damage)
    }
} 