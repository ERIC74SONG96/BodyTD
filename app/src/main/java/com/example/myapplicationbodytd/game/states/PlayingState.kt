package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.WaveManager

class PlayingState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Playing State for Wave ${gameManager.getCurrentWave()}")
        // TODO: Enable player interactions (tower placement, etc.)
        // Assume WaveManager.startWave was called before transitioning here
    }

    override fun update(deltaTime: Float) {
        // Call WaveManager update to handle spawning
        // TODO: Need access to the current map path
        // val currentPath = gameManager.map.path // Example
        val currentPath = emptyList<Pair<Int, Int>>() // Placeholder
        WaveManager.update(deltaTime, gameManager, currentPath)

        // Check for win/loss conditions
        if (gameManager.getCurrentLives() <= 0) {
            Log.d("GameState", "Loss condition met (lives <= 0). Transitioning to LostState.")
            gameManager.changeState(LostState(gameManager))
            return // Exit update early if game is lost
        }

        // Check for wave completion by polling WaveManager
        if (WaveManager.checkWaveCompletion()) {
            if (gameManager.getCurrentWave() >= GameManager.MAX_WAVES) {
                Log.d("GameState", "Final wave cleared. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                Log.d("GameState", "Wave ${gameManager.getCurrentWave()} cleared. Transitioning to WaveClearedState.")
                gameManager.changeState(WaveClearedState(gameManager))
            }
            return // Exit update early after state change
        }
    }

    override fun exit() {
        Log.d("GameState", "Exiting Playing State for Wave ${gameManager.getCurrentWave()}")
        // TODO: Disable player interactions if necessary
    }
} 