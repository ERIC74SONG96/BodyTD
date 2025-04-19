package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.game.mechanics.strategies.HeavyDamageAttack
import com.example.myapplicationbodytd.managers.GameManager

class MacrophageTower(
    position: Pair<Int, Int>,
    gameManager: GameManager
) : Tower(
    range = 150f, // Assuming range 3 means 3 * tile size (50f)
    attackRate = 0.5f, // Slower attack rate
    attackStrategy = HeavyDamageAttack(damageMultiplier = 2.5f), // Higher damage multiplier
    position = position,
    gameManager = gameManager
) {
    companion object {
        const val COST = 20
    }
    // MacrophageTower specific properties or overrides can go here
} 