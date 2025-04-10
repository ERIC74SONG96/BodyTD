package com.example.myapplicationbodytd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationbodytd.ui.GameView


// A simple placeholder for your game screen.
// Later, you will replace this with your game logic and drawing.
//@Composable
//fun GameScreen() {
//    GameView()
////    Column(
////        modifier = Modifier
////            .fillMaxSize()
////            .padding(16.dp),
////        verticalArrangement = Arrangement.Center,
////        horizontalAlignment = Alignment.CenterHorizontally
////    ) {
////        Text(
////            text = "Welcome to BodyTD",
////            color = Color.White,
////            fontSize = 24.sp,
////            textAlign = TextAlign.Center
////        )
////    }
//}

//// The main Composable app function that sets the background and calls GameScreen.
//@Composable
//fun BodyTDApp() {
//    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
//        GameScreen()
//    }
//}

// MainActivity that sets the content view using Jetpack Compose.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                GameView()
            }
        }
    }
}

