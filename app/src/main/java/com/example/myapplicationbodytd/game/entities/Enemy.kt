package com.example.myapplicationbodytd.game.entities

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.Updatable
import com.example.myapplicationbodytd.util.CoordinateConverter
import kotlin.math.max

/**
 * **Inheritance:** Base class for all enemy types.
 * Defines common properties (health, speed, reward, path, position) and behaviors
 * (movement `move()`, taking damage `takeDamage()`, `onDeath()`, `onReachEnd()`).
 *
 * **Encapsulation:** Uses `protected` or `private set` where appropriate to control
 * direct modification of internal state (e.g., `position`, `isSlowed`).
 *
 * **Polymorphism:** Subclasses (`Virus`, `Bacteria`, etc.) inherit from `Enemy` and can
 * override methods like `updateStatusEffects`, `onDeath` to provide specialized behavior.
 * The abstract `getType()` method forces subclasses to provide their specific type identifier.
 */
@Stable
abstract class Enemy(
    // Core stats
    initialHealth: Float,
    val speed: Float, // Tiles per second - Immutable, good for stability
    val reward: Int, // Immutable, good for stability
    protected val path: List<Pair<Int, Int>>, // Immutable list, good for stability
    // Note: GameManager reference might affect stability if GameManager itself isn't stable,
    // but since it's a singleton object, Compose likely treats it as stable.
    protected val gameManager: GameManager // Reference to GameManager for event reporting

) : Updatable { // Implement Updatable for the game loop

    // Path traversal state
    var currentPathIndex: Int = 0 // Mutable var
        protected set
    var progressAlongSegment: Float = 0f // Mutable var
        protected set

    // Derived position based on path and progress
    var position: Offset = Offset.Zero // Mutable var - Offset itself is Immutable
        protected set // Internal calculation updates this

    // Status Effects
    var isSlowed: Boolean = false // Mutable var
        private set
    private var slowTimer: Float = 0f
    private var slowFactor: Float = 1.0f // 1.0 means normal speed

    // Health and Status
    var health: Float = initialHealth // Mutable var
    val maxHealth: Float = initialHealth // Immutable val, good
    val isDead: Boolean
        get() = health <= 0f

    val hasReachedEnd: Boolean
        get() = currentPathIndex >= path.size - 1 && progressAlongSegment >= 1.0f

    init {
        // Ensure path is not empty
        require(path.isNotEmpty()) { "Enemy path cannot be empty." }
        // Set initial position (will be refined in movement logic)
        updatePosition()
        // Register with GameManager for updates
        gameManager.registerGameObject(this)
    }

    /**
     * Calculates the enemy's current world position based on its progress along the path segment.
     * Uses linear interpolation between the current and next path nodes.
     */
    protected fun updatePosition() {
        // Get the current tile size from GameManager
        val tileSize = gameManager.currentCellSize

        if (currentPathIndex >= path.size - 1) {
            // If at or beyond the last node, stay at the last node's position
            val endNode = path.last()
            position = CoordinateConverter.gridToWorld(endNode.first, endNode.second, tileSize)
            return
        }

        val startNodeCoords = path[currentPathIndex]
        val endNodeCoords = path[currentPathIndex + 1]

        val startPos = CoordinateConverter.gridToWorld(startNodeCoords.first, startNodeCoords.second, tileSize)
        val endPos = CoordinateConverter.gridToWorld(endNodeCoords.first, endNodeCoords.second, tileSize)

        // Linearly interpolate between the start and end points of the current segment
        position = lerp(startPos, endPos, progressAlongSegment)
    }

    /**
     * Update method called by the GameManager's game loop.
     * Handles movement and potentially other per-frame logic.
     * @param deltaTime Time elapsed since the last frame in seconds.
     */
    override fun update(deltaTime: Float) {
        updateStatusEffects(deltaTime)
        move(deltaTime)
    }

    /**
     * Updates the duration of active status effects.
     * @param deltaTime Time elapsed since the last frame in seconds.
     */
    protected open fun updateStatusEffects(deltaTime: Float) {
        if (isSlowed) {
            slowTimer -= deltaTime
            if (slowTimer <= 0f) {
                isSlowed = false
                slowFactor = 1.0f
                println("Slow effect wore off.")
            }
        }
        // Add other status effect updates here if needed
    }

    /**
     * Moves the enemy along the path based on its speed and delta time.
     * Handles transitioning between path segments.
     * @param deltaTime Time elapsed since the last frame in seconds.
     */
    protected open fun move(deltaTime: Float) {
        if (hasReachedEnd || isDead) return // Don't move if dead or already finished

        // Calculate distance to move this frame, considering slow effect
        val currentSpeed = speed * slowFactor // Apply slow factor
        val distanceToMove = currentSpeed * deltaTime

        // Calculate remaining distance in the current segment
        // Assuming grid tiles are uniform distance (e.g., 1 unit apart)
        val remainingDistanceInSegment = 1.0f - progressAlongSegment

        if (distanceToMove >= remainingDistanceInSegment) {
            // Move to the next node and potentially beyond
            var distanceLeft = distanceToMove - remainingDistanceInSegment
            currentPathIndex++
            progressAlongSegment = 0f // Reset progress for the new segment

            // Handle reaching the end node exactly
            if (currentPathIndex >= path.size - 1) {
                 progressAlongSegment = 1f // Snap to end
                 updatePosition()
                 onReachEnd()
                 return // Stop moving after reaching the end
            }

            // Consume remaining distance in subsequent segments if needed (handles high speed/low frame rate)
            while (distanceLeft > 0) {
                if (distanceLeft >= 1.0f) { // Can complete the next segment fully
                    distanceLeft -= 1.0f
                    currentPathIndex++
                    if (currentPathIndex >= path.size - 1) {
                        progressAlongSegment = 1f // Snap to end
                        updatePosition()
                        onReachEnd()
                        return // Stop moving after reaching the end
                    }
                } else { // Partially move into the next segment
                    progressAlongSegment = distanceLeft
                    distanceLeft = 0f // No distance left
                }
            }

        } else {
            // Stay in the current segment
            progressAlongSegment += distanceToMove
        }

        // Update the visual position based on new index/progress
        updatePosition()
    }

    /**
     * Applies a slow effect to the enemy.
     * @param duration How long the slow effect lasts in seconds.
     * @param factor The multiplier for speed (e.g., 0.5 for 50% slow).
     */
    open fun applySlow(duration: Float, factor: Float) {
        require(factor in 0.0..1.0) { "Slow factor must be between 0.0 and 1.0" }
        isSlowed = true
        slowTimer = max(slowTimer, duration) // Refresh or extend duration if already slowed
        slowFactor = factor // Apply the new factor
        println("Applied slow (factor: $factor) for $duration seconds.")
    }

    /**
     * Reduces enemy health by the given amount.
     * Triggers death sequence if health drops to 0 or below.
     * @param amount The amount of damage to inflict.
     */
    open fun takeDamage(amount: Float) {
        if (isDead) return // Already dead

        health -= amount
        Log.d("Enemy", "Took $amount damage, health remaining: $health")

        if (isDead) {
            onDeath()
        }
    }

    /**
     * Called when the enemy's health reaches 0 or below.
     * Handles cleanup and notifies the GameManager.
     * Can be overridden by subclasses for specific death behaviors (animations, effects).
     */
    protected open fun onDeath() {
        Log.d("Enemy", "Died!")
        // Notify GameManager about the death
        gameManager.enemyDestroyed(this) 

        // Unregister from game updates
        gameManager.unregisterGameObject(this)

        // TODO: Trigger death animations or effects here
    }

    /**
     * Called when the enemy reaches the final node of the path.
     * Handles cleanup and notifies the GameManager.
     */
    protected open fun onReachEnd() {
        Log.d("Enemy", "Reached end of path!")
        // Notify GameManager that an enemy reached the end
        gameManager.enemyReachedEnd(this)

        // Unregister from game updates
        gameManager.unregisterGameObject(this)

        // Mark as effectively dead or inactive to prevent further interactions
        health = 0f // Or add a separate 'isActive' flag
    }

    /**
     * Abstract method demonstrating polymorphism.
     * Each concrete enemy subclass must implement this to return its specific type.
     * @return An identifier for the enemy type (e.g., a String).
     */
    abstract fun getType(): String

    /**
     * Virtual method called when the enemy is hit by an attack (before damage calculation).
     * Can be overridden by subclasses for custom reactions (e.g., visual effects).
     */
    open fun onHit() {
        // Placeholder for hit effects
    }

    /**
     * Applies a pushback effect, moving the enemy backward along the path.
     * @param distance The distance (in path segments) to push the enemy back.
     */
    open fun applyPushBack(distance: Float) {
        if (isDead || hasReachedEnd) return // Cannot push back dead or finished enemies

        Log.d("Enemy", "Applying pushback of $distance segments.")
        
        var distanceToPush = distance
        while(distanceToPush > 0 && currentPathIndex >= 0) {
            if (distanceToPush <= progressAlongSegment) {
                // Push back within the current segment
                progressAlongSegment -= distanceToPush
                distanceToPush = 0f
            } else {
                // Push back past the start of the current segment
                distanceToPush -= progressAlongSegment
                currentPathIndex--
                if (currentPathIndex < 0) {
                    // Pushed back before the start of the path
                    progressAlongSegment = 0f
                    currentPathIndex = 0 // Keep at start
                    distanceToPush = 0f // Stop pushing
                } else {
                    progressAlongSegment = 1.0f // Start at the end of the previous segment
                }
            }
        }
        // Ensure progress doesn't go below 0 after adjustment
        progressAlongSegment = max(0f, progressAlongSegment)

        updatePosition() // Update visual position immediately
        Log.d("Enemy", "Pushed back to index $currentPathIndex, progress $progressAlongSegment")
    }
} 