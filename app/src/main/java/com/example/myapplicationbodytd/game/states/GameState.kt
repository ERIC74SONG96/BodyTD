package com.example.myapplicationbodytd.game.states

import com.example.myapplicationbodytd.managers.GameManager

/**
 * **State Pattern:** Abstract base class defining the common interface for all game states.
 * Each concrete state (`InitializingState`, `PlayingState`, `WonState`, `LostState`, etc.)
 * encapsulates behavior specific to a particular phase of the game.
 * `GameManager` holds the current state object and delegates behavior (like `update`) to it.
 * The `enter()` and `exit()` methods allow states to perform setup and cleanup upon transition.
 */
abstract class GameState(protected val gameManager: GameManager) {

    /**
     * Defines actions to perform when transitioning *into* this state.
     */
    abstract fun enter()

    /**
     * Defines the behavior of this state during the game loop's update cycle.
     * @param deltaTime Time elapsed since the last update in seconds.
     */
    abstract fun update(deltaTime: Float)

    /**
     * Defines actions to perform when transitioning *out of* this state.
     */
    abstract fun exit()
} 