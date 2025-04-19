# Project TODO List

This list tracks items marked with `// TODO:` in the codebase.

- [ ] Use a centralized grid-to-world conversion utility (`Tower.kt`, line 39)
- [ ] Convert grid coords to world coords properly (`Enemy.kt`, line 81)
- [ ] Convert grid coords to world coords properly (`Enemy.kt`, line 88)
- [ ] Trigger death animations or effects here (`Enemy.kt`, line 221)
- [ ] Consider loading map layouts from files later (`Map.kt`, line 230)
- [ ] Avoid recalculating canvas/tile size within tap gesture? Pass as state or remember? (`GameCanvas.kt`, line 63)
- [ ] Use a proper world coordinate system from Enemy or Map (`GameCanvas.kt`, line 150)
- [ ] Use the `cost` property of the `Tower` class (e.g., for UI display, purchase logic).
- [ ] Consider thread safety if `registerGameObject` is called from multiple threads (`GameManager.kt`, line 92)
- [ ] Consider thread safety if `unregisterGameObject` is called from multiple threads (`GameManager.kt`, line 102)
- [ ] Tell WaveManager to start spawning for currentWave (`GameManager.kt`, line 224)
- [ ] Add checks for existing towers at (x, y) in `GameManager.canPlaceTowerAt` (`GameManager.kt`, line 230)
- [ ] Define `GRID_SIZE` properly, maybe in a constants file (`GameScreen.kt`, line 34)
- [ ] Improve placement indicator - maybe draw a semi-transparent tower? (`GameScreen.kt`, line 108)
- [ ] Verify how enemy.position relates to world/canvas coordinates (`GameScreen.kt`, line 187)
- [ ] Differentiate enemy types visually in `GameScreen.drawEnemies` (`GameScreen.kt`, line 194) 