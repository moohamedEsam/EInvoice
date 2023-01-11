package com.example.maplocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.koin.androidx.compose.viewModel

@Composable
fun MapScreen(
    onLocationPicked: (LatLng) -> Unit = {}
) {
    val cameraPosition = rememberCameraPositionState()
    val viewModel: MapViewModel by viewModel()
    val address by viewModel.address.collectAsState()
    val context = LocalContext.current
    val locationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { !it }) return@rememberLauncherForActivityResult
        val fusedLocationClient = FusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            priority = Priority.PRIORITY_HIGH_ACCURACY
            fastestInterval = 5000
        }
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val client = LocationServices.getSettingsClient(context)
        getCurrentLocation(client, request, fusedLocationClient, viewModel, context)

    }
    val markerState = rememberMarkerState()
    var showDialog by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = address) {
        if (address != null)
            markerState.position = LatLng(address!!.latitude, address!!.longitude)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            googleMapOptionsFactory = { GoogleMapOptions().rotateGesturesEnabled(true) },
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true),
            cameraPositionState = cameraPosition,
            onMapLongClick = viewModel::setAddress
        ) {
            if (address == null) return@GoogleMap
            Marker(state = markerState)
            viewModel.animateCamera {
                cameraPosition.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(address!!.latitude, address!!.longitude),
                        15f
                    )
                )
            }
        }
        LocationTextField(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart),
            viewModel = viewModel
        )
        LocationConfirmButton(
            address,
            modifier = Modifier.align(Alignment.BottomEnd),
            onLocationPicked
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    locationPermission.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                    showDialog = false
                }) {
                    Text("Use Current Location")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Select Manually")
                }
            },
            title = { Text("Select Your Location") },
            text = { Text("select your location manually on the map or use your current as an address") }
        )
    }
}


@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    client: SettingsClient,
    request: LocationSettingsRequest,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: MapViewModel,
    context: Context
) {
    client.checkLocationSettings(request).addOnSuccessListener {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null)
                    viewModel.setAddress(LatLng(location.latitude, location.longitude))
            }
    }.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            exception.startResolutionForResult(context as ComponentActivity, 0)
            getCurrentLocation(client, request, fusedLocationClient, viewModel, context)
        }
    }
}

@Composable
private fun LocationConfirmButton(
    address: LatLng?,
    modifier: Modifier = Modifier,
    onLocationPicked: (LatLng) -> Unit
) {
    val show by remember {
        mutableStateOf(MutableTransitionState(address != null))
    }
    LaunchedEffect(key1 = address) {
        show.targetState = address != null
    }
    AnimatedVisibility(visibleState = show, modifier = modifier.padding(8.dp)) {
        FloatingActionButton(
            onClick = { onLocationPicked(address!!) }
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LocationTextField(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel,
) {
    val context = LocalContext.current
    val geocoder by remember {
        mutableStateOf(Geocoder(context))
    }
    val query by viewModel.query.collectAsState()
    val suggestions by viewModel.findByLocationName(geocoder).collectAsState(initial = emptyList())
    var showSuggestions by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = {
                viewModel.setQuery(it)
                showSuggestions = true
            },
            label = { Text("Search By Location Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { showSuggestions = !showSuggestions }) {
                    if (showSuggestions)
                        Icon(imageVector = Icons.Default.ExpandLess, contentDescription = null)
                    else
                        Icon(imageVector = Icons.Outlined.ExpandMore, contentDescription = null)
                }
            }
        )
        if (showSuggestions)
            LazyColumn {
                items(suggestions) {
                    ListItem(
                        headlineText = { Text(text = it.featureName) },
                        supportingText = {
                            Text(
                                text = it.getAddressLine(0),
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.setAddress(LatLng(it.latitude, it.longitude))
                        }
                    )
                }
            }
    }
}

@Preview
@Composable
fun mapPreview() {
    MapScreen(
        onLocationPicked = {}
    )
}