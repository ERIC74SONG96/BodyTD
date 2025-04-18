package com.example.myapplicationbodytd
// EXAMPLE FILE //
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplicationbodytd.ui.GameScreen
// Remove unused Map and WaveManager imports if GameManager is singleton
// import com.example.myapplicationbodytd.game.map.Map
// import com.example.myapplicationbodytd.managers.WaveManager
import com.example.myapplicationbodytd.managers.GameManager // Import the singleton object
import com.example.myapplicationbodytd.viewmodels.GameViewModelFactory

class MainActivity : ComponentActivity() {

    // No need for lazy delegate for singleton objects
    // private val gameManager by lazy { GameManager(Map(), WaveManager()) } // REMOVE THIS LINE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    // Create the Factory, passing the singleton GameManager instance directly
                    val viewModelFactory = GameViewModelFactory(GameManager)
                    // Pass the factory to GameScreen
                    GameScreen(viewModelFactory = viewModelFactory)
                }
            }
        }
    }
}
// EXAMPLE FILE //
