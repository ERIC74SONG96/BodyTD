package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

class WonState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Won State - Congratulations!")
        // TODO: Display victory UI
        // TODO: Stop game loop or pause updates
        // TODO: Provide options to restart or quit
    }

    override fun update(deltaTime: Float) {
        // Typically no updates needed here, waits for player input (restart/quit)
    }

    override fun exit() {
        Log.d("GameState", "Exiting Won State")
        // TODO: Hide victory UI
        // TODO: Reset game if restarting
    }
} 