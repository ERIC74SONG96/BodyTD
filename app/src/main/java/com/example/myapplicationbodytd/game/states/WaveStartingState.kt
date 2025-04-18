package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.WaveManager

class WaveStartingState(gameManager: GameManager) : GameState(gameManager) {

    private var countdownTimer: Float = 3.0f // Countdown before wave starts

    override fun enter() {
        // Increment wave number in GameManager
        gameManager.startNextWave()
        val waveNum = gameManager.getCurrentWave()
        Log.d("GameState", "Entering WaveStarting State for Wave $waveNum")
        countdownTimer = 3.0f

        // Prepare WaveManager for the upcoming wave
        // TODO: Need access to the map path
        val currentPath = emptyList<Pair<Int, Int>>() // Placeholder
        WaveManager.startWave(waveNum, gameManager, currentPath)

        // TODO: Display wave number and countdown UI
    }

    override fun update(deltaTime: Float) {
        countdownTimer -= deltaTime
        Log.d("GameState", "Wave ${gameManager.getCurrentWave()} starting in: ${"%.1f".format(countdownTimer)}s")
        // TODO: Update countdown UI

        if (countdownTimer <= 0f) {
            Log.d("GameState", "Countdown finished. Transitioning to Playing...")
            gameManager.changeState(PlayingState(gameManager))
        }
    }

    override fun exit() {
        Log.d("GameState", "Exiting WaveStarting State for Wave ${gameManager.getCurrentWave()}")
        // TODO: Hide countdown UI
    }
} 