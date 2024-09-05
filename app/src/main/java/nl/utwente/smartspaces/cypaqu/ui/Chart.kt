package nl.utwente.smartspaces.cypaqu.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun Chart(
	chartViewModel: ChartViewModel = viewModel()
) {
	val chartUiState by chartViewModel.uiState.collectAsState()

	val context = LocalContext.current

	val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

	val accelerometerListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				event?.let {
					chartViewModel.addPoint(it.values[2].toInt())
					println(
						"X: ${it.values[0]}, Y: ${it.values[1]}, Z: ${it.values[2]}"
					)
				}
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
				// Do nothing
			}
		}
	}

	DisposableEffect(Unit) {
		val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
		if (accelerometer != null) {
			sensorManager.registerListener(
				accelerometerListener,
				accelerometer,
				SensorManager.SENSOR_DELAY_UI
			)
		}

		onDispose {
			sensorManager.unregisterListener(accelerometerListener)
		}
	}

	CartesianChartHost(
		chart = rememberCartesianChart(
			rememberLineCartesianLayer(
				axisValueOverrider = AxisValueOverrider.fixed(
					minY = 0.0,
					maxY = 20.0
				)
			),
		),
		modelProducer = chartViewModel.modelProducer,
	)
}
