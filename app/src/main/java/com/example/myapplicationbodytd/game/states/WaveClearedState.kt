package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import kotlinx.coroutines.*

class WaveClearedState(gameManager: GameManager) : GameState(gameManager) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var transitionJob: Job? = null

    override fun enter() {
        Log.d("GameState", "Entering WaveCleared State after Wave ${gameManager.getCurrentWave()}")
        // TODO: Display wave cleared UI

        // Start a coroutine to wait briefly before transitioning
        transitionJob = scope.launch {
            delay(2000) // Wait 2 seconds before next wave or win screen

            if (gameManager.getCurrentWave() >= GameManager.MAX_WAVES) {
                Log.d("GameState", "Max waves reached. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                Log.d("GameState", "Transitioning back to WaveStartingState for next wave.")
                gameManager.changeState(WaveStartingState(gameManager))
            }
        }
    }

    override fun update(deltaTime: Float) {
        // Coroutine handles the transition, nothing needed here
    }

    override fun exit() {
        Log.d("GameState", "Exiting WaveCleared State")
        transitionJob?.cancel() // Cancel transition if state is exited prematurely
        // TODO: Hide wave cleared UI
    }
} 