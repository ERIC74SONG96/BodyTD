package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.WaveManager

class PlayingState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        // Use StateFlow value for logging consistency
        Log.d("GameState", "Entering Playing State for Wave ${gameManager.currentWave.value}")
        // TODO: Enable player interactions (tower placement, etc.)
        // Assume WaveManager.startWave was called before transitioning here?
        // Need clarification on when startWave is called relative to state entry.
        // gameManager.startNextWave() // Should this be called here or elsewhere?
    }

    override fun update(deltaTime: Float) {
        // Call WaveManager update to handle spawning
        // TODO: Need access to the current map path from MapManager or similar
        val currentPath = emptyList<Pair<Int, Int>>() // Placeholder
        WaveManager.update(deltaTime, gameManager, currentPath)

        // Check for loss condition using StateFlow value
        if (gameManager.lives.value <= 0) {
            Log.d("GameState", "Loss condition met (lives <= 0). Transitioning to LostState.")
            gameManager.changeState(LostState(gameManager))
            return // Exit update early if game is lost
        }

        // Check for wave completion by polling WaveManager
        if (WaveManager.checkWaveCompletion()) {
             // Check win condition using StateFlow value
            if (gameManager.currentWave.value >= GameManager.MAX_WAVES) {
                Log.d("GameState", "Final wave cleared. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                // Use StateFlow value for logging consistency
                Log.d("GameState", "Wave ${gameManager.currentWave.value} cleared. Transitioning to WaveClearedState.")
                // TODO: Implement WaveClearedState if it's needed (e.g., for between-wave UI/pauses)
                gameManager.changeState(WaveClearedState(gameManager))
            }
            return // Exit update early after state change
        }

        // Other playing state logic (e.g., handling player input if not done via UI direct calls)
    }

    override fun exit() {
        // Use StateFlow value for logging consistency
        Log.d("GameState", "Exiting Playing State for Wave ${gameManager.currentWave.value}")
        // TODO: Disable player interactions if necessary
    }

    // Added method suggested by GameManager.startNextWave call
     fun startWave(waveNumber: Int) {
         Log.d("PlayingState", "Received instruction to start wave $waveNumber")
         // TODO: Implement logic needed when a wave explicitly starts within this state
         // e.g., trigger WaveManager to begin spawning for this wave number.
         // WaveManager.startSpawningForWave(waveNumber) // Example call
     }
} 