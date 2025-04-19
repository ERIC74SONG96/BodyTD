# TODO Analysis and Action Plan

This document analyzes the `// TODO:` comments found in `TODO copy.md` and `TODO.md`, cross-references them with `PROJECT_WARNINGS.md`, and provides a status update and action plan.

## Completed / Obsolete TODOs (Can be Removed)

*   **`Enemy.kt:290`: Implement path completion logic**
    *   **Status:** Implemented in `Enemy.move` and `Enemy.onReachEnd`.
*   **`Enemy.kt:81`, `Enemy.kt:88`: Convert grid coords to world coords properly**
    *   **Status:** Implemented via `CoordinateConverter.gridToWorld` in `Enemy.updatePosition`.
*   **`EconomyManager.kt:66`: Add methods for earning and spending currency**
    *   **Status:** Implemented (`addCurrency`, `spendCurrency`).
*   **`GameViewModel.kt:124`: Update GameManager interface/methods if needed for canPlaceTowerAt**
    *   **Status:** Implemented. `GameManager.canPlaceTowerAt` checks map and existing towers. `GameViewModel` calls it correctly.
*   **`GameViewModel.kt:171`: Add functions to update currency from enemy defeats (called by GameManager)**
    *   **Status:** Implemented. `GameManager.enemyDestroyed` handles currency updates via `EconomyManager`. `ViewModel` observes the result.
*   **`Tower.kt:39`: Use centralized grid-to-world conversion**
    *   **Status:** Implemented using `CoordinateConverter.gridToWorld`.
*   **`GameManager.kt:92`, `GameManager.kt:102`: Consider thread safety if register/unregisterGameObject**
    *   **Status:** Implemented using `ReentrantLock` (`gameObjectsLock`) and `SnapshotStateList`.
*   **`GameManager.kt:230`: Add checks for existing towers at (x, y) in `canPlaceTowerAt`**
    *   **Status:** Implemented in `GameManager.canPlaceTowerAt`.
*   **`GameScreen.kt:187`: Verify how enemy.position relates to world/canvas coordinates**
    *   **Status:** Verified. `Enemy.position` is world coords, used correctly in `GameScreen` drawing functions.
*   **`GameCanvas.kt:63`, `GameCanvas.kt:150`: Coordinate/Canvas size calculations**
    *   **Status:** Handled/Obsolete. Logic moved into `GameScreen.kt`'s `Canvas` composable. `GameCanvas.kt` is likely unused/refactored. (Action: Verify and delete `GameCanvas.kt` if appropriate).

## Pending TODOs (Need Implementation / Decision)

*   **`Enemy.kt:220`/`Enemy.kt:221`: Trigger death animations or effects here**
    *   **Status:** Pending. `onDeath` only logs and notifies `GameManager`.
    *   **Action:** Implement visual/audio effects.
*   **`Map.kt:173`/`Map.kt:230`: Consider loading map layouts from files later**
    *   **Status:** Pending. Map is hardcoded. Future enhancement.
    *   **Action:** Keep TODO for future implementation.
*   **`InitializingState.kt:13-15`: Reset WaveManager state, Clear existing enemies, Load map**
    *   **Status:** Pending. Needed for proper game restarts. Map loading seems okay, but clearing objects and resetting managers is required when re-entering this state.
    *   **Action:** Implement state clearing and manager resets in `InitializingState.enter` or a dedicated `gameManager.reset()` method called by the state.
*   **`LostState.kt:10-12`: Display game over UI, Stop game loop, Provide options**
    *   **Status:** Pending. `enter` method only logs. Stopping loop relates to unused `GameManager.stopGameLoop` warning.
    *   **Action:** Implement UI display, call `gameManager.stopGameLoop()`, add restart/quit options.
*   **`LostState.kt:21-22`: Hide game over UI, Reset game**
    *   **Status:** Pending. `exit` method only logs.
    *   **Action:** Implement UI hiding and game reset logic (potentially calling `gameManager.changeState(InitializingState(gameManager))`).
*   **`PlayingState.kt:17`, `PlayingState.kt:52`: Enable/Disable player interactions**
    *   **Status:** Pending/Review needed. Interactions seem globally managed via `ViewModel`'s placement mode, not tied to this state explicitly.
    *   **Action:** Decide if state-based interaction control is needed. If yes, implement; if not, remove TODOs.
*   **`WaveClearedState.kt:14`: Update UI to show "Wave Cleared!..."**
    *   **Status:** Pending. `enter` method only logs.
    *   **Action:** Implement UI update (e.g., via `ViewModel` state).
*   **`WaveClearedState.kt:24`: Hide any "Wave Cleared" UI elements**
    *   **Status:** Pending. `exit` method only logs.
    *   **Action:** Implement UI hiding.
