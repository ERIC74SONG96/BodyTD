package com.example.myapplicationbodytd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment


data class Circle(val x: Float, val y: Float, val radius: Float)

fun isColliding(circle1: Circle, circle2: Circle): Boolean {
    val dx = circle1.x - circle2.x
    val dy = circle1.y - circle2.y
    val distance = kotlin.math.sqrt(dx * dx + dy * dy)
    return distance < (circle1.radius + circle2.radius)
}

@Composable
fun TowerWithRange() {
    val canvasSize = 500.dp
    val towerCenter = Offset(1000f, 1500f)

    val currentRange = 60f
    val upgradedRange = 100f

    val enemyPosition = remember { mutableStateOf(Offset(500f, 500f)) }
    val enemyRadius = 10f

    val currentRangeCircle = Circle(towerCenter.x, towerCenter.y, currentRange)
    val upgradedRangeCircle = Circle(towerCenter.x, towerCenter.y, upgradedRange)
    val enemyCircle = Circle(enemyPosition.value.x, enemyPosition.value.y, enemyRadius)

    val isInCurrentRange = isColliding(currentRangeCircle, enemyCircle)
    val isInUpgradedRange = isColliding(upgradedRangeCircle, enemyCircle)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier
            .size(canvasSize)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    enemyPosition.value = offset
                }
            }
        ) {
            // Upgraded range (faded)
            drawCircle(
                color = Color.LightGray,
                radius = upgradedRange,
                center = towerCenter,
                style = Stroke(width = 2.dp.toPx())
            )

            // Current range
            drawCircle(
                color = Color.Red,
                radius = currentRange,
                center = towerCenter,
                style = Stroke(width = 4.dp.toPx())
            )

            // Tower center
            drawCircle(
                color = Color.DarkGray,
                radius = 10f,
                center = towerCenter
            )

            // Enemy
            drawCircle(
                color = if (isInCurrentRange) Color.Green else if (isInUpgradedRange) Color.Yellow else Color.Black,
                radius = enemyRadius,
                center = enemyPosition.value
            )
        }

        Text("In Current Range: $isInCurrentRange")
        Text("In Upgraded Range: $isInUpgradedRange")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TowerWithRange()
            }
        }
    }
}
