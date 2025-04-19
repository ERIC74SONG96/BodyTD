package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

class InitializingState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Initializing State")
        // Call GameManager to reset everything
        gameManager.reset()
        // TODO: Reset WaveManager state if needed <- Handled by gameManager.reset()
        // TODO: Clear existing enemies/towers from previous game if any <- Handled by gameManager.reset()
        // TODO: Load map <- Handled by GameManager init/reset

        // Consider transitioning immediately to PlayingState or WaveClearedState for Wave 0/1
        // For now, assume UI/ViewModel handles the initial "Start Game" prompt
    }

    override fun update(deltaTime: Float) {
        // Nothing to update during initialization itself
    }

    override fun exit() {
        Log.d("GameState", "Exiting Initializing State")
    }
} 