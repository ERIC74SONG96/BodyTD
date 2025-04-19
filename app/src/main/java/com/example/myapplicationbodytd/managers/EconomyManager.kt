package com.example.myapplicationbodytd.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import com.example.myapplicationbodytd.util.Constants

/**
 * Manages the player's currency (economy).
 * Uses Singleton pattern (Kotlin object).
 */
object EconomyManager {

    const val INITIAL_CURRENCY = Constants.STARTING_CURRENCY

    // Use StateFlow for reactive UI updates
    private val _currency = MutableStateFlow(INITIAL_CURRENCY)
    val currency = _currency.asStateFlow() // Expose as immutable StateFlow

    init {
        Log.d("EconomyManager", "Initialized with starting currency: ${currency.value}")
    }

    /**
     * Gets the current currency amount.
     */
    fun getCurrentCurrency(): Int {
        return currency.value
    }

    /**
     * Adds the specified amount to the player's currency.
     * Does nothing if the amount is negative.
     * @param amount The amount of currency to add.
     */
    fun addCurrency(amount: Int) {
        if (amount < 0) {
            Log.w("EconomyManager", "Attempted to add negative currency: $amount")
            return
        }
        _currency.value += amount
        Log.d("EconomyManager", "Added $amount currency. New balance: ${currency.value}")
    }

    /**
     * Attempts to spend the specified amount of currency.
     * Succeeds only if the player has sufficient funds.
     * @param amount The amount to spend (must be non-negative).
     * @return True if the currency was successfully spent, false otherwise.
     */
    fun spendCurrency(amount: Int): Boolean {
        if (amount < 0) {
            Log.w("EconomyManager", "Attempted to spend negative currency: $amount")
            return false
        }
        if (currency.value >= amount) {
            _currency.value -= amount
            Log.d("EconomyManager", "Spent $amount currency. New balance: ${currency.value}")
            return true
        } else {
            Log.d("EconomyManager", "Failed to spend $amount currency. Current balance: ${currency.value}")
            return false
        }
    }

    /**
     * Resets the currency to the starting amount.
     */
    fun reset() {
        Log.d("EconomyManager", "Resetting currency to $INITIAL_CURRENCY")
        _currency.value = INITIAL_CURRENCY
    }
} 