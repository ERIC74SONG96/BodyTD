package com.example.myapplicationbodytd.enemies//Commentaire

/**
 * Bacteria enemy type - high health and damage but slow
 */
class Bacteria(
    health: Int = 100,
    speed: Float = 1.0f,
    damage: Int = 10,
    reward: Int = 20
) : Enemy(health, speed, damage, reward) {
    override fun attack() {
        println("Bacteria inflicts heavy damage!")
    }

    override fun getMaxHealth(): Int = 100
}
