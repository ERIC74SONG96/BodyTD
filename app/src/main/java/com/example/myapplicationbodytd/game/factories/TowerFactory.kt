package com.example.myapplicationbodytd.game.factories

import com.example.myapplicationbodytd.game.entities.*
import com.example.myapplicationbodytd.managers.GameManager
import com.example.myapplicationbodytd.ui.TowerType
import android.util.Log

/**
 * Factory object for creating Tower instances.
 * Centralizes tower creation logic.
 */
object TowerFactory {

    /**
     * Creates a specific tower instance based on the TowerType.
     *
     * @param type The type of tower to create.
     * @param position The grid position (Pair<Int, Int>) where the tower will be placed.
     * @param gameManager Reference to the GameManager.
     * @return The created Tower instance, or null if the type is unknown.
     */
    fun createTower(type: TowerType, position: Pair<Int, Int>, gameManager: GameManager): Tower? {
        Log.d("TowerFactory", "Creating tower of type $type at $position")
        return when (type) {
            TowerType.MUCUS -> MucusTower(position, gameManager)
            TowerType.MACROPHAGE -> MacrophageTower(position, gameManager)
            TowerType.COUGH -> CoughTower(position, gameManager)
            // Add cases for other tower types here
            // else -> {
            //     Log.e("TowerFactory", "Unknown tower type requested: $type")
            //     null
            // }
        }
    }
} 