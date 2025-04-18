package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.managers.GameManager

class FineParticle(
    path: List<Pair<Int, Int>>,
    gameManager: GameManager
) : Enemy(
    initialHealth = 30f,
    speed = 1.0f,
    reward = 5,
    path = path,
    gameManager = gameManager
) {

    override fun getType(): String = "FineParticle"

    // FineParticle-specific overrides can go here
} 