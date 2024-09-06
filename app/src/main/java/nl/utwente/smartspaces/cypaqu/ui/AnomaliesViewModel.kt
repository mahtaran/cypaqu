package nl.utwente.smartspaces.cypaqu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.utwente.smartspaces.cypaqu.data.AccelerometerData
import nl.utwente.smartspaces.cypaqu.data.CHART_LENGTH
import nl.utwente.smartspaces.cypaqu.data.anomalyCooldown
import kotlin.time.TimeSource

class AnomaliesViewModel : ViewModel() {
	private val _uiState = MutableStateFlow(AnomaliesUiState())
	val uiState = _uiState.asStateFlow()

	val modelProducer = CartesianChartModelProducer()
	private val timeSource = TimeSource.Monotonic

	fun addMeasurement(data: AccelerometerData, position: LatLng) {
		_uiState.update { currentState ->
			currentState.copy(
				window = currentState.window.add(data),
				values = currentState.values + data.magnitude,
				lastPosition = position
			)
		}
		updateChart()

		if (data.isAnomaly(_uiState.value.window.average)) {
			_uiState.value.lastAnomaly?.let { lastAnomaly ->
				if (lastAnomaly + anomalyCooldown > timeSource.markNow()) {
					return
				}
			}

			_uiState.update { currentState ->
				currentState.copy(
					anomalies = currentState.anomalies + position,
					lastAnomaly = timeSource.markNow()
				)
			}
		}
	}

	private fun updateChart() {
		viewModelScope.launch {
			modelProducer.runTransaction {
				lineSeries {
					series(uiState.value.values.takeLast(CHART_LENGTH))
				}
			}
		}
	}
}
