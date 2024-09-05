package com.datareader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datareader.ui.theme.DataReaderTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request location permissions
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                // Permission granted for fine location
            } else {
                // Permission denied. Handle appropriately (e.g., show a message to the user)
            }
        }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted. Access the GPS data
            }
            else -> {
                // Request both FINE and COARSE location permissions
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        setContent {
            DataReaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorAndGpsData(
                        sensorManager,
                        locationManager,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SensorAndGpsData(
    sensorManager: SensorManager,
    locationManager: LocationManager,
    modifier: Modifier = Modifier
) {
    var accelerometerData by remember { mutableStateOf("Fetching accelerometer data...") }
    var gpsData by remember { mutableStateOf("Fetching GPS data...") }

    // Get context from composable environment
    val context = LocalContext.current

    // Accelerometer Sensor
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    accelerometerData = "X: ${it.values[0]}, Y: ${it.values[1]}, Z: ${it.values[2]}"
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle sensor accuracy changes if necessary
            }
        }
    }

    // Location Listener
    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                gpsData = "Lat: ${location.latitude}, Long: ${location.longitude}"
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
    }

    DisposableEffect(Unit) {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometerSensor != null) {
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometerData = "Accelerometer not available"
        }

        // Check for location permission before requesting location updates
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    locationListener
                )
            } else {
                gpsData = "GPS not available"
            }
        } else {
            gpsData = "Location permission not granted"
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
            locationManager.removeUpdates(locationListener)
        }
    }

    Column(modifier = modifier) {
        Text(text = accelerometerData)
        Text(text = gpsData)
    }
}

@Preview(showBackground = true)
@Composable
fun SensorAndGpsDataPreview() {
    DataReaderTheme {
        Column {
            Text(text = "X: 0.0, Y: 0.0, Z: 0.0")
            Text(text = "Lat: 0.0, Long: 0.0")
        }
    }
}