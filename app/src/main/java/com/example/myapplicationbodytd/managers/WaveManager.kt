package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.enemies.EnemyFactory
import com.example.myapplicationbodytd.enemies.EnemyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * WaveManager handles the spawning of enemies in waves.
 * It follows the Singleton pattern to ensure there's only one instance throughout the app.
 */
class WaveManager {
    // Wave state
    private var currentWave: Int = 0
    private var isSpawningEnemies: Boolean = false
    private var timeSinceLastWave: Float = 0f
    private val timeBetweenWaves: Float = 10f // 10 seconds between waves
    
    // Enemy factory
    private val enemyFactory = EnemyFactory
    
    // Coroutine scope for spawning enemies
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    /**
     * Updates the wave manager based on elapsed time
     */
    fun update(deltaTime: Float, gameManager: GameManager) {
        // If not currently spawning enemies, check if it's time for the next wave
        if (!isSpawningEnemies) {
            timeSinceLastWave += deltaTime
            
            // If it's time for the next wave, start it
            if (timeSinceLastWave >= timeBetweenWaves) {
                startNextWave(gameManager)
                timeSinceLastWave = 0f
            }
        }
    }
    
    /**
     * Starts the next wave of enemies
     */
    fun startNextWave(gameManager: GameManager) {
        currentWave++
        isSpawningEnemies = true
        
        // Calculate number of enemies based on wave number
        val enemyCount = 5 + currentWave * 2
        
        // Calculate spawn delay (decreases as waves progress)
        val spawnDelay = (2000L - (currentWave * 100)).coerceAtMost(1500)
        
        // Launch coroutine to spawn enemies
        coroutineScope.launch {
            repeat(enemyCount) {
                // Choose enemy type based on wave number
                val enemyType = when {
                    currentWave < 3 -> EnemyType.VIRUS
                    currentWave < 6 -> if (it % 2 == 0) EnemyType.VIRUS else EnemyType.BACTERIA
                    else -> when (it % 3) {
                        0 -> EnemyType.VIRUS
                        1 -> EnemyType.BACTERIA
                        else -> EnemyType.FINE_PARTICLE
                    }
                }
                
                // Create and add enemy
                val enemy = enemyFactory.createEnemy(enemyType.name)
                gameManager.addEnemy(enemy)
                
                // Wait before spawning next enemy
                kotlinx.coroutines.delay(spawnDelay)
            }
            
            // Wave complete
            isSpawningEnemies = false
        }
    }
    
    /**
     * Gets the current wave number
     */
    fun getCurrentWave(): Int = currentWave
    
    /**
     * Checks if enemies are currently being spawned
     */
    fun isSpawning(): Boolean = isSpawningEnemies
    
    /**
     * Resets the wave manager
     */
    fun reset() {
        currentWave = 0
        isSpawningEnemies = false
        timeSinceLastWave = 0f
    }
}