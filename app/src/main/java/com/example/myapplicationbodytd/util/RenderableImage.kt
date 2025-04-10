package com.example.myapplicationbodytd.util

/**
 * Data class representing a renderable image in the game
 * @param resId The resource ID of the image to render
 * @param x The x coordinate of the image
 * @param y The y coordinate of the image
 * @param size The size of the image in dp
 */
data class RenderableImage(
    val resId: Int,    // The image resource ID (e.g., R.drawable.enemy_blob)
    var x: Float,      // Current x-coordinate (in dp units)
    var y: Float,      // Current y-coordinate (in dp units)
    val size: Float = 48f // Default size (in dp)
)
