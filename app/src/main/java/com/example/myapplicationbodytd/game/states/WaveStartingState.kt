package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import kotlinx.coroutines.*

class WaveStartingState(gameManager: GameManager) : GameState(gameManager) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var transitionJob: Job? = null

    override fun enter() {
        // Use StateFlow value
        val waveNum = gameManager.currentWave.value + 1 // Increment for the wave that is STARTING
        Log.d("GameState", "Entering WaveStarting State for Wave $waveNum")
        // Start the next wave in GameManager which updates the StateFlow
        gameManager.startNextWave()

        // TODO: Display "Wave Starting" UI / countdown?

        // Start a coroutine to automatically transition to PlayingState after a short delay
        transitionJob = scope.launch {
            delay(1500) // e.g., 1.5 second delay before wave actually starts playing
            Log.d("GameState", "Transitioning to PlayingState for Wave $waveNum")
            gameManager.changeState(PlayingState(gameManager))
        }
    }

    override fun update(deltaTime: Float) {
        // Coroutine handles the transition, can add countdown logic here if needed
    }

    override fun exit() {
        transitionJob?.cancel() // Ensure coroutine is cancelled if state exits early
        Log.d("GameState", "Exiting WaveStarting State")
        // TODO: Hide "Wave Starting" UI
    }
} 