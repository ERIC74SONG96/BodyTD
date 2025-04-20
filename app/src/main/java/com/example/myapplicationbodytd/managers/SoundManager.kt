package com.example.myapplicationbodytd.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import com.example.myapplicationbodytd.R // Import R class
// import android.media.MediaPlayer // Comment out old MediaPlayer import

/**
 * Singleton object to manage sound effects and background music playback.
 * NOTE: This implementation uses placeholders. Real sounds need loading and ExoPlayer is preferred for music.
 */
object SoundManager {

    private var soundPool: SoundPool? = null
    private var context: Context? = null
    private var isInitialized = false
    private var isMuted = false // TODO: Add UI control for this

    // Map to hold sound IDs returned by SoundPool.load()
    // Using Int placeholders (1) as we can't load real sounds now.
    private val sounds = mutableMapOf<SoundType, Int>()

    // Placeholder for music player (ExoPlayer recommended)
    // private var backgroundMusicPlayer: ExoPlayer? = null

    enum class SoundType {
        ENEMY_HIT,
        ENEMY_DEATH,
        ENEMY_REACHED_END,
        TOWER_PLACE,
        TOWER_FIRE,
        WAVE_START,
        WAVE_CLEARED,
        GAME_WIN,
        GAME_LOSE,
        UI_CLICK // Example for UI feedback
    }

    /**
     * Initializes the SoundManager. Must be called once, preferably from the Application class.
     * @param appContext The application context.
     */
    fun init(appContext: Context) {
        if (isInitialized) {
            Log.w("SoundManager", "Already initialized.")
            return
        }
        context = appContext.applicationContext // Store application context

        try {
            // Build SoundPool
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10) // Max simultaneous streams
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    Log.d("SoundManager", "Sound loaded successfully: $sampleId")
                    // Here you would map the loaded sampleId back to your SoundType if needed
                } else {
                    Log.e("SoundManager", "Error loading sound $sampleId, status: $status")
                }
            }

            loadSounds() // Load sounds (currently placeholders)

            // TODO: Initialize ExoPlayer for background music here
            // backgroundMusicPlayer = ExoPlayer.Builder(context!!).build()
            // val mediaItem = MediaItem.fromUri(Uri.parse("android.resource://${context!!.packageName}/${R.raw.background_music}"))
            // backgroundMusicPlayer?.setMediaItem(mediaItem)
            // backgroundMusicPlayer?.repeatMode = Player.REPEAT_MODE_ONE // Loop music
            // backgroundMusicPlayer?.prepare()

            isInitialized = true
            Log.d("SoundManager", "SoundManager initialized successfully.")

        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing SoundManager", e)
            soundPool = null // Ensure pool is null on error
        }
    }

    /** Placeholder for loading sound resources */
    private fun loadSounds() {
        if (soundPool == null || context == null) {
            Log.e("SoundManager", "Cannot load sounds: SoundPool or Context is null.")
            return
        }
        Log.d("SoundManager", "Loading sounds...")
        try {
            // Load the specific hit sound
            sounds[SoundType.ENEMY_HIT] = soundPool!!.load(context, R.raw.hit_03, 1)

            // Load placeholders for other sounds (replace with actual files later)
            sounds[SoundType.ENEMY_DEATH] = 1 // Placeholder ID
            sounds[SoundType.ENEMY_REACHED_END] = 1 // Placeholder ID
            sounds[SoundType.TOWER_PLACE] = 1 // Placeholder ID
            sounds[SoundType.TOWER_FIRE] = 1 // Placeholder ID
            sounds[SoundType.WAVE_START] = 1 // Placeholder ID
            sounds[SoundType.WAVE_CLEARED] = 1 // Placeholder ID
            sounds[SoundType.GAME_WIN] = 1 // Placeholder ID
            sounds[SoundType.GAME_LOSE] = 1 // Placeholder ID
            sounds[SoundType.UI_CLICK] = 1 // Placeholder ID

            Log.d("SoundManager", "Sounds map after loading: $sounds")
        } catch (e: Exception) {
            Log.e("SoundManager", "Error loading sounds", e)
            // Consider how to handle errors - maybe disable sound?
        }
    }

    /**
     * Plays a specific sound effect.
     * @param soundType The type of sound to play.
     */
    fun playSound(soundType: SoundType) {
        if (!isInitialized || isMuted || soundPool == null) return

        val soundId = sounds[soundType]
        if (soundId != null && soundId != 0) { // Check if sound ID is valid
            try {
                // TODO: Adjust volume, rate, loop etc. as needed
                soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
                // Log.v("SoundManager", "Playing sound: $soundType (ID: $soundId)") // Verbose logging
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing sound $soundType (ID: $soundId)", e)
            }
        } else {
            Log.w("SoundManager", "Sound not loaded or invalid ID for type: $soundType")
        }
    }

    /** Placeholder: Requests audio focus */
    private fun requestAudioFocusPlaceholder() {
        Log.d("SoundManager", "Placeholder: Requesting Audio Focus")
        // TODO: Implement actual audio focus request using AudioManager
    }

    /** Placeholder: Abandons audio focus */
    private fun abandonAudioFocusPlaceholder() {
        Log.d("SoundManager", "Placeholder: Abandoning Audio Focus")
        // TODO: Implement actual audio focus abandon using AudioManager
    }

    /** Starts playing background music (Placeholder) */
    fun startBackgroundMusic() {
        if (!isInitialized || isMuted) return
        Log.d("SoundManager", "Starting background music (Placeholder)")
        requestAudioFocusPlaceholder()
        // TODO: Use ExoPlayer: backgroundMusicPlayer?.playWhenReady = true
    }

    /** Stops playing background music (Placeholder) */
    fun stopBackgroundMusic() {
        if (!isInitialized) return
        Log.d("SoundManager", "Stopping background music (Placeholder)")
        abandonAudioFocusPlaceholder()
        // TODO: Use ExoPlayer: backgroundMusicPlayer?.playWhenReady = false
        // TODO: Seek to start? backgroundMusicPlayer?.seekTo(0)
    }

    /** Releases resources used by the SoundManager */
    fun release() {
        if (!isInitialized) return
        Log.d("SoundManager", "Releasing SoundManager resources.")
        try {
            soundPool?.release()
            // TODO: Release ExoPlayer: backgroundMusicPlayer?.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing resources", e)
        }
        soundPool = null
        // backgroundMusicPlayer = null
        context = null
        isInitialized = false
        sounds.clear()
    }

    // TODO: Add functions to control mute state (setMuted, toggleMute)
} 