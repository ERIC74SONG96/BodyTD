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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationbodytd.R
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.towers.strategies.CoughTower
import com.example.myapplicationbodytd.towers.strategies.MacrophageTower
import com.example.myapplicationbodytd.towers.strategies.MucusTower
import com.example.myapplicationbodytd.util.RenderableImage
import kotlinx.coroutines.delay

/**
 * Composable that renders multiple images at specified positions
 */
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

/**
 * Composable that displays the game HUD
 */
@Composable
fun GameHUD(
    money: Int,
    wave: Int,
    time: Float,
    onStartGame: () -> Unit,
    onPlaceMucusTower: () -> Unit,
    onPlaceMacrophageTower: () -> Unit,
    onPlaceCoughTower: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(8.dp)
    ) {
        // Game info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Money: $$money",
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = "Wave: $wave",
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = "Time: ${String.format("%.1f", time)}s",
                color = Color.White,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tower buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onPlaceMucusTower,
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Mucus (10$)")
            }
            
            Button(
                onClick = onPlaceMacrophageTower,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Macro (20$)")
            }
            
            Button(
                onClick = onPlaceCoughTower,
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Cough (10$)")
            }
        }
    }
}

/**
 * Main game view composable
 */
@Composable
fun GameView() {
    // Get game manager instance
    val gameManager = remember { GameManager.getInstance() }
    
    // Game state
    var gameStarted by remember { mutableStateOf(false) }
    var selectedTowerType by remember { mutableStateOf<String?>(null) }
    
    // Game time tracking
    var gameTime by remember { mutableStateOf(0f) }
    
    // Start game loop
    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            gameManager.startGame()
            
            while (true) {
                // Update game time
                gameTime += 0.016f // Approximately 60 FPS
                
                // Update game state
                gameManager.update(0.016f)
                
                // Check for game over
                if (gameManager.isGameOver()) {
                    gameStarted = false
                    break
                }
                
                // Wait for next frame
                delay(16L)
            }
        }
    }
    
    // Game content
    Box(modifier = Modifier.fillMaxSize()) {
        // Game background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E3A8A)) // Dark blue background
        )
        
        // Game elements
        if (gameStarted) {
            // Render game objects
            val renderableObjects by gameManager.renderableObjects.collectAsState()
            MultiImageView(renderableImages = renderableObjects)
            
            // Game HUD
            GameHUD(
                money = gameManager.getMoney(),
                wave = gameManager.getCurrentWave(),
                time = gameTime,
                onStartGame = { gameStarted = true },
                onPlaceMucusTower = { selectedTowerType = "MucusTower" },
                onPlaceMacrophageTower = { selectedTowerType = "MacrophageTower" },
                onPlaceCoughTower = { selectedTowerType = "CoughTower" }
            )
        } else {
            // Start game screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BodyTD",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { gameStarted = true },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                ) {
                    Text("Start Game", fontSize = 18.sp)
                }
            }
        }
    }
}
