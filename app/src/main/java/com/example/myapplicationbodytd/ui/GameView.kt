package com.example.myapplicationbodytd.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.player.Player
import com.example.myapplicationbodytd.map.GameMap // ← supposé
import com.example.myapplicationbodytd.ui.HUD     // ← supposé

class GameView(
    context: Context,
    private val gameManager: GameManager,
    private val player: Player
) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private val thread: Thread = Thread(this)
    private var running = false
    private val targetFPS = 60

    private val paint = Paint()
    private val map = GameMap() // ← ou ton propre objet de carte
    private val hud = HUD()

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        resume()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        pause()
    }

    fun resume() {
        running = true
        thread.start()
    }

    fun pause() {
        running = false
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var lastTime = System.nanoTime()

        while (running) {
            val now = System.nanoTime()
            val deltaTime = (now - lastTime) / 1_000_000_000f
            lastTime = now

            gameManager.update(deltaTime)

            val canvas = holder.lockCanvas()
            if (canvas != null) {
                try {
                    map.draw(canvas, paint)
                    hud.draw(canvas, paint)
                    hud.drawTowerMenu(canvas, paint)
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            val targetFrameTime = 1000 / targetFPS
            val elapsedTime = (System.nanoTime() - now) / 1_000_000
            val sleepTime = targetFrameTime - elapsedTime

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
