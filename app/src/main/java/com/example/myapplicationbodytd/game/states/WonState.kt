package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

class WonState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Won State - Congratulations!")
        // TODO: Display victory UI -> Handled by ViewModel/UI observing state
        // TODO: Stop game loop or pause updates
        gameManager.stopGameLoop()
        // TODO: Provide options to restart or quit -> Handled by ViewModel/UI observing state
        // Reuse isGameOver flag, UI will differentiate text based on actual state
        gameManager.setGameOver(true) 
    }

    override fun update(deltaTime: Float) {
        // Game loop is stopped, no updates needed
    }

    override fun exit() {
        Log.d("GameState", "Exiting Won State")
        // TODO: Hide victory UI -> Handled by ViewModel/UI observing state
        gameManager.setGameOver(false)
        // TODO: Reset game if restarting -> Reset is triggered by ViewModel calling changeState
    }
} 