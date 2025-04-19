package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.game.states.GameState
import com.example.myapplicationbodytd.game.states.InitializingState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.TimeSource
import android.util.Log
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.game.states.LostState
import com.example.myapplicationbodytd.game.states.PlayingState
import com.example.myapplicationbodytd.game.states.WonState
import com.example.myapplicationbodytd.managers.EconomyManager
import com.example.myapplicationbodytd.util.CoordinateConverter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Interface for game objects that need periodic updates.
 * Part of the Observer pattern implicitly used by the game loop.
 */
interface Updatable {
    /**
     * Update the state of the object.
     * @param deltaTime Time elapsed since the last update.
     */
    fun update(deltaTime: Float)
}

/**
 * **Singleton Pattern:** Manages the overall game state, game loop, and coordination between systems.
 * Ensures a single instance controls the game flow.
 *
 * **State Pattern:** Uses `GameState` subclasses (`InitializingState`, `PlayingState`, etc.)
 * to manage different phases of the game (initialization, playing, win, loss).
 * The `changeState` method facilitates transitions between states.
 *
 * **Observer Pattern (via StateFlow):** Exposes core game state (lives, currency, enemies, etc.)
 * reactively using `StateFlow`. Other parts of the application (like ViewModels)
 * can observe these flows to react to state changes without tight coupling.
 *
 * Implements a fixed time step game loop for deterministic updates.
 */
object GameManager {
    val gameMap = Map()

    // Lock for synchronizing access to game objects and state flows
    private val gameObjectsLock = ReentrantLock()
    private val gameObjects = mutableListOf<Updatable>()
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var gameLoopJob: Job? = null

    // Cell size for coordinate conversions (updated by UI)
    var currentCellSize: Float = CoordinateConverter.DEFAULT_TILE_SIZE // Initialize with default
        private set

    // Target update rate (e.g., 60 updates per second)
    private const val TARGET_UPDATES_PER_SECOND = 60
    private const val TIME_STEP_NANOS = 1_000_000_000L / TARGET_UPDATES_PER_SECOND

    // Game Progress Tracking
    const val MAX_WAVES = 3
    const val MAX_LIVES_LOST = 3
    private const val STARTING_LIVES = 3

    // --- Reactive Game State using StateFlow ---
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _lives = MutableStateFlow(STARTING_LIVES)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _currency = MutableStateFlow(EconomyManager.INITIAL_CURRENCY)
    val currency: StateFlow<Int> = _currency.asStateFlow()

    private val _currentWave = MutableStateFlow(0)
    val currentWave: StateFlow<Int> = _currentWave.asStateFlow()

    private val _activeEnemies = MutableStateFlow<List<Enemy>>(emptyList())
    val activeEnemies: StateFlow<List<Enemy>> = _activeEnemies.asStateFlow()

    private val _placedTowers = MutableStateFlow<List<Tower>>(emptyList())
    val placedTowers: StateFlow<List<Tower>> = _placedTowers.asStateFlow()

    // Initialize and start the game loop
    init {
        // Initialize with the InitializingState
        changeState(InitializingState(this))
        startGameLoop()
    }

    /**
     * Thread-safe registration of game objects.
     * Uses ReentrantLock to synchronize access to shared collections and state flows.
     */
    fun registerGameObject(obj: Updatable) {
        gameObjectsLock.withLock {
            if (!gameObjects.contains(obj)) {
                gameObjects.add(obj)
                when (obj) {
                    is Enemy -> _activeEnemies.update { list -> list + obj }
                    is Tower -> _placedTowers.update { list -> list + obj }
                }
            }
        }
    }

    /**
     * Thread-safe unregistration of game objects.
     * Uses ReentrantLock to synchronize access to shared collections and state flows.
     */
    fun unregisterGameObject(obj: Updatable) {
        gameObjectsLock.withLock {
            if (gameObjects.remove(obj)) {
                when (obj) {
                    is Enemy -> _activeEnemies.update { list -> list - obj }
                    is Tower -> _placedTowers.update { list -> list - obj }
                }
            }
        }
    }

