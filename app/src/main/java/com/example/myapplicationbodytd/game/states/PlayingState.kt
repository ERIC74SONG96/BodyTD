package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.WaveManager

class PlayingState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        // Use StateFlow value for logging consistency
        val wave = gameManager.currentWave.value
        Log.i("GameState", "Entering Playing State for Wave $wave")

        // Start the wave spawning logic in WaveManager
        WaveManager.startWave(wave, gameManager, gameManager.gameMap.path)
        
        // TODO: Enable player interactions specific to active gameplay if needed
    }

    override fun update(deltaTime: Float) {
        // Call WaveManager update first to handle spawning for this frame
        WaveManager.update(deltaTime, gameManager, gameManager.gameMap.path)

        // Check for loss condition using StateFlow value
        if (gameManager.lives.value <= 0) {
            Log.w("GameState", "Loss condition met (lives <= 0). Transitioning to LostState.")
            gameManager.changeState(LostState(gameManager))
            return // Exit update early if game is lost
        }

        // Check for wave completion by polling WaveManager
        if (WaveManager.checkWaveCompletion()) {
             val currentWave = gameManager.currentWave.value
             // Check if the completed wave was the final one
            if (currentWave >= GameManager.MAX_WAVES) {
                Log.i("GameState", "Final wave ($currentWave) cleared. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                // Wave cleared, but not the last one. Go back to waiting state.
                Log.i("GameState", "Wave $currentWave cleared. Transitioning to WaveClearedState.")
                gameManager.changeState(WaveClearedState(gameManager))
            }
            return // Exit update early after state change
        }

        // Other playing state logic (e.g., enemy movement, tower attacks are handled by their own update methods)
    }

    override fun exit() {
        // Use StateFlow value for logging consistency
        Log.d("GameState", "Exiting Playing State for Wave ${gameManager.currentWave.value}")
        // TODO: Disable player interactions if necessary
    }
} 