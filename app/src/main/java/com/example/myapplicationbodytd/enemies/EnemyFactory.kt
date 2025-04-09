package com.example.myapplicationbodytd.enemies

import FineParticle
import Virus

object EnemyFactory {

    fun createEnemy(type: String): Enemy {
        return when (type.lowercase()) {
            "virus" -> Virus()
            "bacteria" -> Bacteria()
            "fineparticle", "particulefine" -> FineParticle()
            else -> throw IllegalArgumentException("Unknown enemy type: $type")
        }
    }
}
