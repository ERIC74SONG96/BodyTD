# Enemy Spawning Debugging Checklist

This file tracks potential reasons why enemies are not appearing.

## Potential Issues Checklist:

- [x] **UI Trigger:** Is the UI (`GameScreen.kt`) correctly calling `GameManager.startNextWave()` when expected (e.g., on button press)? - _Initially missing, added Start Wave button._
- [x] **Game State Transitions:**
    - [x] Does `InitializingState` correctly transition to a state where waves can begin? - _Yes, transitions to `WaveStartingState`._
    - [x] How does the game transition *into* `PlayingState`? - _`WaveStartingState` calls `startNextWave` and then transitions to `PlayingState` after a delay._
    - [x] Does the state transition correctly trigger `WaveManager.startWave` *within* the `GameManager.startNextWave` function? - _Yes, checked in `GameManager.kt`._
- [x] **WaveManager Logic:**
    - [x] Does `WaveManager.startWave` correctly set the status to `IN_PROGRESS`? - _Yes, code confirms._
    - [x] Is `timeUntilNextSpawn` being set correctly? - _Yes, uses `firstOrNull()?.delaySeconds`._
- [x] **Enemy Creation (`EnemyFactory.kt`):**
    - [x] Is `EnemyFactory.createEnemy` actually being called by `WaveManager.update`? - _Presumed yes, as `PlayingState.update` calls `WaveManager.update`._
    - [x] Does `EnemyFactory.createEnemy` successfully instantiate an `Enemy` object? - _Yes._
    - [x] Does `EnemyFactory.createEnemy` call `gameManager.registerGameObject()`? - _Initially NO, fixed._
- [x] **GameManager Registration (`GameManager.kt`):**
    - [x] Is `gameManager.registerGameObject()` successfully adding the enemy to the internal `gameObjects` list? - _Yes, code confirms (uses lock)._
    - [x] Is `gameManager.registerGameObject()` updating the `_activeEnemies` `StateFlow`? - _Yes, code confirms._
- [x] **UI Observation (`GameScreen.kt`):**
    - [x] Is the UI correctly collecting/observing the `GameManager.activeEnemies` `StateFlow`? - _Yes, via `GameViewModel`._
    - [x] Is the drawing logic (`GameCanvas.kt`?) correctly iterating over the observed enemies and rendering them? - _Yes, `drawEnemies` exists and iterates._

## Summary of Fixes Applied:

1.  **`PlayingState.kt`:** Ensured `WaveManager.update` is called with the correct map path.
2.  **`EnemyFactory.kt`:** Modified `createEnemy` to call `gameManager.registerGameObject()` after creating an enemy instance.
3.  **`GameScreen.kt`:** Added a "Start Wave" button to the `HUD` and connected its `onClick` to `gameViewModel.startNextWave()`.

## Remaining Potential Issues / Next Steps:

- **Coordinate System:** The `drawEnemies` function has a TODO regarding coordinate conversion (`enemy.position` vs. canvas coordinates). If enemies are spawning but are off-screen, this is the next place to look.
- **Wave Definitions:** Double-check `WaveManager.initializeWaveDefinitions` to ensure waves are defined correctly with non-zero enemies and reasonable delays.
- **Game Logic:** Could an enemy be created and immediately destroyed or removed due to some other game logic bug before it's rendered?

## Findings:

*(Will be updated as we investigate)* 