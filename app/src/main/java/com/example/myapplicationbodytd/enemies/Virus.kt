package com.example.myapplicationbodytd.enemies

/**
 * Virus enemy type - fast but low health
 */
class Virus(
    health: Int = 50,
    speed: Float = 3.0f,
    damage: Int = 5,
    reward: Int = 10
) : Enemy(health, speed, damage, reward) {
    override fun attack() {
        println("Virus attacks quickly!")
    }

    override fun getMaxHealth(): Int = 50

    fun moveFast() {
        println("Virus se déplace à vitesse élevée.")
    }
}
