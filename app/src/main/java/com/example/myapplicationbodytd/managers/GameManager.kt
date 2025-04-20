package com.example.myapplicationbodytd.managers

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.game.states.GameState
import com.example.myapplicationbodytd.game.states.InitializingState
import com.example.myapplicationbodytd.game.states.PlayingState
import com.example.myapplicationbodytd.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.TimeSource
import com.example.myapplicationbodytd.game.effects.Effect
import com.example.myapplicationbodytd.game.states.LostState
import com.example.myapplicationbodytd.game.states.WaveClearedState
import com.example.myapplicationbodytd.game.states.WonState
import com.example.myapplicationbodytd.managers.SoundManager

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
    var currentCellSize: Float = Constants.DEFAULT_TILE_SIZE // Use Constant directly
        private set

    // Target update rate (e.g., 60 updates per second)
    private const val TARGET_UPDATES_PER_SECOND = 60

    // Game Progress Tracking
    const val MAX_WAVES = Constants.WAVES_TO_WIN // Use Constant
    private const val STARTING_LIVES = Constants.MAX_LIVES // Use Constant

    // --- Reactive Game State using StateFlow ---
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _lives = MutableStateFlow(STARTING_LIVES)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _currency = MutableStateFlow(EconomyManager.INITIAL_CURRENCY)
    val currency: StateFlow<Int> = _currency.asStateFlow()

    private val _currentWave = MutableStateFlow(0)
    val currentWave: StateFlow<Int> = _currentWave.asStateFlow()

    private val _activeEnemies = mutableStateListOf<Enemy>()
    val activeEnemies: SnapshotStateList<Enemy> = _activeEnemies

    private val _placedTowers = mutableStateListOf<Tower>()
    val placedTowers: SnapshotStateList<Tower> = _placedTowers

    // Add list for active effects
    private val _activeEffects = mutableStateListOf<Effect>()
    val activeEffects: SnapshotStateList<Effect> = _activeEffects

    // StateFlow to explicitly trigger recomposition in the UI
    private val _drawTick = MutableStateFlow(0L)
    val drawTick: StateFlow<Long> = _drawTick.asStateFlow()

    // StateFlow for Game Over overlay
    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    // StateFlow for Wave Cleared message
    private val _waveClearMessage = MutableStateFlow<String?>(null)
    val waveClearMessage: StateFlow<String?> = _waveClearMessage.asStateFlow()

    // Initialize and start the game loop
    init {
        // Initialize with the InitializingState
        changeState(InitializingState(this))
        startGameLoop()
    }

    /**
     * Thread-safe registration of game objects.
     * Modifies SnapshotStateLists directly.
     */
    fun registerGameObject(obj: Updatable) {
        gameObjectsLock.withLock {
            if (!gameObjects.contains(obj)) {
                gameObjects.add(obj)
                when (obj) {
                    is Enemy -> _activeEnemies.add(obj)
                    is Tower -> _placedTowers.add(obj)
                }
            }
        }
    }

    /**
     * Thread-safe unregistration of game objects.
     * Modifies SnapshotStateLists directly.
     */
    fun unregisterGameObject(obj: Updatable) {
        gameObjectsLock.withLock {
            if (gameObjects.remove(obj)) {
                when (obj) {
                    is Enemy -> _activeEnemies.remove(obj)
                    is Tower -> _placedTowers.remove(obj)
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

            // Play sounds based on the new state
            when (newState) {
                is PlayingState -> SoundManager.playSound(SoundManager.SoundType.WAVE_START)
                is WaveClearedState -> SoundManager.playSound(SoundManager.SoundType.WAVE_CLEARED)
                is WonState -> {
                    SoundManager.playSound(SoundManager.SoundType.GAME_WIN)
                    SoundManager.stopBackgroundMusic() // Stop music on win
                }
                is LostState -> {
                    SoundManager.playSound(SoundManager.SoundType.GAME_LOSE)
                    SoundManager.stopBackgroundMusic() // Stop music on loss
                }
                // Add other state-specific sound triggers if needed
                else -> {} // No specific sound for other states like Initializing
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel() // Cancel previous loop if any
        gameLoopJob = scope.launch {
            val timeSource = TimeSource.Monotonic
            var lastTimeMark = timeSource.markNow()
            // Remove lagNanos and frameCounter for simplicity during test
            // var lagNanos = 0L 
            // var frameCounter = 0L

            Log.i("GameLoop", "Starting game loop (Variable Timestep Test)...")

            while (isActive) {
                val loopStartTime = timeSource.markNow()
                val elapsedNanos = (loopStartTime - lastTimeMark).inWholeNanoseconds
                lastTimeMark = loopStartTime

                // Prevent division by zero or excessively large delta if loop stalls
                if (elapsedNanos <= 0) {
                    yield() // Skip update if no time elapsed
                    continue
                }
                // Cap delta time to avoid huge jumps after stalls (e.g., 100ms max step)
                val clampedNanos = minOf(elapsedNanos, 100_000_000L)
                val deltaTimeSeconds = clampedNanos / 1_000_000_000f 

                // === Call updateGame directly ===
                val updateStartTime = timeSource.markNow()
                updateGame(deltaTimeSeconds)
                val updateDurationMs = (timeSource.markNow() - updateStartTime).inWholeMilliseconds
                // ================================

                if (updateDurationMs > 10) { // Log if update takes longer than 10ms
                    Log.w("GameLoop", "updateGame took ${updateDurationMs}ms (variable dt: ${deltaTimeSeconds})")
                }
                
                // Yield control 
                yield() 

                // Increment the draw tick at the end of each loop iteration to trigger UI recomposition
                _drawTick.value += 1L
            }
            Log.i("GameLoop", "Game loop stopped.")
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

        // Update Effects
        gameObjectsLock.withLock { // Lock while modifying effects list
            val effectsIterator = _activeEffects.iterator()
            while (effectsIterator.hasNext()) {
                val effect = effectsIterator.next()
                effect.update(deltaTime)
                if (effect.isFinished) {
                    effectsIterator.remove()
                }
            }
        }
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
        Log.d("GameManager", "Enemy ${enemy.getType()} reached end.")
        // State check should ideally be inside PlayingState or a specific state logic
        // but for simplicity, we can check here to prevent decrementing lives in Won/Lost states
        if (_gameState.value is PlayingState) {
            _lives.update { currentLives -> maxOf(0, currentLives - 1) }
             // Play sound when an enemy reaches the end
             SoundManager.playSound(SoundManager.SoundType.ENEMY_REACHED_END)
            unregisterGameObject(enemy) // Remove enemy from updates and lists

            if (_lives.value <= 0) {
                Log.i("GameManager", "Game Over - Lives reached zero.")
                changeState(LostState(this))
            }
        } else {
            // If not in PlayingState, just remove the enemy without affecting lives
            unregisterGameObject(enemy)
        }
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
        val existingTowers = _placedTowers
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

    // Called by LostState/WonState to signal UI
    fun setGameOver(isOver: Boolean) {
        _isGameOver.value = isOver
    }

    /**
     * Resets the entire game state to its initial configuration.
     */
    fun reset() {
        Log.i("GameManager", "Resetting game state...")
        // Stop the current loop before changing state
        stopGameLoop()

        // Reset Core Game State
        _lives.value = STARTING_LIVES
        _currentWave.value = 0
        _isGameOver.value = false
        _waveClearMessage.value = null // Also reset message

        // Reset Managers
        EconomyManager.reset() // Reset currency
        _currency.value = EconomyManager.getCurrentCurrency() // Update StateFlow after reset
        WaveManager.reset() // Reset wave progression

        // Clear All Game Objects Safely
        gameObjectsLock.withLock {
            // Create a copy for safe iteration while removing
            val objectsToRemove = ArrayList(gameObjects)
            objectsToRemove.forEach { obj ->
                // Use unregisterGameObject to ensure SnapshotStateLists are also updated
                unregisterGameObject(obj)
            }
            // Double-check lists are empty (should be handled by unregisterGameObject)
            if (gameObjects.isNotEmpty() || _activeEnemies.isNotEmpty() || _placedTowers.isNotEmpty()) {
                Log.w("GameManager", "Game object lists not fully cleared during reset. Force clearing.")
                gameObjects.clear()
                _activeEnemies.clear()
                _placedTowers.clear()
            }
            // Clear effects list too
            _activeEffects.clear()
        }

        // Reset Map State (if applicable - currently static)
        // gameMap.reset() // Example if map needed resetting

        // Reset Cell Size (optional, or keep last known size)
        // currentCellSize = Constants.DEFAULT_TILE_SIZE

        // Restart the game loop
        startGameLoop()
        Log.i("GameManager", "Game reset complete. New game loop started.")
    }

    /** Sets or clears the message displayed between waves */
    fun setWaveClearMessage(message: String?) {
        _waveClearMessage.value = message
    }

    /** Adds a visual effect to be managed and drawn. */
    fun addEffect(effect: Effect) {
        gameObjectsLock.withLock {
            _activeEffects.add(effect)
        }
    }
}
