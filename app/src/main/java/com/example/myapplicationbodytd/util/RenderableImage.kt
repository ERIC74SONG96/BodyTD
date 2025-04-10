package com.example.myapplicationbodytd.util


data class RenderableImage(
    val resId: Int,    // The image resource ID (e.g., R.drawable.enemy_blob)
    var x: Float,      // Current x-coordinate (in dp units)
    var y: Float,      // Current y-coordinate (in dp units)
    val size: Int = 64 // Default size (in dp)
)