    fun changeState(newState: GameState) {
        val oldState = _gameState.value
        if (oldState != newState) {
            Log.d("GameManager", "Changing state from ${oldState?.javaClass?.simpleName} to ${newState.javaClass.simpleName}")
            oldState?.exit() // Call exit on the old state
            _gameState.value = newState // Update the StateFlow
            newState.enter() // Call enter on the new state
        }
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
        // Update the current state FIRST - it might change game objects or game state itself
        _gameState.value?.update(deltaTime)

        // Update all registered game objects
        // Create a copy to avoid ConcurrentModificationException if list is modified during iteration
        // Note: State updates should handle adding/removing objects safely now
        val objectsToUpdate = ArrayList(gameObjects) // Still use copy for iteration safety
        for (obj in objectsToUpdate) {
            // Check if object might have been unregistered by state update or another object's update
             if (gameObjects.contains(obj)) {
                 try {
                     obj.update(deltaTime)
                 } catch (e: Exception) {
                     Log.e("GameManager", "Error updating game object: ${obj::class.simpleName}", e)
                     // Decide how to handle: remove object, log, etc.
                     // For now, just log and continue
                 }
            }
        }

        // Win/loss checks are handled within specific states (e.g., PlayingState)
        // Example: PlayingState now checks lives and wave completion and calls changeState
    }

    fun stopGameLoop() {
        gameLoopJob?.cancel()
    }

    // Clean up the scope when the game manager is no longer needed (e.g., application exit)
    fun cleanup() {
        scope.cancel()
    }

    // --- Event Reporting Methods (Update StateFlows) ---
    fun enemyReachedEnd(enemy: Enemy) {
        Log.d("GameManager", "Enemy ${enemy.getType()} reached the end!")
        _lives.update { currentLives -> maxOf(0, currentLives - 1) } // Ensure lives don't go below 0
        Log.d("GameManager", "Lives remaining: ${_lives.value}")
        WaveManager.notifyEnemyRemoved()
        unregisterGameObject(enemy) // This will update _activeEnemies StateFlow

        // Loss condition check is handled by PlayingState observing the lives StateFlow or via direct check in update
        // if (_lives.value <= 0 && _gameState.value is PlayingState) {
        //     changeState(LostState(this))
        // }
    }

    fun enemyDestroyed(enemy: Enemy) {
        Log.d("GameManager", "Enemy ${enemy.getType()} destroyed! Awarding ${enemy.reward} currency.")
        EconomyManager.addCurrency(enemy.reward) // Update EconomyManager's state
        _currency.value = EconomyManager.getCurrentCurrency() // Use correct method name
        WaveManager.notifyEnemyRemoved()
        unregisterGameObject(enemy) // This will update _activeEnemies StateFlow
    }

    fun placeTower(tower: Tower, cost: Int): Boolean {
        if (EconomyManager.spendCurrency(cost)) {
            _currency.value = EconomyManager.getCurrentCurrency() // Use correct method name
            registerGameObject(tower) // This will update _placedTowers StateFlow
            Log.d("GameManager", "Placed tower ${tower.javaClass.simpleName}. Currency remaining: ${_currency.value}")
            return true
        }
        Log.w("GameManager", "Not enough currency to place tower ${tower.javaClass.simpleName}. Needed: $cost, Have: ${EconomyManager.getCurrentCurrency()}") // Use correct method name in log
        return false
    }

    /**
     * Called by the UI (via ViewModel) or state machine to initiate the next wave.
     * It increments the wave counter (if valid) and transitions to the PlayingState.
     * The actual wave spawning logic is triggered within PlayingState.enter().
     */
    fun requestNextWave() {
        if (_currentWave.value >= MAX_WAVES) {
            Log.w("GameManager", "Requested next wave, but already completed max waves ($MAX_WAVES).")
            // Optional: Could transition to WonState here if not already handled by PlayingState
            return
        }

        val nextWave = _currentWave.value + 1
        Log.i("GameManager", "Request received to start Wave $nextWave.")
        _currentWave.update { nextWave }
        
        // Transition to PlayingState. PlayingState.enter() will handle calling WaveManager.
        changeState(PlayingState(this))
    }

    // --- Map Interaction ---
    fun canPlaceTowerAt(x: Int, y: Int): Boolean {
        // First check if the map tile allows tower placement
        if (!gameMap.canPlaceTowerAt(x, y)) {
            return false
        }

        // Then check if there's already a tower at this location
        // Use the synchronized list of placed towers from the StateFlow
        val existingTowers = _placedTowers.value
        return !existingTowers.any { it.position == Pair(x, y) }
    }
    // ---------------------

    // --- Remove simple getters, UI will observe StateFlows ---
    // fun getCurrentLives(): Int = _lives.value // No longer needed
    // fun getCurrentWave(): Int = _currentWave.value // No longer needed
    // fun getEnemies(): List<Enemy> = _activeEnemies.value // No longer needed, observe StateFlow
    // fun getTowers(): List<Tower> = _placedTowers.value // No longer needed, observe StateFlow

    // --- Configuration ---
    fun updateCellSize(newSize: Float) {
        if (newSize > 0f) {
            currentCellSize = newSize
            // Optionally log or notify other systems if needed
            // Log.d("GameManager", "Cell size updated to $newSize")
        }
    }
}
