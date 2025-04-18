package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import kotlinx.coroutines.*

class WaveClearedState(gameManager: GameManager) : GameState(gameManager) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var transitionJob: Job? = null
    private var timer: Float = 3.0f // Time before starting next wave

    override fun enter() {
        // Use StateFlow value for consistency
        Log.d("GameState", "Entering WaveCleared State after Wave ${gameManager.currentWave.value}")
        // TODO: Display wave cleared UI
        // TODO: Provide button to start next wave immediately?

        // Start a coroutine to wait briefly before transitioning
        transitionJob = scope.launch {
            delay(2000) // Wait 2 seconds before next wave or win screen

            if (gameManager.currentWave.value >= GameManager.MAX_WAVES) {
                Log.d("GameState", "Max waves reached. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                Log.d("GameState", "Timer elapsed. Transitioning to WaveStartingState for next wave.")
                gameManager.changeState(WaveStartingState(gameManager))
            }
        }
    }

    override fun update(deltaTime: Float) {
        timer -= deltaTime
        if (timer <= 0f) {
            // Check if max waves reached before attempting to start next
            if (gameManager.currentWave.value >= GameManager.MAX_WAVES) {
                Log.d("GameState", "Max waves reached. Transitioning to WonState.")
                gameManager.changeState(WonState(gameManager))
            } else {
                Log.d("GameState", "Timer elapsed. Transitioning to WaveStartingState for next wave.")
                gameManager.changeState(WaveStartingState(gameManager))
            }
        }
    }

    override fun exit() {
        Log.d("GameState", "Exiting WaveCleared State")
        transitionJob?.cancel() // Cancel transition if state is exited prematurely
        // TODO: Hide wave cleared UI
    }
} 