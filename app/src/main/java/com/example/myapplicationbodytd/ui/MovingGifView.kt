package com.example.myapplicationbodytd.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background

//@Composable
//fun MovingGifView() {
//    // Create an infinite transition for the x-offset animation
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
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black),
//        contentAlignment = Alignment.Center
//    ) {
//        // Use AsyncImage to load and display the GIF
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(R.drawable.animated_enemy) // Place your gif in res/drawable/animated_enemy.gif
//                .crossfade(true)
//                .build(),
//            contentDescription = "Animated enemy",
//            modifier = Modifier
//                .offset(x = xOffset.dp, y = 100.dp)
//                .size(100.dp)
//        )
//    }
//}
