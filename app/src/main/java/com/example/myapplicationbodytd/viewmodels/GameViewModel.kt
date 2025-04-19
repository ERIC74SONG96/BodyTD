package com.example.myapplicationbodytd.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.myapplicationbodytd.game.entities.CoughTower
import com.example.myapplicationbodytd.game.entities.Enemy
import com.example.myapplicationbodytd.game.entities.MacrophageTower
import com.example.myapplicationbodytd.game.entities.MucusTower
import com.example.myapplicationbodytd.game.entities.Tower
import com.example.myapplicationbodytd.game.map.Map
import com.example.myapplicationbodytd.game.states.GameState
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.ui.TowerType
import kotlinx.coroutines.flow.StateFlow
import com.example.myapplicationbodytd.game.factories.TowerFactory
import com.example.myapplicationbodytd.game.states.InitializingState
import com.example.myapplicationbodytd.game.effects.Effect

/**
 * ViewModel for the main Game Screen.
 * Manages the game state exposed to the UI by observing GameManager's StateFlows.
 */
class GameViewModel(private val gameManager: GameManager) : ViewModel() {

    // --- Game Logic Integration ---

    // Expose Map directly (assuming it's relatively static after init)
    val gameMap: Map = gameManager.gameMap

    // --- Game State Flows (Observed from GameManager) ---
    val lives: StateFlow<Int> = gameManager.lives
    val currency: StateFlow<Int> = gameManager.currency
    val gameState: StateFlow<GameState?> = gameManager.gameState
    val currentWave: StateFlow<Int> = gameManager.currentWave
    val maxWaves: Int = GameManager.MAX_WAVES
    val drawTick: StateFlow<Long> = gameManager.drawTick
    val isGameOver: StateFlow<Boolean> = gameManager.isGameOver
    val waveClearMessage: StateFlow<String?> = gameManager.waveClearMessage

    // Expose SnapshotStateLists directly - Compose observes these
    val enemies: SnapshotStateList<Enemy> = gameManager.activeEnemies
    val towers: SnapshotStateList<Tower> = gameManager.placedTowers
    val activeEffects: SnapshotStateList<Effect> = gameManager.activeEffects

    // --- UI-Specific State --- 
    private val _placementMode = mutableStateOf(false)
    val placementMode: State<Boolean> = _placementMode

    private val _selectedTowerType = mutableStateOf<TowerType?>(null) // Renamed
    val selectedTowerType: State<TowerType?> = _selectedTowerType // Renamed

    init {
        Log.d("GameViewModel", "Initializing GameViewModel.")
        // No need for collectors here anymore, we expose GameManager flows directly
    }

    // --- Helper to check if Start Wave button should be enabled --- 
    // This logic likely needs to be moved to where the button is defined (e.g., GameScreen) 
    // and observe the gameState flow directly.
    // val canStartNextWave: Boolean 
    //     get() = gameState.value is WaveClearedState // Observe gameState flow instead

    // --- Placement Actions ---
    fun enterPlacementMode(towerType: TowerType) {
        // Check affordability before entering placement mode
        val cost = getTowerCost(towerType) ?: Int.MAX_VALUE
        if (gameManager.currency.value >= cost) { // Check against current currency StateFlow
            _selectedTowerType.value = towerType // Use renamed state
            _placementMode.value = true
            Log.d("GameViewModel", "Entered placement mode for $towerType")
        } else {
            Log.w("GameViewModel", "Cannot enter placement mode for $towerType: Insufficient funds (Need: $cost, Have: ${gameManager.currency.value})")
            // Optionally show feedback to user (e.g., Toast)
        }
    }

    fun exitPlacementMode() {
        _selectedTowerType.value = null // Use renamed state
        _placementMode.value = false
        Log.d("GameViewModel", "Exited placement mode")
    }

    fun togglePlacementMode(towerType: TowerType) {
        if (_placementMode.value && _selectedTowerType.value == towerType) { // Use renamed state
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

        val towerTypeToPlace = _selectedTowerType.value // Use renamed state
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

        if (gameManager.canPlaceTowerAt(x, y)) {
            Log.d("GameViewModel", "Placement location seems valid for $towerTypeToPlace at ($x, $y).")

            // Create the actual Tower instance using the factory
            val towerInstance: Tower? = TowerFactory.createTower(towerTypeToPlace, Pair(x, y), gameManager)

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
                Log.e("GameViewModel", "TowerFactory failed to create instance for tower type $towerTypeToPlace")
                exitPlacementMode()
            }
        } else {
            Log.w("GameViewModel", "Placement location is invalid according to GameManager for $towerTypeToPlace at ($x, $y).")
            exitPlacementMode() // Exit placement mode on invalid location
        }
    }

    // Helper function to get cost from Tower companion objects
    internal fun getTowerCost(towerType: TowerType): Int? {
        return when (towerType) {
            TowerType.MUCUS -> MucusTower.COST
            TowerType.MACROPHAGE -> MacrophageTower.COST
            TowerType.COUGH -> CoughTower.COST
            // Add other tower types here
        }
    }

    // --- Game Actions ---
    fun requestNextWave() {
        Log.d("GameViewModel", "Requesting GameManager to start the next wave.")
        gameManager.requestNextWave()
    }

    fun restartGame() {
        Log.d("GameViewModel", "Restarting game...")
        // TODO: Implement full reset logic in GameManager or InitializingState
        // The reset logic is now called within InitializingState.enter() via GameManager.reset()
        gameManager.changeState(InitializingState(gameManager))
        // Ensure game loop restarts if it was stopped
        // gameManager.startGameLoop() // GameManager needs a public startGameLoop or reset method
    }

    // --- Configuration Updates ---
    fun updateCellSize(newSize: Float) {
        // Relay the cell size update to the GameManager
        gameManager.updateCellSize(newSize)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameViewModel", "GameViewModel is being cleared.")
        // No need to explicitly cancel gameManager's scope here, as GameManager is a singleton
        // But if GameManager's lifecycle were tied to ViewModel, you'd cancel its scope here.
    }
}

// Dummy TowerType for compilation if not already defined elsewhere
// enum class TowerType { MUCUS, MACROPHAGE, COUGH }
// enum class TowerType { MUCUS, MACROPHAGE, COUGH }