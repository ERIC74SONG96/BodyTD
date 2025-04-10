//package com.example.myapplicationbodytd.ui
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import com.example.myapplicationbodytd.managers.GameManager
//import com.example.myapplicationbodytd.player.Player
//import com.example.myapplicationbodytd.map.GameMap // ← supposé
//import com.example.myapplicationbodytd.ui.HUD     // ← supposé
//
//class GameView(
//    context: Context,
//    private val gameManager: GameManager,
//    private val player: Player
//) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
//
//    private val thread: Thread = Thread(this)
//    private var running = false
//    private val targetFPS = 60
//
//    private val paint = Paint()
//    private val map = GameMap() // ← ou ton propre objet de carte
//    private val hud = HUD()
//
//    init {
//        holder.addCallback(this)
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        resume()
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        pause()
//    }
//
//    fun resume() {
//        running = true
//        thread.start()
//    }
//
//    fun pause() {
//        running = false
//        try {
//            thread.join()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun run() {
//        var lastTime = System.nanoTime()
//
//        while (running) {
//            val now = System.nanoTime()
//            val deltaTime = (now - lastTime) / 1_000_000_000f
//            lastTime = now
//
//            gameManager.update(deltaTime)
//
//            val canvas = holder.lockCanvas()
//            if (canvas != null) {
//                try {
//                    map.draw(canvas, paint)
//                    hud.draw(canvas, paint)
//                    hud.drawTowerMenu(canvas, paint)
//                } finally {
//                    holder.unlockCanvasAndPost(canvas)
//                }
//            }
//
//            val targetFrameTime = 1000 / targetFPS
//            val elapsedTime = (System.nanoTime() - now) / 1_000_000
//            val sleepTime = targetFrameTime - elapsedTime
//
//            if (sleepTime > 0) {
//                try {
//                    Thread.sleep(sleepTime)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//}


//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.AsyncImage
//import com.example.myapplicationbodytd.R
//
//@Composable
//fun GameView() {
//    val infiniteTransition = rememberInfiniteTransition()
//    val xOffset by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 300f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 3000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AsyncImage(
//            model = R.drawable.enemyyy, // Ensure this is your animated WebP
//            contentDescription = "Animated Enemy",
//            modifier = Modifier
//                .offset(x = xOffset.dp, y = 200.dp)
//                .size(64.dp)
//        )
//    }
//}

package com.example.myapplicationbodytd.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplicationbodytd.util.RenderableImage


import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.myapplicationbodytd.R

@Composable
fun MultiImageView(renderableImages: List<RenderableImage>) {
    Box(modifier = Modifier.fillMaxSize()) {
        renderableImages.forEach { image ->
            val painter = painterResource(id = image.resId)
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .offset(x = image.x.dp, y = image.y.dp)
                    .size(image.size.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun GameView() {
    // Create a mutable list of images
    val images = remember {
        mutableStateListOf(
            RenderableImage(resId = R.drawable.mac, x = 0f, y = 0f),
            RenderableImage(resId = R.drawable.mac, x = 50f, y = 50f)
        )
    }

    // Animate the images: update positions every frame (~60 FPS)
    LaunchedEffect(Unit) {
        while (true) {
            images.forEach { image ->
                // Simple movement: increase x and y coordinates
                image.x += 2f  // Move to the right
                image.y += 1f  // Move downward
            }
            delay(16L) // Approximately 60 FPS
        }
    }

    // Render the images
    MultiImageView(renderableImages = images)
}
