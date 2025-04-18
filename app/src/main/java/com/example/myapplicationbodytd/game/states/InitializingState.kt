package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.EconomyManager
import com.example.myapplicationbodytd.managers.GameManager

class InitializingState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Initializing State")
        // Reset core game systems
        EconomyManager.reset() // Reset currency
        // TODO: Reset WaveManager state if needed
        // TODO: Clear existing enemies/towers from previous game if any
        // TODO: Load map
        
        Log.d("GameState", "Initialization complete. Transitioning to WaveStarting...")
        gameManager.changeState(WaveStartingState(gameManager))
    }

    override fun update(deltaTime: Float) {
        // Usually nothing happens in the update of an initializing state
        // as it transitions immediately upon entering.
    }

    override fun exit() {
        Log.d("GameState", "Exiting Initializing State")
    }
} 