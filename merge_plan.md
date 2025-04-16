# Merge Plan for BodyTD Project

## Overview

This document outlines the plan to merge the implementations from:

- `com.bodytd.core` (core implementation)
- `com.example.myapplicationbodytd` (example implementation)

## Priority Order and File Mapping

### 1. Core Interfaces and Models (Highest Priority)

These define the contract for the entire application.

- Models:
  - `com.bodytd.core.models/*` → Base models
  - `com.example.myapplicationbodytd.entities/*` → Merge into core models
- Interfaces:
  - `com.bodytd.core.interfaces/*` → Keep as source of truth
  - Review and incorporate any additional interfaces from example implementation

### 2. Game Management Layer (High Priority)

Handles core game logic and state management.

- Game Manager:

  - `com.bodytd.core.managers/GameManagerImpl.kt` (primary)
  - `com.example.myapplicationbodytd.managers/GameManager.kt` (merge improvements)

- Wave Manager:
  - `com.bodytd.core.WaveManager.kt` (primary)
  - `com.example.myapplicationbodytd.managers/WaveManager.kt` (merge improvements)

### 3. Game Entities (High Priority)

Core game objects and their implementations.

- Enemies:

  - `com.bodytd.core.enemies/*` (primary)
  - `com.example.myapplicationbodytd.enemies/*` (merge unique features)

- Towers:
  - `com.bodytd.core.towers/*` (primary)
  - `com.example.myapplicationbodytd.towers/*` (merge improvements)

### 4. UI Layer (Medium Priority)

User interface components and views.

- Game Map View:

  - `com.bodytd.core.ui/GameMapView.kt`
  - `com.example.myapplicationbodytd.ui/GameMapView.kt`
  - Merge best features from both implementations

- Other UI Components:
  - Review and merge unique UI components from both implementations

### 5. Path System (Medium Priority)

- `com.bodytd.core.path/*` (primary)
- Merge any path-related code from example implementation

### 6. Player Management (Medium Priority)

- `com.example.myapplicationbodytd.player/*`
- `com.bodytd.core.models/PlayerState.kt`
- Merge player-related functionality

### 7. ViewModels (Lower Priority)

- `com.example.myapplicationbodytd.viewmodels/*`
- Integrate with core implementation

### 8. Main Activity (Final Step)

- `com.example.myapplicationbodytd.MainActivity.kt`
- Update to use merged implementations

## Merge Strategy

1. For each component:

   - Compare implementations
   - Identify best features from each
   - Create unified implementation
   - Update dependencies and references

2. Testing Approach:

   - Test each merged component individually
   - Integration testing after each major merge
   - Full system testing after all merges

3. Conflict Resolution:
   - Prefer core implementation for architecture
   - Incorporate improvements from example
   - Maintain consistent naming conventions
   - Keep best performing solutions

## Implementation Order

1. Start with models and interfaces
2. Move to managers (Game, Wave)
3. Implement game entities (Enemies, Towers)
4. Update UI components
5. Integrate path system
6. Merge player management
7. Update ViewModels
8. Final MainActivity update

## Notes

- Keep core package structure as primary
- Maintain backward compatibility where possible
- Document significant changes
- Update tests for merged implementations
