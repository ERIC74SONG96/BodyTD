package com.example.myapplicationbodytd.game.factories

import com.example.myapplicationbodytd.game.entities.Bacteria
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.FineParticle
import com.example.myapplicationbodytd.game.entities.Virus
import com.example.myapplicationbodytd.managers.GameManager

/**
 * Factory for creating different types of enemies.
 * Uses the Factory Pattern.
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
        return when (type) {
            EnemyType.VIRUS -> createVirus(path, gameManager)
            EnemyType.BACTERIA -> createBacteria(path, gameManager)
            EnemyType.FINE_PARTICLE -> createFineParticle(path, gameManager)
        }
    }
} 