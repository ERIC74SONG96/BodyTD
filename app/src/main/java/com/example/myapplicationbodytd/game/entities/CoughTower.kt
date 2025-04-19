package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.game.mechanics.strategies.PushBackAttack
import com.example.myapplicationbodytd.managers.GameManager

class CoughTower(
    position: Pair<Int, Int>,
    gameManager: GameManager
) : Tower(
    range = 150f, // Assuming range 3 means 3 * tile size (50f)
    attackRate = 1.0f,
    attackStrategy = PushBackAttack(pushBackDistance = 1.0f, damageMultiplier = 0.5f), // Push back 1 tile, deal half damage
    position = position,
    gameManager = gameManager
) {
    companion object {
        const val COST = 10
    }
    // CoughTower specific properties or overrides can go here
} 