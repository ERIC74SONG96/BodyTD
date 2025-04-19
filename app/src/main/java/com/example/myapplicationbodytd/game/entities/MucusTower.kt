package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.game.mechanics.strategies.CompositeAttackStrategy
import com.example.myapplicationbodytd.game.mechanics.strategies.SingleTargetAttack
import com.example.myapplicationbodytd.game.mechanics.strategies.SlowEffectAttack
import com.example.myapplicationbodytd.managers.GameManager

class MucusTower(
    position: Pair<Int, Int>,
    gameManager: GameManager
) : Tower(
    cost = COST, // Use companion object constant
    range = 150f, // Assuming range 3 means 3 * tile size (50f)
    attackRate = 1.0f,
    attackStrategy = CompositeAttackStrategy(listOf(
        SlowEffectAttack(slowFactor = 0.5f, slowDuration = 2.0f, damageMultiplier = 0f), // Apply slow only
        SingleTargetAttack(damageMultiplier = 1.0f) // Apply standard damage
    )),
    position = position,
    gameManager = gameManager
) {
    companion object {
        const val COST = 10
    }
    // MucusTower specific properties or overrides can go here
} 