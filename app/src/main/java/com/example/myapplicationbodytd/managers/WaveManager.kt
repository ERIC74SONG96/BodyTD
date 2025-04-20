package com.example.myapplicationbodytd.managers

import android.util.Log
import com.example.myapplicationbodytd.game.factories.EnemyFactory

/**
 * Data class representing a single spawn instruction within a wave.
 * @property type The type of enemy to spawn.
 * @property delaySeconds The delay *after* the previous enemy in this wave is spawned.
 */
data class SpawnInstruction(
    val type: EnemyFactory.EnemyType,
    val delaySeconds: Float 
)

/**
 * Data class holding the definition for a single wave.
 * @property spawns A list of spawn instructions defining the enemies and their timing.
 */
data class WaveDefinition(
    val spawns: List<SpawnInstruction>
) {
    val totalEnemies: Int = spawns.size
}

/**
 * **Singleton Pattern:** Manages the progression of enemy waves, including defining wave structures
 * and controlling the spawning timing based on `WaveDefinition`s.
 *
 * Tracks the current wave status and notifies `GameManager` about enemy removals.
 */
object WaveManager : Updatable {

    enum class WaveStatus { WAITING_TO_START, IN_PROGRESS, COMPLETED }

    private var status: WaveStatus = WaveStatus.WAITING_TO_START
    private var currentWaveNumber: Int = 0 // Set by GameManager when starting a wave
    private var enemiesSpawnedThisWave: Int = 0
    private var enemiesRemainingThisWave: Int = 0 // Decremented when GameManager reports enemy death/reach end

    // Wave Definitions
    private val waveDefinitions = mutableListOf<WaveDefinition>()

    // Spawning state
    private var timeUntilNextSpawn: Float = 0f
    private var currentWaveSpawns = listOf<SpawnInstruction>() // Holds spawns for the active wave
    private var currentSpawnIndex = 0

    init {
        initializeWaveDefinitions()
        Log.d("WaveManager", "Initialized with ${waveDefinitions.size} wave definitions.")
    }

    private fun initializeWaveDefinitions() {
        waveDefinitions.clear()

        // --- Define Waves --- TODO: Balance these values!
        // Wave 1: Increased count, faster spawn, mix in Bacteria
        waveDefinitions.add(WaveDefinition(
            spawns = List(30) { SpawnInstruction(EnemyFactory.EnemyType.VIRUS, 0.6f) } +
                     List(5) { SpawnInstruction(EnemyFactory.EnemyType.BACTERIA, 1.0f) }
        ))

        // Wave 2: Increased counts, faster spawns, more mix
        waveDefinitions.add(WaveDefinition(
            spawns = List(20) { SpawnInstruction(EnemyFactory.EnemyType.VIRUS, 0.5f) } +
                     List(20) { SpawnInstruction(EnemyFactory.EnemyType.BACTERIA, 0.8f) } +
                     List(10) { SpawnInstruction(EnemyFactory.EnemyType.FINE_PARTICLE, 0.7f) }
        ))

        // Wave 3: Significantly increased counts, very fast spawns
        waveDefinitions.add(WaveDefinition(
            spawns = List(15) { SpawnInstruction(EnemyFactory.EnemyType.VIRUS, 0.4f) } +
                     List(25) { SpawnInstruction(EnemyFactory.EnemyType.BACTERIA, 0.6f) } +
                     List(30) { SpawnInstruction(EnemyFactory.EnemyType.FINE_PARTICLE, 0.4f) }
        ))
    }

