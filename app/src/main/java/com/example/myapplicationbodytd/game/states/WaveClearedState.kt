package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

/**
 * Represents the state between waves, where the player can prepare and initiate the next wave.
 */
class WaveClearedState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        val nextWave = gameManager.currentWave.value + 1
        Log.i("GameState", "Entering WaveCleared State. Ready for Wave $nextWave")
        // TODO: Update UI to show "Wave Cleared! Ready for Wave X" and enable Start Wave button
    }

    override fun update(deltaTime: Float) {
        // Do nothing in update. Waiting for user input via the UI button.
        // The UI button click will call GameManager.requestNextWave()
    }

    override fun exit() {
        Log.d("GameState", "Exiting WaveCleared State")
        // TODO: Hide any "Wave Cleared" UI elements if necessary
    }
} 