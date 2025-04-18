package com.example.myapplicationbodytd.game.entities

import com.example.myapplicationbodytd.managers.GameManager

class Bacteria(
    path: List<Pair<Int, Int>>,
    gameManager: GameManager
) : Enemy(
    initialHealth = 100f,
    speed = 1.0f,
    reward = 20,
    path = path,
    gameManager = gameManager
) {

    override fun getType(): String = "Bacteria"

    // Bacteria-specific overrides can go here
} 