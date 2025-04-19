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

    /**
     * Creates a Virus enemy.
     * @param path The path the enemy will follow.
     * @param gameManager Reference to the game manager.
     * @return A new Virus instance.
     */
    fun createVirus(path: List<Pair<Int, Int>>, gameManager: GameManager): Virus {
        return Virus(path = path, gameManager = gameManager)
    }

    /**
     * Creates a Bacteria enemy.
     * @param path The path the enemy will follow.
     * @param gameManager Reference to the game manager.
     * @return A new Bacteria instance.
     */
    fun createBacteria(path: List<Pair<Int, Int>>, gameManager: GameManager): Bacteria {
        return Bacteria(path = path, gameManager = gameManager)
    }

    /**
     * Creates a Fine Particle enemy.
     * @param path The path the enemy will follow.
     * @param gameManager Reference to the game manager.
     * @return A new FineParticle instance.
     */
    fun createFineParticle(path: List<Pair<Int, Int>>, gameManager: GameManager): FineParticle {
        return FineParticle(path = path, gameManager = gameManager)
    }

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