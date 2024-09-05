package nl.utwente.smartspaces.cypaqu.ui

import com.google.android.gms.maps.model.LatLng

data class AnomaliesUiState(
	val values: List<Int> = mutableListOf(),
	val lastPosition: LatLng? = null
)
