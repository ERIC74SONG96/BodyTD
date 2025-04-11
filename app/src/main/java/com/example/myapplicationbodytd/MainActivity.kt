package com.example.myapplicationbodytd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.ui.GameView

class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise GameManager
        gameManager = GameManager()

        // Initialise GameView avec GameManager
        gameView = GameView(this, gameManager)

        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
}
