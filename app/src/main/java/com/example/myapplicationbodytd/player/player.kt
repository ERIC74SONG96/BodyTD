package com.example.myapplicationbodytd.player

import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.towers.MacrophageTower
import com.example.myapplicationbodytd.towers.MucusTower
import com.example.myapplicationbodytd.towers.TouxTower
import com.example.myapplicationbodytd.towers.Tower
import com.example.myapplicationbodytd.ui.Map

/**
 * Classe représentant le joueur et gérant ses interactions
 */
class Player {
    private val gameManager = GameManager.getInstance()
    private val map = Map()
    private var selectedTowerType: TowerType? = null

    /**
     * Types de tours disponibles
     */
    enum class TowerType {
        TOUX,
        MUCUS,
        MACROPHAGE
    }

    /**
     * Sélectionne un type de tour à placer
     */
    fun selectTowerType(type: TowerType) {
        selectedTowerType = type
    }

    /**
     * Tente de placer une tour à l'emplacement indiqué
     */
    fun placeTower(x: Float, y: Float): Boolean {
        // Vérifier si un type de tour est sélectionné
        val towerType = selectedTowerType ?: return false

        // Vérifier si l'emplacement est valide
        if (!map.isValidTowerLocation(x, y)) {
            return false
        }

        // Créer la tour en fonction du type sélectionné
        val tower: Tower = when (towerType) {
            TowerType.TOUX -> TouxTower()
            TowerType.MUCUS -> MucusTower()
            TowerType.MACROPHAGE -> MacrophageTower()
        }

        // Essayer d'ajouter la tour (vérifie si le joueur a assez d'argent)
        if (gameManager.addTower(tower, x, y)) {
            selectedTowerType = null // Désélectionner après placement réussi
            return true
        }

        return false
    }

    /**
     * Améliore une tour si possible
     */
    fun upgradeTower(x: Float, y: Float): Boolean {
        // Trouver la tour la plus proche du point indiqué
        val tower = findTowerNearPoint(x, y)

        // Si une tour est trouvée et que le joueur a assez d'argent, améliorer la tour
        if (tower != null) {
            // Récupérer le coût d'amélioration
            val upgradeCost = when (tower) {
                is TouxTower -> tower.getUpgradeCost()
                is MucusTower -> tower.getUpgradeCost()
                is MacrophageTower -> tower.getUpgradeCost()
                else -> 0
            }

            // Vérifier si le joueur a assez d'argent
            if (gameManager.getMoney() >= upgradeCost) {
                // Déduire le coût et améliorer la tour
                // (Dans une implémentation complète, gameManager devrait gérer cela)
                tower.upgrade()
                return true
            }
        }

        return false
    }

    /**
     * Trouve une tour proche d'un point donné
     */
    private fun findTowerNearPoint(x: Float, y: Float): Tower? {
        val towers = gameManager.getTowers()
        val maxDistanceSquared = 40f * 40f // Distance maximale pour considérer une tour (rayon de la tour)

        return towers.minByOrNull { tower ->
            val dx = tower.position.x - x
            val dy = tower.position.y - y
            dx * dx + dy * dy
        }?.let { tower ->
            val dx = tower.position.x - x
            val dy = tower.position.y - y
            val distanceSquared = dx * dx + dy * dy

            if (distanceSquared <= maxDistanceSquared) tower else null
        }
    }
}