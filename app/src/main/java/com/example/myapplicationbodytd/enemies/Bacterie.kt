package com.example.myapplicationbodytd.enemies//Commentaire
class Bacteria(
    health: Int = 100,
    speed: Float = 1.0f,
    damage: Int = 10,
    reward: Int = 20
) : Enemy(health, speed, damage, reward) {
    override fun attack() {
        println("Bactérie inflige beaucoup de dégâts !")
    }
}
