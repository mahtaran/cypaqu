package nl.utwente.smartspaces.cypaqu.ui

import androidx.compose.runtime.mutableStateListOf
import com.google.android.gms.maps.model.LatLng
import nl.utwente.smartspaces.cypaqu.data.SlidingWindow
import nl.utwente.smartspaces.cypaqu.data.WINDOW_SIZE
import kotlin.time.ComparableTimeMark

data class AnomaliesUiState(
	val window: SlidingWindow = SlidingWindow(WINDOW_SIZE),
	val values: List<Float> = mutableListOf(),
	val anomalies: List<LatLng> = mutableStateListOf(),
	val lastPosition: LatLng? = null,
	val lastAnomaly: ComparableTimeMark? = null
)