    /**
     * Called by GameManager to start a specific wave.
     * @param waveNumber The wave number to start (1-based).
     * @param gameManager Reference to the game manager (needed for spawning).
     * @param path The path enemies should follow.
     */
    fun startWave(waveNumber: Int, gameManager: GameManager, path: List<Pair<Int, Int>>) {
        if (waveNumber <= 0 || waveNumber > waveDefinitions.size) {
            Log.e("WaveManager", "Attempted to start invalid wave number: $waveNumber")
            return
        }
        if (status == WaveStatus.IN_PROGRESS) {
             Log.w("WaveManager", "Tried to start wave $waveNumber while another wave is in progress.")
            return
        }

        val waveDef = waveDefinitions[waveNumber - 1]
        Log.d("WaveManager", "Starting Wave $waveNumber (${waveDef.totalEnemies} enemies)")
        currentWaveNumber = waveNumber
        status = WaveStatus.IN_PROGRESS
        currentSpawnIndex = 0
        enemiesSpawnedThisWave = 0
        enemiesRemainingThisWave = waveDef.totalEnemies
        currentWaveSpawns = waveDef.spawns
        timeUntilNextSpawn = currentWaveSpawns.firstOrNull()?.delaySeconds ?: 0f // Delay before first spawn
    }

    /**
     * Update method called periodically (e.g., by GameManager's PlayingState) to handle spawning.
     * @param deltaTime Time elapsed since the last update.
     * @param gameManager Ref to GameManager for spawning
     * @param path Path for enemies
     */
    fun update(deltaTime: Float, gameManager: GameManager, path: List<Pair<Int, Int>>) {
        if (status != WaveStatus.IN_PROGRESS || currentSpawnIndex >= currentWaveSpawns.size) {
            return
        }

        timeUntilNextSpawn -= deltaTime
        
        while (timeUntilNextSpawn <= 0f && currentSpawnIndex < currentWaveSpawns.size) {
            val instruction = currentWaveSpawns[currentSpawnIndex]
            
            Log.d("WaveManager", "Spawning ${instruction.type} (index: $currentSpawnIndex)")
            EnemyFactory.createEnemy(instruction.type, path, gameManager)
            enemiesSpawnedThisWave++
            currentSpawnIndex++

            if (currentSpawnIndex < currentWaveSpawns.size) {
                val nextDelay = currentWaveSpawns[currentSpawnIndex].delaySeconds
                timeUntilNextSpawn += nextDelay
            } else {
                Log.d("WaveManager", "All enemies for wave $currentWaveNumber spawned.")
                timeUntilNextSpawn = Float.MAX_VALUE
                break
            }
        }
    }

    // Update method from Updatable interface - GameManager won't call this directly
    // It will call the update(deltaTime, gameManager, path) version when needed.
    override fun update(deltaTime: Float) { 
        // This update might be used for internal timers if WaveManager registers itself.
        // However, the current design has GameManager calling the specific update.
    }

    /**
     * Checks if the current wave is complete (all spawned enemies are gone).
     * Should be called after an enemy is destroyed or reaches the end.
     */
     fun checkWaveCompletion(): Boolean {
         val waveFinishedSpawning = (status == WaveStatus.IN_PROGRESS && currentSpawnIndex >= currentWaveSpawns.size)
         val allEnemiesGone = (enemiesRemainingThisWave <= 0)

         if (waveFinishedSpawning && allEnemiesGone) {
             if (status != WaveStatus.COMPLETED) { // Prevent multiple logs/transitions
                Log.d("WaveManager", "Wave $currentWaveNumber completed!")
                status = WaveStatus.COMPLETED
             }
             return true
         }
         return false
     }

    /** Called by GameManager when an enemy is removed */
    fun notifyEnemyRemoved() {
        if (status == WaveStatus.IN_PROGRESS) {
            enemiesRemainingThisWave--
            // Don't check completion here, let GameManager poll or react to event
        }
    }

    fun getCurrentWaveStatus(): WaveStatus = status

    /**
     * Resets the WaveManager to its initial state.
     */
    fun reset() {
        Log.d("WaveManager", "Resetting WaveManager state.")
        status = WaveStatus.WAITING_TO_START
        currentWaveNumber = 0
        enemiesSpawnedThisWave = 0
        enemiesRemainingThisWave = 0
        currentSpawnIndex = 0
        currentWaveSpawns = emptyList()
        timeUntilNextSpawn = 0f
    }
}