*   **`WonState.kt:10-12`: Display victory UI, Stop game loop, Provide options**
    *   **Status:** Pending. `enter` method only logs. Stopping loop relates to unused `GameManager.stopGameLoop` warning.
    *   **Action:** Implement UI display, call `gameManager.stopGameLoop()`, add restart/quit options.
*   **`WonState.kt:21-22`: Hide victory UI, Reset game**
    *   **Status:** Pending. `exit` method only logs.
    *   **Action:** Implement UI hiding and game reset logic.
*   **`GameScreen.kt:45`/`GameScreen.kt:34`: Define GRID_SIZE properly, maybe in a constants file**
    *   **Status:** Partially Done. `GRID_SIZE` is a `const val` in `GameScreen`.
    *   **Action:** Decide if moving to `Constants.kt` is preferred. Address unused warning if applicable. Remove TODO.
*   **`GameScreen.kt:443`: Add cost display?**
    *   **Status:** Partially Done. Cost *is* displayed in `TowerSelectionPanel`, but uses hardcoded values. See next TODO.
    *   **Action:** Refine or remove this TODO; covered by `TowerSelectionPanel.kt:67`.
*   **`TowerSelectionPanel.kt:67`: Get cost from centralized source (Task 5)**
    *   **Status:** Pending. Uses local hardcoded helper function. Relates to unused `Tower` cost parameter and potentially unused `GameScreen.getTowerCost`.
    *   **Action:** Implement fetching cost from `Tower` definitions or `ViewModel`. Remove helper function.
*   **`GameViewModel.kt:129`: Use a TowerFactory or more robust creation mechanism**
    *   **Status:** Pending. Uses a simple `when` statement in a helper function. Relates to unused `EnemyFactory` warning.
    *   **Action:** Implement `TowerFactory` and use it in `GameViewModel`.
*   **`Tower.kt`: Use the `cost` property**
    *   **Status:** Pending. `cost` parameter in constructor is unused within the class.
    *   **Action:** Decide if `cost` is needed *inside* `Tower`. If yes, use it. If no, remove from constructor and rely on external sources (ViewModel/Factory) for cost info. Address the related warning.
*   **`GameScreen.kt:108`: Improve placement indicator...**
    *   **Status:** Partially Done. Draws colored squares. Suggestion was semi-transparent tower.
    *   **Action:** Decide if current indicator is sufficient. If not, implement suggested improvement. Keep or remove TODO accordingly.

## New TODOs from Project Warnings

*   **Cleanup Unused Imports:** Many files listed with unused imports.
    *   **Action:** Use IDE's "Optimize Imports" feature across the project.
*   **Implement/Remove Empty Methods:**
    *   `Enemy.kt`: `setCurrentPathIndex`, `setProgressAlongSegment`, `onHit`
    *   `Tower.kt`: `setCurrentTarget`
    *   **Action:** Implement necessary logic or remove these methods if obsolete.
*   **Implement/Remove Unused Declarations:**
    *   `Constants.kt`: `ATTACK_EFFECT_DURATION`, `WAVE_COOLDOWN` (Are these needed?)
    *   `CoordinateConverter.kt`: `DEFAULT_TILE_SIZE`, `distanceSquared`, `gridToWorldSize`, `gridToWorldTopLeft` (Consolidate or remove?)
    *   `EnemyFactory.kt`: `createBacteria`, `createFineParticle`, `createVirus` (Implement Factory usage or remove if not using pattern).
    *   `GameManager.kt`: `cleanup()`, `MAX_LIVES_LOST` (Is max lives handled elsewhere?), `stopGameLoop()` (Needed for Win/Loss states), `TIME_STEP_NANOS` (Obsolete from fixed timestep?).
    *   `GameScreen.kt`: `getTowerCost` (Likely obsolete if ViewModel handles cost).
    *   `Map.kt`: `getPathEndPoint()`, `getPathStartPoint()` (Needed for layout loading?).
    *   `Tower.kt`: `cost` parameter (See pending TODO above).
    *   `WaveManager.kt`: `getCurrentWaveNumber()`, `getCurrentWaveStatus()`, `gameManager` param in `startWave`, `path` param in `startWave` (Are these needed? State seems handled by GameManager flows).
    *   **Action:** Review each unused item. Implement if needed, remove if obsolete. This addresses several related TODOs implicitly (cost, coordinate conversion).
*   **Remove Redundant Qualifiers:**
    *   `GameScreen.kt`: Lines 450-452 in `getTowerCost`.
    *   **Action:** Remove redundant qualifiers (e.g., `TowerType.MUCUS` -> `MUCUS` if imported correctly).
*   **Review Module Dependencies:**
    *   `androidTest` and `unitTest` modules don't depend on `main`.
    *   **Action:** Verify if this is correct or if dependencies need to be added/removed in Gradle files.

This analysis provides a clearer picture of the remaining work and technical debt. Addressing the warnings alongside the pending TODOs will improve code quality and maintainability. 