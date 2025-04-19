package com.example.myapplicationbodytd.game.effects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Interface for visual effects in the game.
 */
interface Effect {
    val isFinished: Boolean
    fun update(deltaTime: Float)
    fun draw(drawScope: DrawScope, offsetX: Float, offsetY: Float)
}

/**
 * A visual effect where an enemy fades out upon death.
 *
 * @param position The world position where the effect occurs.
 * @param color The original color of the enemy.
 * @param radius The original radius of the enemy.
 * @param duration The total time in seconds the fade should last.
 */
class FadeEffect(
    private val position: Offset,
    private val color: Color,
    private val radius: Float,
    private val duration: Float = 0.5f // Default fade duration 0.5 seconds
) : Effect {

    private var timer: Float = duration

    override val isFinished: Boolean
        get() = timer <= 0f

    override fun update(deltaTime: Float) {
        timer -= deltaTime
    }

    override fun draw(drawScope: DrawScope, offsetX: Float, offsetY: Float) {
        if (!isFinished) {
            val alpha = (timer / duration).coerceIn(0f, 1f)
            val center = Offset(position.x + offsetX, position.y + offsetY)
            drawScope.drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                center = center
            )
        }
    }
}

/**
 * A visual effect indicating an enemy was hit.
 * Shows a brief white flash over the enemy's position.
 *
 * @param position The world position where the effect occurs (enemy center).
 * @param radius The original radius of the enemy hit.
 * @param duration The total time the flash should last.
 */
class HitEffect(
    private val position: Offset,
    private val radius: Float,
    private val duration: Float = 0.15f // Default flash duration 0.15 seconds
) : Effect {
    private var timer: Float = duration

    override val isFinished: Boolean
        get() = timer <= 0f

    override fun update(deltaTime: Float) {
        timer -= deltaTime
    }

    override fun draw(drawScope: DrawScope, offsetX: Float, offsetY: Float) {
        if (!isFinished) {
            // Simple white flash, fades out quickly
            val alpha = (timer / duration).coerceIn(0f, 1f)
            val center = Offset(position.x + offsetX, position.y + offsetY)
            drawScope.drawCircle(
                color = Color.White.copy(alpha = alpha * 0.8f), // Semi-transparent white
                radius = radius, // Flash covers the enemy
                center = center
            )
        }
    }
}

// Add other effect types here later (e.g., ExplosionEffect) 