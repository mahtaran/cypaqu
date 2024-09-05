package nl.utwente.smartspaces.cypaqu.ui

import android.util.Log
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
import java.util.Date

class AnomaliesViewModel : ViewModel() {
	private val _uiState = MutableStateFlow(AnomaliesUiState())
	val uiState = _uiState.asStateFlow()

	val modelProducer = CartesianChartModelProducer()

	private fun addPoint(value: Int) {
		_uiState.update { currentState ->
			currentState.copy(
				values = currentState.values + value
			)
		}

		updateChart()
	}

	fun testMeasurement(data: AccelerometerData, position: LatLng) {
		val timestamp = Date()

		Log.d(
			"Test", """
			|Test measurement:
			|  Timestamp: $timestamp
			|  Data: $data
			|  Position: $position
		""".trimIndent()
		)

		addPoint(data.z.toInt())
		_uiState.update { currentState ->
			currentState.copy(
				lastPosition = position
			)
		}
	}

	private fun updateChart() {
		viewModelScope.launch {
			modelProducer.runTransaction {
				lineSeries {
					series(uiState.value.values.takeLast(20))
				}
			}
		}
	}
}
