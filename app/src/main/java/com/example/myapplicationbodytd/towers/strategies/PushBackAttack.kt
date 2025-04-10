//package com.example.myapplicationbodytd.towers.strategies
//
//import android.graphics.PointF
//import com.example.myapplicationbodytd.enemies.Enemy
//
///**
// * Stratégie d'attaque: cible plusieurs ennemis et les repousse
// */
//class PushBackAttack : AttackStrategy {
//    private val maxTargets = 3 // Nombre maximum d'ennemis ciblés simultanément
//
//    override fun selectTargets(targets: List<Enemy>, towerPosition: PointF): List<Enemy> {
//        if (targets.isEmpty()) return emptyList()
//
//        // Trier les ennemis par distance
//        val sortedTargets = targets.sortedBy { enemy ->
//            val dx = enemy.position.x - towerPosition.x
//            val dy = enemy.position.y - towerPosition.y
//            dx * dx + dy * dy
//        }
//
//        // Prendre les ennemis les plus proches (jusqu'au maximum)
//        return sortedTargets.take(maxTargets)
//    }
//
//    /**
//     * Applique l'effet de poussée aux ennemis (serait appelé par la tour)
//     */
//    fun pushEnemies(enemies: List<Enemy>, towerPosition: PointF, pushStrength: Float) {
//        for (enemy in enemies) {
//            // Calculer la direction de poussée (opposée à la tour)
//            val dx = enemy.position.x - towerPosition.x
//            val dy = enemy.position.y - towerPosition.y
//            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
//
//            if (distance > 0) {
//                // Normaliser et appliquer la force de poussée
//                val pushX = dx / distance * pushStrength
//                val pushY = dy / distance * pushStrength
//
//                // Ici, nous devrions avoir une méthode pour pousser l'ennemi
//                // Par exemple: enemy.push(pushX, pushY)
//                // Pour l'exemple, nous allons simplement déplacer sa position
//                enemy.position.x += pushX
//                enemy.position.y += pushY
//            }
//        }
//    }
//}