package com.example.myapplicationbodytd.game.states

import com.example.myapplicationbodytd.managers.GameManager

/**
 * Abstract base class for all game states (State Pattern).
 * Defines the common interface for game state behaviors.
 */
abstract class GameState(protected val gameManager: GameManager) {

    /**
     * Called when entering this state.
     */
    abstract fun enter()

    /**
     * Called every game update cycle while this state is active.
     * @param deltaTime Time elapsed since the last update in seconds.
     */
    abstract fun update(deltaTime: Float)

    /**
     * Called when exiting this state.
     */
    abstract fun exit()
} 