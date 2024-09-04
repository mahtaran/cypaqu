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
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    private lateinit var xText: TextView
    private lateinit var gpsText: TextView

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                val data = "X: $x, Y: $y, Z: $z"
                xText.text = data
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Handle accuracy changes if necessary
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val gpsData = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            gpsText.text = gpsData
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize views
        xText = findViewById(R.id.xText)
        gpsText = findViewById(R.id.gpsText)


        // Initialize sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Register the listener
        accelerometerSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Initialize location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the sensor listener to stop receiving updates when the activity is destroyed
        sensorManager.unregisterListener(sensorEventListener)
        // Unregister the location listener
        locationManager.removeUpdates(locationListener)
    }
}


//import android.hardware.SensorManager
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//
//import android.widget.TextView
//
//class MainActivity : ComponentActivity() , SensorEventListener{
//    private lateinit var xText: TextView
//    private lateinit var yText: TextView
//    private lateinit var zText: TextView
//
//    private lateinit var sensorManager: SensorManager
//    private lateinit var accelerometer : Sensor
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        setContentView(R.layout.activity_main)
//        //get sensor manager from system service
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager;
//
//        // get accelerometer sensor from sensor manager
//        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).also {
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//        }
//        xText = findViewById(R.id.xText)
//        yText = findViewById(R.id.yText)
//        zText = findViewById(R.id.zText)
//
//
//
//
//        super.onCreate(savedInstanceState)
//    }
////        enableEdgeToEdge()
////        setContent {
////            SensorDataCollectionTheme {
////                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
////                    Greeting(
////                        sides = sides,
////                        modifier = Modifier.padding(innerPadding)
////                    )
////                }
////            }
////        }
////    }
//
//    // needed by the sensor event listener. This function wil triggers on sensor value changed
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event ?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//            xText.text = "X" +event.values[0]
//            yText.text="X" +event.values[1]
//            zText.text="Z" +event.values[2]
//
//        }
//
////            event.values[0].toString()
//
//    }
//
//    // this function is triggered when the sensor accuracy is changed
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO()
//    }
//}








//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.datareader.ui.theme.DataReaderTheme
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // initing the sensor manager to later access the Accelerometer Sensor
//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        setContent {
//            DataReaderTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    AccelerometerSensor(sensorManager, Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun AccelerometerSensorPreview() {
//    DataReaderTheme {
//        Text(text = "X: 0.0, Y: 0.0, Z: 0.0")
//    }
//}
//
//@Composable
//fun AccelerometerSensor(sensorManager: SensorManager, modifier: Modifier = Modifier) {
//    var accelerometerData by remember { mutableStateOf("Fetching data...") }
//
//    val sensorEventListener = remember {
//        object : SensorEventListener {
//            override fun onSensorChanged(event: SensorEvent?) {
//                event?.let {
//                    accelerometerData = "X: ${it.values[0]}, Y: ${it.values[1]}, Z: ${it.values[2]}"
//                }
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//                // Handle sensor accuracy changes if necessary
//            }
//        }
//    }
//
//    DisposableEffect(Unit) {
//        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
//
//        onDispose {
//            sensorManager.unregisterListener(sensorEventListener)
//        }
//    }
//
//    Text(
//        text = accelerometerData,
//        modifier = modifier
//    )
//}
