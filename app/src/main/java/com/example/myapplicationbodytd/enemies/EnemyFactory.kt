package com.example.myapplicationbodytd.enemies

import FineParticle
import Virus

/**
 * Factory for creating different types of enemies.
 * This follows the Factory pattern to centralize enemy creation logic.
 */
object EnemyFactory {

    /**
     * Creates an enemy of the specified type
     */
    fun createEnemy(type: String): Enemy {
        return when (type.uppercase()) {
            "VIRUS" -> Virus()
            "BACTERIA" -> Bacteria()
            "FINE_PARTICLE" -> FineParticle()
            else -> throw IllegalArgumentException("Unknown enemy type: $type")
        }
    }

    /**
     * Creates an enemy of the specified type with custom properties
     */
    fun createEnemy(type: EnemyType, waveNumber: Int = 1): Enemy {
        // Scale enemy properties based on wave number
        val healthMultiplier = 1.0f + (waveNumber - 1) * 0.1f
        val speedMultiplier = 1.0f + (waveNumber - 1) * 0.05f
        val damageMultiplier = 1.0f + (waveNumber - 1) * 0.1f
        val rewardMultiplier = 1.0f + (waveNumber - 1) * 0.05f
        
        return when (type) {
            EnemyType.VIRUS -> Virus(
                health = (type.getBaseHealth() * healthMultiplier).toInt(),
                speed = type.getBaseSpeed() * speedMultiplier,
                damage = (type.getBaseDamage() * damageMultiplier).toInt(),
                reward = (type.getBaseReward() * rewardMultiplier).toInt()
            )
            EnemyType.BACTERIA -> Bacteria(
                health = (type.getBaseHealth() * healthMultiplier).toInt(),
                speed = type.getBaseSpeed() * speedMultiplier,
                damage = (type.getBaseDamage() * damageMultiplier).toInt(),
                reward = (type.getBaseReward() * rewardMultiplier).toInt()
            )
            EnemyType.FINE_PARTICLE -> FineParticle(
                health = (type.getBaseHealth() * healthMultiplier).toInt(),
                speed = type.getBaseSpeed() * speedMultiplier,
                damage = (type.getBaseDamage() * damageMultiplier).toInt(),
                reward = (type.getBaseReward() * rewardMultiplier).toInt()
            )
        }
    }

    /**
     * Creates a new enemy of the specified type
     * @param type The type of enemy to create
     * @param position The starting position of the enemy
     * @return A new Enemy instance
     */
    fun createEnemy(type: EnemyType, position: Int): Enemy {
        return when (type) {
            EnemyType.VIRUS -> Enemy(
                type = EnemyType.VIRUS,
                health = 100,
                speed = 1.0f,
                damage = 10,
                position = position
            )
            EnemyType.BACTERIA -> Enemy(
                type = EnemyType.BACTERIA,
                health = 150,
                speed = 0.8f,
                damage = 15,
                position = position
            )
            EnemyType.FINE_PARTICLE -> Enemy(
                type = EnemyType.FINE_PARTICLE,
                health = 50,
                speed = 1.5f,
                damage = 5,
                position = position
            )
        }
    }

    /**
     * Creates a wave of enemies based on the wave number
     * @param waveNumber The current wave number
     * @return List of enemies for the wave
     */
    fun createWave(waveNumber: Int): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val baseEnemyCount = 5
        val enemyCount = baseEnemyCount + (waveNumber - 1) * 2

        for (i in 0 until enemyCount) {
            val type = when {
                waveNumber <= 2 -> EnemyType.VIRUS
                waveNumber <= 4 -> if (i % 2 == 0) EnemyType.VIRUS else EnemyType.BACTERIA
                else -> when (i % 3) {
                    0 -> EnemyType.VIRUS
                    1 -> EnemyType.BACTERIA
                    else -> EnemyType.FINE_PARTICLE
                }
            }
            enemies.add(createEnemy(type, 0))
        }

        return enemies
    }
}
