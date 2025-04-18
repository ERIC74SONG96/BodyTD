package com.example.myapplicationbodytd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationbodytd.managers.GameManager
import java.lang.IllegalArgumentException

/**
 * Factory for creating instances of GameViewModel with dependencies.
 * This is necessary because GameViewModel requires a GameManager in its constructor.
 */
class GameViewModelFactory(private val gameManager: GameManager) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given `ViewModel` class.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is GameViewModel
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            // If it is, create and return an instance, passing the gameManager
            @Suppress("UNCHECKED_CAST") // Suppress warning as check is done above
            return GameViewModel(gameManager) as T
        }
        // If it's not GameViewModel, throw an exception as this factory only knows how to create GameViewModel
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}. This factory only creates GameViewModel.")
    }
} 