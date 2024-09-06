package nl.utwente.smartspaces.cypaqu.data

import com.google.android.gms.maps.model.LatLng
import kotlin.time.Duration.Companion.seconds

const val CHART_AXIS_Y_MIN = 0.0
const val CHART_AXIS_Y_MAX = 20.0
const val CHART_LENGTH = 100

const val WINDOW_SIZE = 6

const val ANOMALY_THRESHOLD = 4
const val ANOMALY_THRESHOLD_X = 4
const val ANOMALY_THRESHOLD_Y = 4
const val ANOMALY_THRESHOLD_Z = 4

val anomalyCooldown = 3.seconds

val defaultLocation = LatLng(52.2383, 6.8507)
