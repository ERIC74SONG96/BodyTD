package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

/**
 * Represents the state where a wave has been cleared, but the next one hasn't started.
 * Waits for the player to initiate the next wave.
 */
class WaveClearedState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Wave Cleared State")
        // TODO: Update UI to show "Wave Cleared! Ready for Wave X" and enable Start Wave button
        // -> Button enabling is handled by UI observing gameState
        val currentWave = gameManager.currentWave.value
        val message = "Wave $currentWave Cleared! Ready for Wave ${currentWave + 1}?"
        gameManager.setWaveClearMessage(message)
    }

    override fun update(deltaTime: Float) {
        // Nothing to update, waiting for player input via UI (Start Wave button)
    }

    override fun exit() {
        Log.d("GameState", "Exiting Wave Cleared State")
        // TODO: Hide any "Wave Cleared" UI elements if necessary
        gameManager.setWaveClearMessage(null)
    }
} 