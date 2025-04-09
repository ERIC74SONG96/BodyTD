package com.example.myapplicationbodytd.enemies
abstract class Enemy(
    var health: Int,
    val speed: Float,
    val damage: Int,
    val reward: Int,
    var position: Int = 0 // par exemple position sur le chemin
) {
    abstract fun attack()
    open fun move() {
        position += speed.toInt()
    }

    fun takeDamage(amount: Int) {
        health -= amount
    }

    fun isDead(): Boolean {
        return health <= 0
    }
}


