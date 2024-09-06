package nl.utwente.smartspaces.cypaqu.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import nl.utwente.smartspaces.cypaqu.data.AccelerometerData
import nl.utwente.smartspaces.cypaqu.data.CHART_AXIS_Y_MAX
import nl.utwente.smartspaces.cypaqu.data.CHART_AXIS_Y_MIN
import nl.utwente.smartspaces.cypaqu.data.defaultLocation

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Anomalies(
	padding: PaddingValues,
	anomaliesViewModel: AnomaliesViewModel = viewModel()
) {
	val context = LocalContext.current
	val configuration = LocalConfiguration.current

	val locationPermissionState = rememberMultiplePermissionsState(
		listOf(
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
	)
	val locationClient = LocationServices.getFusedLocationProviderClient(context)

	val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	val accelerometerListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				event?.let {
					if (ContextCompat.checkSelfPermission(
							context,
							Manifest.permission.ACCESS_FINE_LOCATION
						) == PackageManager.PERMISSION_GRANTED
					) {
						locationClient.getCurrentLocation(
							CurrentLocationRequest.Builder().build(),
							null
						).addOnSuccessListener { location ->
							anomaliesViewModel.addMeasurement(
								AccelerometerData(
									it.values[0],
									it.values[1],
									it.values[2]
								),
								LatLng(location.latitude, location.longitude)
							)
						}
					}
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

	if (locationPermissionState.allPermissionsGranted) {
		when (configuration.orientation) {
			Configuration.ORIENTATION_PORTRAIT -> {
				Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
					Chart(modifier = Modifier.defaultMinSize())
					Map(
						padding = padding,
						modifier = Modifier.fillMaxSize()
					)
				}
			}

			Configuration.ORIENTATION_LANDSCAPE -> {
				Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
					Chart(modifier = Modifier.weight(1f))
					Map(
						padding = padding,
						modifier = Modifier.weight(2f)
					)
				}
			}

			else -> {
				Text(
					text = "Unsupported orientation",
					textAlign = TextAlign.Center
				)
			}
		}

		CartesianChartHost(
			chart = rememberCartesianChart(
				rememberLineCartesianLayer(
					axisValueOverrider = AxisValueOverrider.fixed(
						minY = CHART_AXIS_Y_MIN,
						maxY = CHART_AXIS_Y_MAX
					)
				),
			),
			modelProducer = anomaliesViewModel.modelProducer,
		)
	} else {
		Column(
			modifier = Modifier
				.padding(32.dp)
				.fillMaxSize()
				.wrapContentHeight(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Location permission is needed to visualise anomalies",
				textAlign = TextAlign.Center
			)
			Button(
				onClick = { locationPermissionState.launchMultiplePermissionRequest() }
			) {
				Text("Request permission")
			}
		}
	}
}

@Composable
fun Chart(
	modifier: Modifier = Modifier.fillMaxSize(),
	anomaliesViewModel: AnomaliesViewModel = viewModel()
) {
	CartesianChartHost(
		chart = rememberCartesianChart(
			rememberLineCartesianLayer(
				axisValueOverrider = AxisValueOverrider.fixed(
					minY = 0.0,
					maxY = 20.0
				),
			),
		),
		modelProducer = anomaliesViewModel.modelProducer,
		modifier = modifier
	)
}

@Composable
fun Map(
	padding: PaddingValues,
	modifier: Modifier = Modifier.fillMaxSize(),
	anomaliesViewModel: AnomaliesViewModel = viewModel()
) {
	val uiState by anomaliesViewModel.uiState.collectAsState()
	val cameraPositionState = rememberCameraPositionState {
		position = CameraPosition.fromLatLngZoom(uiState.lastPosition ?: defaultLocation, 15f)
	}

	LaunchedEffect(uiState.lastPosition) {
		uiState.lastPosition?.let {
			cameraPositionState.animate(CameraUpdateFactory.newLatLng(it))
		}
	}

	Column {
		Text(
			text = uiState.anomalies.size.toString()
		)
		GoogleMap(
			modifier = modifier,
			cameraPositionState = cameraPositionState,
			contentPadding = padding,
		) {
			uiState.anomalies.forEach { anomaly ->
				Marker(
					state = MarkerState(position = anomaly),
					title = "Anomaly",
					snippet = "Anomaly detected here"
				)
			}

			Marker(
				state = rememberMarkerState(position = LatLng(52.2383, 6.8507)),
				title = "University of Twente",
				snippet = "The most beautiful campus in the Netherlands",
			)
		}
	}
}
