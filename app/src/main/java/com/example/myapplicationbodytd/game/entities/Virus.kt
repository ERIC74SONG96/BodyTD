package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.managers.GameManager

class Virus(
    path: List<Pair<Int, Int>>,
    gameManager: GameManager
) : Enemy(
    initialHealth = 50f,
    speed = 1.5f,
    reward = 10,
    path = path,
    gameManager = gameManager
) {

    override fun getType(): String = "Virus"

    // Virus-specific overrides can go here (e.g., unique onDeath behavior)
    // override fun onDeath() { ... super.onDeath() ... }
} 