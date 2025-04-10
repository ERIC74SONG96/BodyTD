package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.enemies.Enemy
import com.example.myapplicationbodytd.enemies.EnemyFactory
import com.example.myapplicationbodytd.enemies.EnemyType
import com.example.myapplicationbodytd.towers.strategies.CoughTower
import com.example.myapplicationbodytd.towers.strategies.MacrophageTower
import com.example.myapplicationbodytd.towers.strategies.MucusTower
import com.example.myapplicationbodytd.towers.strategies.Turret
import com.example.myapplicationbodytd.util.RenderableImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton class that manages the game state and logic
 */
class GameManager private constructor() {
    companion object {
        @Volatile
        private var instance: GameManager? = null

        fun getInstance(): GameManager {
            return instance ?: synchronized(this) {
                instance ?: GameManager().also { instance = it }
            }
        }
    }

    // Game state
    private var money = 100
    private var currentWave = 0
    private var isGameActive = false
    private var playerHealth = 100

    // Game objects
    private val enemies = mutableListOf<Enemy>()
    private val towers = mutableListOf<Turret>()
    
    // Renderable objects state
    private val _renderableObjects = MutableStateFlow<List<RenderableImage>>(emptyList())
    val renderableObjects: StateFlow<List<RenderableImage>> = _renderableObjects.asStateFlow()

    // Game constants
    private val startingMoney = 100
    private val startingHealth = 100
    private val moneyPerWave = 50
    private val moneyPerKill = 10

    /**
     * Starts a new game
     */
    fun startGame() {
        money = startingMoney
        currentWave = 0
        playerHealth = startingHealth
        isGameActive = true
        enemies.clear()
        towers.clear()
        updateRenderableObjects()
    }

    /**
     * Updates the game state
     * @param deltaTime Time elapsed since last update in seconds
     */
    fun update(deltaTime: Float) {
        if (!isGameActive) return

        // Update enemies
        enemies.forEach { enemy ->
            enemy.update(deltaTime)
            
            // Check if enemy reached the end
            if (enemy.hasReachedEnd()) {
                playerHealth -= enemy.getDamage()
                enemies.remove(enemy)
                
                if (playerHealth <= 0) {
                    isGameActive = false
                }
            }
        }

        // Update towers
        towers.forEach { tower ->
            // Find closest enemy in range
            val target = enemies.minByOrNull { enemy ->
                val distance = calculateDistance(tower.position, enemy.position)
                if (distance <= tower.attackStrategy.getRange()) distance else Float.MAX_VALUE
            }
            
            target?.let { tower.attackEnemy(it) }
        }

        // Check if wave is complete
        if (enemies.isEmpty() && isGameActive) {
            startNextWave()
        }

        updateRenderableObjects()
    }

    /**
     * Starts the next wave of enemies
     */
    private fun startNextWave() {
        currentWave++
        money += moneyPerWave

        // Spawn enemies based on wave number
        val numEnemies = 5 + currentWave
        repeat(numEnemies) {
            val enemyType = when (currentWave % 3) {
                0 -> EnemyType.VIRUS
                1 -> EnemyType.BACTERIA
                else -> EnemyType.FINE_PARTICLE
            }
            val enemy = EnemyFactory.createEnemy(enemyType, currentWave)
            enemies.add(enemy)
        }
    }

    /**
     * Places a tower at the specified position
     * @param towerType The type of tower to place
     * @param position The position to place the tower
     * @return true if the tower was placed successfully, false otherwise
     */
    fun placeTower(towerType: String, position: Int): Boolean {
        val cost = when (towerType) {
            "MucusTower" -> 10
            "MacrophageTower" -> 20
            "CoughTower" -> 10
            else -> return false
        }

        if (money < cost) return false

        val tower = when (towerType) {
            "MucusTower" -> MucusTower(position)
            "MacrophageTower" -> MacrophageTower(position)
            "CoughTower" -> CoughTower(position)
            else -> return false
        }

        money -= cost
        towers.add(tower)
        updateRenderableObjects()
        return true
    }

    /**
     * Updates the list of renderable objects
     */
    private fun updateRenderableObjects() {
        val renderables = mutableListOf<RenderableImage>()

        // Add enemies
        enemies.forEach { enemy ->
            renderables.add(
                RenderableImage(
                    resId = when (enemy.type) {
                        EnemyType.VIRUS -> R.drawable.virus
                        EnemyType.BACTERIA -> R.drawable.bacteria
                        EnemyType.FINE_PARTICLE -> R.drawable.particle
                    },
                    x = enemy.position * 48f,
                    y = 300f,
                    size = 48f
                )
            )
        }

        // Add towers
        towers.forEach { tower ->
            renderables.add(
                RenderableImage(
                    resId = when (tower) {
                        is MucusTower -> R.drawable.mucus_tower
                        is MacrophageTower -> R.drawable.macrophage_tower
                        is CoughTower -> R.drawable.cough_tower
                        else -> R.drawable.tower
                    },
                    x = tower.position * 48f,
                    y = 400f,
                    size = 48f
                )
            )
        }

        _renderableObjects.value = renderables
    }

    /**
     * Calculates the distance between two positions
     */
    private fun calculateDistance(pos1: Int, pos2: Int): Float {
        return kotlin.math.abs(pos1 - pos2).toFloat()
    }

    // Getters
    fun getMoney() = money
    fun getCurrentWave() = currentWave
    fun getPlayerHealth() = playerHealth
    fun isGameOver() = !isGameActive
}

