package com.example.myapplicationbodytd.game.states

import android.util.Log
import com.example.myapplicationbodytd.managers.GameManager

class LostState(gameManager: GameManager) : GameState(gameManager) {

    override fun enter() {
        Log.d("GameState", "Entering Lost State - Game Over!")
        // TODO: Display game over UI -> Handled by ViewModel/UI observing state
        // TODO: Stop game loop or pause updates
        gameManager.stopGameLoop()
        // TODO: Provide options to restart or quit -> Handled by ViewModel/UI observing state
        gameManager.setGameOver(true) // Signal ViewModel/UI
    }

    override fun update(deltaTime: Float) {
        // Game loop is stopped, no updates needed
    }

    override fun exit() {
        Log.d("GameState", "Exiting Lost State")
        // TODO: Hide game over UI -> Handled by ViewModel/UI observing state
        gameManager.setGameOver(false) // Signal ViewModel/UI
        // TODO: Reset game if restarting -> Reset is triggered by ViewModel calling changeState
    }
} 