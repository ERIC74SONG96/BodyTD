package com.example.myapplicationbodytd.game.entities

import androidx.compose.ui.geometry.Offset
import com.example.myapplicationbodytd.game.mechanics.strategies.AttackStrategy
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.managers.Updatable
import com.example.myapplicationbodytd.util.CoordinateConverter
import kotlin.math.pow
import kotlin.math.sqrt
import android.util.Log
import androidx.compose.runtime.Stable
import com.example.myapplicationbodytd.util.Constants

/**
 * **Inheritance:** Abstract base class for all defensive towers.
 * Defines common properties (cost, range, attackRate, position, damage) and core logic
 * (`update`, `tryAttack`, `findTarget`, `isInRange`).
 *
 * **Encapsulation:** Key properties like `currentTarget` have `protected set`.
 *
 * **Polymorphism:** Subclasses (`MucusTower`, etc.) inherit and can potentially override
 * methods like `findTarget` or `attack` for specialized behavior, although the primary
 * variation is intended via the Strategy pattern.
 *
 * **Composition & Strategy Pattern:** Holds a reference to an `AttackStrategy` object.
 * Delegates the specific attack execution logic (`attackStrategy.execute()`) to this
 * strategy object, allowing attack behavior to be changed independently.
 */
@Stable
abstract class Tower(
    val range: Float, // Range in terms of world units (e.g., pixels or dp)
    val attackRate: Float, // Attacks per second
    var attackStrategy: AttackStrategy,
    val position: Pair<Int, Int>, // Grid position (x, y)
    protected val gameManager: GameManager, // Reference for accessing game state (e.g., enemy list)
    val baseDamage: Float = 10f // Add base damage property
) : Updatable { 

    protected var cooldownTimer: Float = 0f // Time remaining until next attack
    protected val attackCooldown: Float = 1.0f / attackRate

    // Convert grid position to world position using the centralized converter
    // Pass the current cell size from GameManager
    val worldPosition: Offset = CoordinateConverter.gridToWorld(
        position.first, 
        position.second, 
        gameManager.currentCellSize
    )

    var currentTarget: Enemy? = null
        protected set
    var attackEffectTimer: Float = 0f
    private val attackEffectDuration: Float = Constants.ATTACK_EFFECT_DURATION // Use constant

    init {
        gameManager.registerGameObject(this) // Register for updates
    }

    /**
     * Update method called by GameManager.
     * Handles cooldown reduction and attack execution logic.
     * @param deltaTime Time elapsed since the last update.
     */
    override fun update(deltaTime: Float) {
        // Update attack effect timer
        if (attackEffectTimer > 0f) {
            attackEffectTimer -= deltaTime
            if (attackEffectTimer <= 0f) {
                currentTarget = null // Clear target when effect display time is over
            }
        }

        // Update attack cooldown
        cooldownTimer -= deltaTime
        if (cooldownTimer <= 0f) {
            tryAttack()
        }
    }

    /**
     * Attempts to find a target and attack if cooldown is ready.
     * Optimizes by checking the current target first before searching all enemies.
     */
    protected open fun tryAttack() {
        // 1. Validate existing target
        val targetStillValid = currentTarget?.let { !it.isDead && isInRange(it) } == true

        if (targetStillValid) {
            // If current target is still valid, attack it
            attack(currentTarget!!) // Safe non-null call due to check
            cooldownTimer = attackCooldown // Reset cooldown
        } else {
            // 2. Current target is invalid (null, dead, or out of range), find a new one
            currentTarget = null // Explicitly clear invalid target
            // Access the list directly from GameManager (it's a SnapshotStateList)
            val newTarget = findTarget(gameManager.activeEnemies)
            if (newTarget != null) {
                // Found a new target, attack it
                attack(newTarget)
                cooldownTimer = attackCooldown // Reset cooldown
            } else {
                // No target found in range
                // Cooldown will continue ticking down in the next update
            }
        }
    }

    /**
     * Finds a target enemy within range based on a specific strategy.
     * NOTE: This is potentially expensive and should only be called when necessary.
     * Default: Find the first enemy that entered the range (approximated by path progress).
     * @param enemies List of potential target enemies.
     * @return The targeted Enemy, or null if no valid target is in range.
     */
    protected open fun findTarget(enemies: List<Enemy>): Enemy? { 
        // Optimized slightly by using sequence for potentially lazy evaluation if needed later
        return enemies.asSequence()
            .filter { !it.isDead && isInRange(it) }
            .maxByOrNull { it.currentPathIndex + it.progressAlongSegment }
    }

    /**
     * Executes the attack on the chosen target using the assigned strategy.
     * @param targetEnemy The enemy to attack.
     */
    protected open fun attack(targetEnemy: Enemy) {
        Log.d("Tower", "Attacking ${targetEnemy.getType()} at ${targetEnemy.position}")
        currentTarget = targetEnemy // Set current target for visual effect
        attackEffectTimer = attackEffectDuration // Start timer for visual effect
        attackStrategy.execute(targetEnemy, this)
    }

    /**
     * Checks if a given enemy is within the tower's attack range.
     * @param enemy The enemy to check.
     * @return True if the enemy is in range, false otherwise.
     */
    protected open fun isInRange(enemy: Enemy): Boolean {
        // we could also have used bitmap collision for faster (although static) in-range detection
        return calculateDistanceSquared(enemy) <= range.pow(2)
    }

    /**
     * Calculates the squared distance between the tower and an enemy.
     * Used for efficient range checking (avoids sqrt).
     * @param enemy The enemy to measure distance to.
     * @return The squared distance.
     */
    protected fun calculateDistanceSquared(enemy: Enemy): Float {
        val dx = enemy.position.x - worldPosition.x
        val dy = enemy.position.y - worldPosition.y
        return dx * dx + dy * dy
    }

} 