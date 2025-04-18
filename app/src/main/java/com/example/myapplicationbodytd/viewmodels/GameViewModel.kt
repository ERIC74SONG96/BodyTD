package com.example.myapplicationbodytd.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
//import com.example.myapplicationbodytd.game.entities.TowerType
import com.example.myapplicationbodytd.managers.GameManager // Assume GameManager exists
import com.example.myapplicationbodytd.game.map.Map
import android.util.Log
import com.example.myapplicationbodytd.ui.TowerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Define costs here temporarily, ideally fetch from game data/GameManager
val towerCostsViewModel = mapOf(
    TowerType.MUCUS to 10,
    TowerType.MACROPHAGE to 20,
    TowerType.COUGH to 10
)

/**
 * ViewModel for the main Game Screen.
 * Manages the game state and handles user interactions.
 */
class GameViewModel : ViewModel() {

    // --- Game Logic Integration ---
    // TODO: Properly inject or retrieve singleton instances
    private val gameManager = GameManager // Placeholder instantiation

    // --- Game State ---
    // Get initial state from GameManager and observe changes
    // TODO: Use Flows or other observable patterns from GameManager if available
    private val _map = mutableStateOf(gameManager.map) // Assuming GameManager exposes map
    val map: State<Map> = _map

    private val _enemies = mutableStateOf(gameManager.enemies) // Assuming GameManager exposes enemies
    val enemies: State<List<Enemy>> = _enemies

    private val _towers = mutableStateOf(gameManager.towers) // Assuming GameManager exposes towers
    val towers: State<List<Tower>> = _towers

    private val _currency = mutableStateOf(gameManager.currency) // Assuming GameManager exposes currency
    val currency: State<Int> = _currency

    private val _lives = mutableStateOf(gameManager.lives) // Assuming GameManager exposes lives
    val lives: State<Int> = _lives

    private val _wave = mutableStateOf(gameManager.currentWave) // Assuming GameManager exposes wave
    val wave: State<Int> = _wave

    // --- Placement State ---
    private val _placementMode = mutableStateOf(false)
    val placementMode: State<Boolean> = _placementMode

    private val _selectedTowerForPlacement = mutableStateOf<TowerType?>(null)
    val selectedTowerForPlacement: State<TowerType?> = _selectedTowerForPlacement

    init {
        Log.d("GameViewModel", "Initializing GameViewModel and starting game loop...")
        // Start the game loop when ViewModel is created
        startGameLoop()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            var lastFrameTime = System.nanoTime()
            while (true) { // TODO: Add condition to stop the loop (e.g., game over state)
                val currentTime = System.nanoTime()
                val deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f // Delta time in seconds
                lastFrameTime = currentTime

                // Update game state via GameManager
                gameManager.update(deltaTime)

                // Update ViewModel state based on GameManager state
                // TODO: Optimize this - ideally observe flows from GameManager
                _map.value = gameManager.map
                _enemies.value = gameManager.enemies
                _towers.value = gameManager.towers
                _currency.value = gameManager.currency
                _lives.value = gameManager.lives
                _wave.value = gameManager.currentWave

                delay(16) // Aim for ~60 FPS (adjust as needed)
            }
        }
    }

    // --- Placement Actions ---
    fun enterPlacementMode(towerType: TowerType) {
        // Check if affordable before entering placement mode?
        // Or just disable button and rely on check during placement?
        // Current approach: Check during placement tap.
        _selectedTowerForPlacement.value = towerType
        _placementMode.value = true
        Log.d("GameViewModel", "Entered placement mode for $towerType")
    }

    fun exitPlacementMode() {
        _selectedTowerForPlacement.value = null
        _placementMode.value = false
        Log.d("GameViewModel", "Exited placement mode")
    }

    fun togglePlacementMode(towerType: TowerType) {
        if (_placementMode.value && _selectedTowerForPlacement.value == towerType) {
            exitPlacementMode()
        } else {
            enterPlacementMode(towerType)
        }
    }

    fun handleTileTap(x: Int, y: Int) {
        Log.d("GameViewModel", "Tile tapped: ($x, $y)")
        if (!_placementMode.value) {
            Log.d("GameViewModel", "Tap ignored: Not in placement mode.")
            return
        }

        val towerTypeToPlace = _selectedTowerForPlacement.value
        if (towerTypeToPlace == null) {
            Log.e("GameViewModel", "Tap ignored: In placement mode but no tower type selected!")
            exitPlacementMode()
            return
        }

        Log.d("GameViewModel", "Attempting to place $towerTypeToPlace at ($x, $y)")

        // --- Check Placement Validity (via GameManager) ---
        if (gameManager.canPlaceTowerAt(x, y)) { // Assuming GameManager has this method
            Log.d("GameViewModel", "Placement is valid for $towerTypeToPlace at ($x, $y).")

            // --- Economy Check ---
            val cost = towerCostsViewModel[towerTypeToPlace] ?: Int.MAX_VALUE // Use cost map
            if (gameManager.canAfford(cost)) { // Assuming GameManager has this method
                Log.d("GameViewModel", "Player can afford $towerTypeToPlace (Cost: $cost, Current: ${gameManager.currency}).")

                // --- Place Tower (via GameManager) ---
                val placed = gameManager.placeTower(towerTypeToPlace, x, y) // Assuming GameManager has this method
                if (placed) {
                    Log.i("GameViewModel", "Tower $towerTypeToPlace placed successfully at ($x, $y) by GameManager.")
                    // Currency deduction should be handled within GameManager.placeTower or EconomíaManager
                    // No need to manually update _currency here if GameManager handles it
                    exitPlacementMode() // Exit placement mode after successful placement
                } else {
                    Log.e("GameViewModel", "GameManager failed to place tower $towerTypeToPlace at ($x, $y)!")
                     exitPlacementMode() // Exit if placement fails in game logic
                }

            } else {
                Log.w("GameViewModel", "Player cannot afford $towerTypeToPlace (Cost: $cost, Current: ${gameManager.currency}).")
                exitPlacementMode() // Exit placement mode on failed attempt
            }
        } else {
            Log.w("GameViewModel", "Placement is invalid according to GameManager for $towerTypeToPlace at ($x, $y).")
            exitPlacementMode() // Exit placement mode on failed attempt
        }
    }

    // TODO: Add functions to update currency from enemy defeats (called by GameManager)

}

// Dummy TowerType for compilation if not already defined elsewhere
// enum class TowerType { MUCUS, MACROPHAGE, COUGH }