package nl.utwente.smartspaces.cypaqu.data

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class AccelerometerData(
	val x: Float,
	val y: Float,
	val z: Float
) {
	val magnitude: Float
		get() = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

	override fun toString(): String {
		return "AccelerometerData(x=%+04.3f, y=%+04.3f, z=%+04.3f; magnitude=%+04.3f)"
			.format(x, y, z, magnitude)
	}

	fun isAnomaly(average: AccelerometerData): Boolean {
		val difference = abs(this - average)

		return difference.magnitude > ANOMALY_THRESHOLD ||
				difference.x > ANOMALY_THRESHOLD_X ||
				difference.y > ANOMALY_THRESHOLD_Y ||
				difference.z > ANOMALY_THRESHOLD_Z
	}

	private fun abs(accelerometerData: AccelerometerData): AccelerometerData {
		return AccelerometerData(
			abs(accelerometerData.x),
			abs(accelerometerData.y),
			abs(accelerometerData.z)
		)
	}

	private operator fun minus(other: AccelerometerData): AccelerometerData {
		return AccelerometerData(
			x - other.x,
			y - other.y,
			z - other.z
		)
	}
}
