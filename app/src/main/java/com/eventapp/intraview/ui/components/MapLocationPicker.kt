package com.eventapp.intraview.ui.components

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapLocationPicker(
    initialLocation: LocationData? = null,
    onLocationSelected: (LocationData) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    // Default location (New York City)
    val defaultLocation = LatLng(40.7128, -74.0060)
    var selectedLocation by remember { 
        mutableStateOf(
            initialLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation
        ) 
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
    }
    
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }
    
    // Move camera to selected location when it changes
    LaunchedEffect(selectedLocation) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f)
        )
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                Marker(
                    state = MarkerState(position = selectedLocation),
                    title = "Selected Location"
                )
            }
            
            // Top bar with cancel and title
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.normal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )
                    }
                    Text(
                        text = stringResource(R.string.select_location),
                        style = MaterialTheme.typography.titleLarge
                    )
                    // Empty space for balance
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
            
            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(AppSpacing.normal),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                // My Location button
                if (locationPermissionState.status.isGranted) {
                    FloatingActionButton(
                        onClick = {
                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                            try {
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    location?.let {
                                        val newLocation = LatLng(it.latitude, it.longitude)
                                        selectedLocation = newLocation
                                    }
                                }
                            } catch (e: SecurityException) {
                                // Handle permission error
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Location"
                        )
                    }
                }
                
                // Confirm button
                Button(
                    onClick = {
                        onLocationSelected(
                            LocationData(
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude,
                                address = "${selectedLocation.latitude}, ${selectedLocation.longitude}"
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimensions.buttonHeightLarge),
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.iconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.small))
                    Text(
                        text = stringResource(R.string.confirm_location),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CompactMapView(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 13f)
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        Marker(
            state = MarkerState(position = location)
        )
    }
}

