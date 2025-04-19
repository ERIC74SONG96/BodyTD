package com.example.myapplicationbodytd.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.entities.CoughTower // Import specific towers
import com.example.myapplicationbodytd.game.entities.MacrophageTower
import com.example.myapplicationbodytd.game.entities.MucusTower
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.game.map.Map
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import com.example.myapplicationbodytd.ui.TowerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.CancellationException
import com.example.myapplicationbodytd.managers.WaveManager
import com.example.myapplicationbodytd.game.states.GameState // Import GameState
import kotlinx.coroutines.flow.StateFlow // Import StateFlow

/**
 * ViewModel for the main Game Screen.
 * Manages the game state exposed to the UI by observing GameManager's StateFlows.
 */
class GameViewModel(private val gameManager: GameManager) : ViewModel() {

    // --- Game Logic Integration ---

    // --- Game State (exposed to UI via Compose State) ---
    // These are updated by collectors observing GameManager's StateFlows

    // TODO: Map state - Assuming GameManager provides a static map or a Flow for it.
    // For now, let's assume map doesn't change frequently after init.
    private val _map = mutableStateOf<Map>(gameManager.gameMap) // Corrected reference
    val map: State<Map> = _map

    private val _enemies = mutableStateOf<List<Enemy>>(emptyList())
    val enemies: State<List<Enemy>> = _enemies

    private val _towers = mutableStateOf<List<Tower>>(emptyList())
    val towers: State<List<Tower>> = _towers

    private val _currency = mutableIntStateOf(0)
    val currency: State<Int> = _currency

    private val _lives = mutableIntStateOf(0)
    val lives: State<Int> = _lives

    private val _wave = mutableIntStateOf(0)
    val wave: State<Int> = _wave

    // --- Placement State ---
    private val _placementMode = mutableStateOf(false)
    val placementMode: State<Boolean> = _placementMode

    private val _selectedTowerForPlacement = mutableStateOf<TowerType?>(null)
    val selectedTowerForPlacement: State<TowerType?> = _selectedTowerForPlacement

    // --- Game State Flows (Observed by UI) ---
    // These are managed internally by observing GameManager
    // Remove the conflicting direct exposures:
    // val gameState: StateFlow<GameState?> = gameManager.gameState
    // val lives: StateFlow<Int> = gameManager.lives
    // val currency: StateFlow<Int> = gameManager.currency
    // val currentWave: StateFlow<Int> = gameManager.currentWave
    // val activeEnemies: StateFlow<List<Enemy>> = gameManager.activeEnemies
    // val placedTowers: StateFlow<List<Tower>> = gameManager.placedTowers

    init {
        Log.d("GameViewModel", "Initializing GameViewModel and starting observers...")
        observeGameManagerStates()
    }

