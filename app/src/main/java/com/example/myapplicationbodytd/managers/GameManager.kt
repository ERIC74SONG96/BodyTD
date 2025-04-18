package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.game.states.GameState
import com.example.myapplicationbodytd.game.states.InitializingState
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource
import android.util.Log
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.states.LostState

/**
 * Interface for game objects that need periodic updates.
 */
interface Updatable {
    fun update(deltaTime: Float)
}

/**
 * Manages the overall game state, game loop, and coordination between systems.
 * Uses the Singleton Pattern.
 * Implements a fixed time step game loop and State Pattern for game phases.
 */
object GameManager {
    private val gameObjects = mutableListOf<Updatable>()
    private val activeEnemies = mutableListOf<Enemy>() // Keep track of active enemies
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var gameLoopJob: Job? = null
    private var currentState: GameState? = null // Current game state

    // Target update rate (e.g., 60 updates per second)
    private const val TARGET_UPDATES_PER_SECOND = 60
    private const val TIME_STEP_NANOS = 1_000_000_000L / TARGET_UPDATES_PER_SECOND

    // Game Progress Tracking
    private var lives: Int = 3 // Starting lives
    private var currentWave: Int = 0 // Starts at wave 0, first wave is 1
    const val MAX_WAVES = 3 // As per specifications
    const val MAX_LIVES_LOST = 3 // As per specifications

    // Initialize and start the game loop
    init {
        // Initialize with the InitializingState
        changeState(InitializingState(this))
        startGameLoop()
    }

    fun registerGameObject(obj: Updatable) {
        // TODO: Consider thread safety
        if (!gameObjects.contains(obj)) {
            gameObjects.add(obj)
            if (obj is Enemy) { // Add to specific enemy list if it's an Enemy
                activeEnemies.add(obj)
            }
        }
    }

    fun unregisterGameObject(obj: Updatable) {
        // TODO: Consider thread safety
        if (gameObjects.remove(obj)) {
             if (obj is Enemy) {
                activeEnemies.remove(obj)
            }
        }
    }

    fun changeState(newState: GameState) {
        currentState?.exit() // Call exit on the old state
        currentState = newState
        currentState?.enter() // Call enter on the new state
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel() // Cancel previous loop if any
        gameLoopJob = scope.launch {
            val timeSource = TimeSource.Monotonic
            var lastTimeMark = timeSource.markNow()
            var lagNanos = 0L

            while (isActive) {
                val now = timeSource.markNow()
                val elapsedNanos = (now - lastTimeMark).inWholeNanoseconds
                lastTimeMark = now
                lagNanos += elapsedNanos

                // Update game logic based on fixed time step
                while (lagNanos >= TIME_STEP_NANOS) {
                    val deltaTimeSeconds = TIME_STEP_NANOS / 1_000_000_000f
                    updateGame(deltaTimeSeconds)
                    lagNanos -= TIME_STEP_NANOS
                }

                // Yield or delay to avoid busy-waiting and control frame rate
                // Calculate delay needed to approximate target frame time if necessary
                // For simplicity, a small fixed delay can be used, or rely on UI loop
                delay(1) // Minimal delay to yield the coroutine
            }
        }
    }

    private fun updateGame(deltaTime: Float) {
        // Update the current state
        currentState?.update(deltaTime)

        // Update all registered game objects
        // Create a copy to avoid ConcurrentModificationException if list is modified during iteration
        val objectsToUpdate = ArrayList(gameObjects)
        for (obj in objectsToUpdate) {
            obj.update(deltaTime)
        }

        // Placeholder for win/loss checks - will likely move into specific states
        if (lives <= 0 && currentState !is LostState) {
             Log.w("GameManager", "Loss condition met!")
            // changeState(LostState(this)) // Move check to PlayingState update
        }

        // TODO: Implement win/loss condition checks here
    }

    fun stopGameLoop() {
        gameLoopJob?.cancel()
    }

    // Clean up the scope when the game manager is no longer needed (e.g., application exit)
    fun cleanup() {
        scope.cancel()
    }

    // --- Event Reporting Methods (called by other systems) ---
    fun enemyReachedEnd(enemy: Enemy) {
        Log.d("GameManager", "Enemy ${enemy.getType()} reached the end!")
        lives--
        Log.d("GameManager", "Lives remaining: $lives")
        WaveManager.notifyEnemyRemoved()
        unregisterGameObject(enemy)

        // Loss condition check moved to PlayingState
    }

    fun enemyDestroyed(enemy: Enemy) {
        Log.d("GameManager", "Enemy ${enemy.getType()} destroyed! Awarding ${enemy.reward} currency.")
        EconomyManager.addCurrency(enemy.reward) // Add currency
        WaveManager.notifyEnemyRemoved()
        unregisterGameObject(enemy)
    }

    fun startNextWave() {
        currentWave++
        Log.d("GameManager", "Starting Wave $currentWave")
        // TODO: Tell WaveManager to start spawning for currentWave (Task 8)
    }

    // --- Getters for UI --- (Potentially replace with StateFlow/LiveData later)
    fun getCurrentLives(): Int = lives
    fun getCurrentWave(): Int = currentWave

    /**
     * Returns a snapshot of the currently active enemies.
     */
    fun getEnemies(): List<Enemy> {
        // Return a copy to prevent modification issues outside GameManager
        return ArrayList(activeEnemies)
    }
}
