package com.example.myapplicationbodytd.enemies

/**
 * FineParticle enemy type - low health and damage but can be numerous
 */
class FineParticle(
    health: Int = 30,
    speed: Float = 1.0f,
    damage: Int = 2,
    reward: Int = 5
) : Enemy(health, speed, damage, reward) {
    
    override fun attack() {
        println("Fine particle attacks weakly.")
    }
    
    override fun getMaxHealth(): Int = 30
}
