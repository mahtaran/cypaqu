package nl.utwente.smartspaces.cypaqu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import nl.utwente.smartspaces.cypaqu.ui.Anomalies
import nl.utwente.smartspaces.cypaqu.ui.theme.CyPaQuTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			CyPaQuTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
					Anomalies(padding)
				}
			}
		}
	}
}
