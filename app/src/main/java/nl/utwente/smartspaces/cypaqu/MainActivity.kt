package nl.utwente.smartspaces.cypaqu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import nl.utwente.smartspaces.cypaqu.ui.Chart
import nl.utwente.smartspaces.cypaqu.ui.theme.CyPaQuTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			CyPaQuTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
					Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
						Chart()
						Map(padding)
					}
				}
			}
		}
	}
}

@Composable
fun Map(padding: PaddingValues) {
	val utwente = LatLng(52.2383, 6.8507)
	val utwenteMarkerState = rememberMarkerState(position = utwente)
	val cameraPositionState = rememberCameraPositionState {
		position = CameraPosition.fromLatLngZoom(utwente, 15f)
	}

	GoogleMap(
		modifier = Modifier.fillMaxSize(),
		cameraPositionState = cameraPositionState,
		contentPadding = padding,
	) {
		Marker(
			state = utwenteMarkerState,
			title = "University of Twente",
			snippet = "The most beautiful campus in the Netherlands",
		)
	}
}

@Preview(showBackground = true)
@Composable
fun MapPreview() {
	CyPaQuTheme {
		Map(PaddingValues(0.dp))
	}
}
