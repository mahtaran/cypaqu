package nl.utwente.smartspaces.cypaqu.data

class SlidingWindow(private val size: Int) {
	private val window: MutableList<AccelerometerData> = ArrayList(size)

	val average: AccelerometerData
		get() = AccelerometerData(
			window.map { it.x }.average().toFloat(),
			window.map { it.y }.average().toFloat(),
			window.map { it.z }.average().toFloat()
		)

	fun add(value: AccelerometerData): SlidingWindow {
		if (window.size == size) {
			window.removeAt(0)
		}
		window.add(value)

		return this
	}
}
