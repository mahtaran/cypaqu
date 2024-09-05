package nl.utwente.smartspaces.cypaqu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChartViewModel : ViewModel() {
	private val _uiState = MutableStateFlow(ChartUiState())
	val uiState = _uiState.asStateFlow()

	val modelProducer = CartesianChartModelProducer()

	fun addPoint(value: Int) {
		_uiState.update { currentState ->
			currentState.copy(
				values = currentState.values + value
			)
		}

		updateChart()
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
