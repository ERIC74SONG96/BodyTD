package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.enemies.EnemyFactory
import com.example.myapplicationbodytd.enemies.EnemyType
import kotlinx.coroutines.*

/**
 * WaveManager gère les vagues d'ennemis
 */
class WaveManager {
    private var currentWave: Int = 0
    private var isSpawningEnemies: Boolean = false
    private val enemyFactory = EnemyFactory()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Démarre la vague suivante d'ennemis
     */
    fun startNextWave(gameManager: GameManager) {
        currentWave++
        isSpawningEnemies = true

        // Le nombre et la difficulté des ennemis augmentent à chaque vague
        val enemyCount = 5 + currentWave * 2
        val spawnDelay = 2000L - (currentWave * 100).coerceAtMost(1500)

        coroutineScope.launch {
            repeat(enemyCount) {
                // Choix de l'ennemi en fonction de la vague actuelle
                val enemyType = when {
                    currentWave < 3 -> EnemyType.VIRUS
                    currentWave < 6 -> if (it % 2 == 0) EnemyType.VIRUS else EnemyType.BACTERIE
                    else -> when (it % 3) {
                        0 -> EnemyType.VIRUS
                        1 -> EnemyType.BACTERIE
                        else -> EnemyType.PARTICULE_FINE
                    }
                }

                val enemy = enemyFactory.createEnemy(enemyType, currentWave)
                gameManager.addEnemy(enemy)
                delay(spawnDelay)
            }
            isSpawningEnemies = false
        }
    }

    /**
     * Indique si des ennemis sont en cours d'apparition
     */
    fun isSpawning() = isSpawningEnemies

    /**
     * Récupère le numéro de la vague actuelle
     */
    fun getCurrentWave() = currentWave
}