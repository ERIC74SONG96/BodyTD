package com.example.myapplicationbodytd.enemies

/**
 * Enum representing the different types of enemies in the game.
 * Each type has associated properties like health, speed, damage, and reward.
 */
enum class EnemyType {
    VIRUS,
    BACTERIA,
    FINE_PARTICLE;
    
    /**
     * Gets the base health for this enemy type
     */
    fun getBaseHealth(): Int = when (this) {
        VIRUS -> 50
        BACTERIA -> 100
        FINE_PARTICLE -> 30
    }
    
    /**
     * Gets the base speed for this enemy type
     */
    fun getBaseSpeed(): Float = when (this) {
        VIRUS -> 3.0f
        BACTERIA -> 1.0f
        FINE_PARTICLE -> 1.0f
    }
    
    /**
     * Gets the base damage for this enemy type
     */
    fun getBaseDamage(): Int = when (this) {
        VIRUS -> 5
        BACTERIA -> 10
        FINE_PARTICLE -> 2
    }
    
    /**
     * Gets the base reward for this enemy type
     */
    fun getBaseReward(): Int = when (this) {
        VIRUS -> 10
        BACTERIA -> 20
        FINE_PARTICLE -> 5
    }
    
    /**
     * Gets the resource ID for this enemy type's sprite
     */
    fun getSpriteResourceId(): Int = when (this) {
        VIRUS -> com.example.myapplicationbodytd.R.drawable.virus
        BACTERIA -> com.example.myapplicationbodytd.R.drawable.bacteria
        FINE_PARTICLE -> com.example.myapplicationbodytd.R.drawable.fine_particle
    }
} 