    private fun observeGameManagerStates() {
        Log.d("GameViewModel", "Setting up collectors for GameManager StateFlows...")

        viewModelScope.launch {
            try {
                gameManager.lives.onEach { Log.d("GameViewModel", "Observed lives: $it") }.collect { _lives.intValue = it }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "Lives collection cancelled.")
                throw e // Re-throw cancellation exceptions
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting lives state", e)
                // Handle error appropriately, e.g., show error message
            }
        }
        viewModelScope.launch {
             try {
                 gameManager.currency.onEach { Log.d("GameViewModel", "Observed currency: $it") }.collect { _currency.intValue = it }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "Currency collection cancelled.")
                throw e
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting currency state", e)
            }
        }
        viewModelScope.launch {
            try {
                gameManager.currentWave.onEach { Log.d("GameViewModel", "Observed wave: $it") }.collect { _wave.intValue = it }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "Wave collection cancelled.")
                throw e
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting wave state", e)
            }
        }
        viewModelScope.launch {
            try {
                gameManager.gameState.onEach { Log.d("GameViewModel", "Observed gameState: ${it?.javaClass?.simpleName}") }.collect { state ->
                    // Optionally update a ViewModel state specific to the game phase
                }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "GameState collection cancelled.")
                throw e
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting gameState", e)
            }
        }
        viewModelScope.launch {
            try {
                gameManager.activeEnemies.onEach { Log.d("GameViewModel", "Observed enemies: ${it.size}") }.collect { _enemies.value = it }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "Enemies collection cancelled.")
                throw e
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting enemies state", e)
            }
        }
        viewModelScope.launch {
            try {
                gameManager.placedTowers.onEach { Log.d("GameViewModel", "Observed towers: ${it.size}") }.collect { _towers.value = it }
            } catch (e: CancellationException) {
                Log.d("GameViewModel", "Towers collection cancelled.")
                throw e
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error collecting towers state", e)
            }
        }

        // TODO: Add Map StateFlow collection if/when GameManager exposes it dynamically
        // For now, map is assumed static after init
    }

    // --- Placement Actions ---
    fun enterPlacementMode(towerType: TowerType) {
        // Check affordability before entering placement mode
        val cost = getTowerCost(towerType) ?: Int.MAX_VALUE
        if (gameManager.currency.value >= cost) { // Check against current currency StateFlow
            _selectedTowerForPlacement.value = towerType
            _placementMode.value = true
            Log.d("GameViewModel", "Entered placement mode for $towerType")
        } else {
            Log.w("GameViewModel", "Cannot enter placement mode for $towerType: Insufficient funds (Need: $cost, Have: ${gameManager.currency.value})")
            // Optionally show feedback to user (e.g., Toast)
        }
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
            enterPlacementMode(towerType) // Will check affordability
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

        // --- Use GameManager for placement logic ---

        // Get cost from the Tower's companion object
        val cost = getTowerCost(towerTypeToPlace)
        if (cost == null) {
            Log.e("GameViewModel", "Could not determine cost for tower type $towerTypeToPlace")
            exitPlacementMode()
            return
        }

        // TODO: Update GameManager interface/methods if needed for canPlaceTowerAt
        if (gameManager.canPlaceTowerAt(x, y)) { // Placeholder check
            Log.d("GameViewModel", "Placement location seems valid for $towerTypeToPlace at ($x, $y).")

            // Create the actual Tower instance
            // TODO: Use a TowerFactory or more robust creation mechanism
            val towerInstance: Tower? = createTowerInstance(towerTypeToPlace, x, y)

            if (towerInstance != null) {
                // Attempt placement via GameManager (handles cost check again internally + registration)
                if (gameManager.placeTower(towerInstance, cost)) {
                    Log.i("GameViewModel", "Tower $towerTypeToPlace placed successfully at ($x, $y) by GameManager.")
                    exitPlacementMode() // Exit placement mode after successful placement
                } else {
                    Log.w("GameViewModel", "GameManager failed to place tower $towerTypeToPlace at ($x, $y) (e.g., insufficient funds or other reason).")
                    exitPlacementMode() // Exit if placement fails
                }
            } else {
                Log.e("GameViewModel", "Could not create instance for tower type $towerTypeToPlace")
                exitPlacementMode()
            }
        } else {
            Log.w("GameViewModel", "Placement location is invalid according to GameManager for $towerTypeToPlace at ($x, $y).")
            exitPlacementMode() // Exit placement mode on invalid location
        }
    }

    // Helper function to get cost from Tower companion objects
    private fun getTowerCost(towerType: TowerType): Int? {
        return when (towerType) {
            TowerType.MUCUS -> MucusTower.COST
            TowerType.MACROPHAGE -> MacrophageTower.COST
            TowerType.COUGH -> CoughTower.COST
            // Add other tower types here
        }
    }

    // Helper function to create tower instances
    private fun createTowerInstance(towerType: TowerType, x: Int, y: Int): Tower? {
         return when (towerType) {
             TowerType.MUCUS -> MucusTower(Pair(x, y), gameManager)
             TowerType.MACROPHAGE -> MacrophageTower(Pair(x, y), gameManager)
             TowerType.COUGH -> CoughTower(Pair(x, y), gameManager)
             // Add other tower types here
         }
    }

    // TODO: Add functions to update currency from enemy defeats (called by GameManager)

}

// Dummy TowerType for compilation if not already defined elsewhere
// enum class TowerType { MUCUS, MACROPHAGE, COUGH }
// enum class TowerType { MUCUS, MACROPHAGE, COUGH }