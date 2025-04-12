package com.example.myapplicationbodytd.managers

import com.example.myapplicationbodytd.player.Player
import com.example.myapplicationbodytd.ui.GameMap
import com.example.myapplicationbodytd.ui.HUD


class GameManager {
    private val waveManager = WaveManager()
    private val map = GameMap()
    private val player = Player()
    private val hud = HUD()
    private var gameOver: Boolean = false

    fun startGame() {
        // init game loop
    }

    fun update(time: Float) {
        if (checkGameOver()) return
        hud.updateDisplay(player.money, waveManager.getCurrentWave(), time)
    }

    fun turn(position: Position) {
        // handle user interaction
    }

    private fun checkGameOver(): Boolean {
        // check if any enemy reached the end or player lost
        return gameOver
    }

    fun getInstance(): GameManager = this
}

