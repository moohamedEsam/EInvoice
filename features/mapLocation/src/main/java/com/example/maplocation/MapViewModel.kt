package com.example.maplocation

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _address = MutableStateFlow<LatLng?>(null)
    val address = _address.asStateFlow()


    fun setQuery(value: String) = viewModelScope.launch {
        _query.update { value }
    }

    @OptIn(FlowPreview::class)
    fun findByLocationName(geocoder: Geocoder) = channelFlow<List<Address>> {
        _query.debounce(500)
            .collectLatest {
                if (it.length > 3)
                    send(geocoder.getFromLocationName(it, 4) ?: emptyList())
            }
    }

    fun animateCamera(animate: suspend () -> Unit) = viewModelScope.launch {
        animate()
    }

    fun setAddress(address: LatLng) {
        _address.update { address }
    }
}