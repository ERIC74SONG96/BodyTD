package com.example.myapplicationbodytd.game.factories

import com.example.myapplicationbodytd.game.entities.Bacteria
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.FineParticle
import com.example.myapplicationbodytd.game.entities.Virus
import com.example.myapplicationbodytd.managers.GameManager

/**
 * **Factory Pattern:** Centralizes the creation logic for different types of `Enemy` objects.
 * Provides methods (`createVirus`, `createBacteria`, `createEnemy`) to instantiate specific
 * enemy subclasses, decoupling the client code (like `WaveManager`) from the concrete
 * enemy constructors.
 */
object EnemyFactory {

    // Example of a more generic factory method
    enum class EnemyType { VIRUS, BACTERIA, FINE_PARTICLE }

    fun createEnemy(type: EnemyType, path: List<Pair<Int, Int>>, gameManager: GameManager): Enemy {
        val enemy = when (type) {
            EnemyType.VIRUS -> Virus(path = path, gameManager = gameManager)
            EnemyType.BACTERIA -> Bacteria(path = path, gameManager = gameManager)
            EnemyType.FINE_PARTICLE -> FineParticle(path = path, gameManager = gameManager)
        }
        gameManager.registerGameObject(enemy)
        return enemy
    }
} 