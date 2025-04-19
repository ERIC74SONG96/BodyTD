# Project Warnings (from index.html)

## Empty Methods

*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Enemy.kt`
    *   `line 39`: Method `setCurrentPathIndex` is empty
    *   `line 41`: Method `setProgressAlongSegment` is empty
    *   `line 250`: Method `onHit` is empty
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Tower.kt`
    *   `line 51`: Method `setCurrentTarget` is empty

## Unused Declarations

*   `app/src/main/java/com/example/myapplicationbodytd/util/Constants.kt`
    *   Field `ATTACK_EFFECT_DURATION` is never used.
    *   Field `WAVE_COOLDOWN` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/util/CoordinateConverter.kt`
    *   Field `DEFAULT_TILE_SIZE` is never used.
    *   Method `distanceSquared(long, long)` is never used.
    *   Method `gridToWorldSize(float, float)` is never used.
    *   Method `gridToWorldTopLeft(int, int, float, float, float)` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/game/factories/EnemyFactory.kt`
    *   Method `createBacteria(List<Pair<Integer, Integer>>, GameManager)` is never used.
    *   Method `createFineParticle(List<Pair<Integer, Integer>>, GameManager)` is never used.
    *   Method `createVirus(List<Pair<Integer, Integer>>, GameManager)` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/managers/GameManager.kt`
    *   Method `cleanup()` is never used.
    *   Field `MAX_LIVES_LOST` is never used.
    *   Method `stopGameLoop()` is never used.
    *   Field `TIME_STEP_NANOS` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/ui/GameScreen.kt`
    *   Method `getTowerCost(TowerType)` is never used.
    *   Field `GRID_SIZE` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/game/map/Map.kt`
    *   Method `getPathEndPoint()` is never used.
    *   Method `getPathStartPoint()` is never used.
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Tower.kt`
    *   Parameter `cost` in constructor `Tower(...)` is not used.
*   `app/src/main/java/com/example/myapplicationbodytd/managers/WaveManager.kt`
    *   Method `getCurrentWaveNumber()` is never used.
    *   Method `getCurrentWaveStatus()` is never used.
    *   Parameter `gameManager` in method `startWave(...)` is not used.
    *   Parameter `path` in method `startWave(...)` is not used.

## Unnecessary Module Dependencies

*   Module `My_Application_BodyTD.app.androidTest` sources do not depend on module `My_Application_BodyTD.app.main` sources.
*   Module `My_Application_BodyTD.app.unitTest` sources do not depend on module `My_Application_BodyTD.app.main` sources.

## Redundant Qualifier Name

*   `app/src/main/java/com/example/myapplicationbodytd/ui/GameScreen.kt`
    *   `line 450`: Remove redundant qualifier name in `getTowerCost`.
    *   `line 451`: Remove redundant qualifier name in `getTowerCost`.
    *   `line 452`: Remove redundant qualifier name in `getTowerCost`.

## Unused Import Directives

*   `app/src/main/java/com/example/myapplicationbodytd/util/CoordinateConverter.kt`
    *   `line 4`: Unused import `androidx.compose.ui.geometry.Offset` (or similar, exact import path clipped in source).
*   `app/src/main/java/com/example/myapplicationbodytd/managers/GameManager.kt`
    *   `line 15`: Unused import directive (e.g., `com.example.myapplicationbodytd.game.states.*`).
    *   `line 17`: Unused import directive.
    *   `line 18`: Unused import directive.
    *   `line 20`: Unused import directive.
*   `app/src/main/java/com/example/myapplicationbodytd/managers/WaveManager.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/ui/GameScreen.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/ui/GameView.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/viewmodels/GameViewModel.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/MainActivity.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/states/InitializingState.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/states/LostState.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/states/PlayingState.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/states/WaveClearedState.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/states/WonState.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/ui/TowerSelectionPanel.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Bacteria.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/CoughTower.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Enemy.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/FineParticle.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/MacrophageTower.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/MucusTower.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Tower.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/entities/Virus.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/map/Map.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/managers/EconomyManager.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/mechanics/SingleTargetAttackStrategy.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/mechanics/SlowAttackStrategy.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).
*   `app/src/main/java/com/example/myapplicationbodytd/game/mechanics/SplashAttackStrategy.kt`
    *   Multiple unused import directives (lines not specified in the summary, check file directly).

## Miscellaneous

*   _(Some unused import details were truncated in the HTML report. You might need to check the specific files manually or re-run the inspection in Android Studio for full details)_ 