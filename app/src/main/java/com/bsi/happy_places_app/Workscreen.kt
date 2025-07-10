package com.bsi.happy_places_app

import Place
import PlacesViewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.ImageDecoder
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


@Composable
fun WorkScreen(
    navController: NavHostController,
    placesViewModel: PlacesViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var markerPosition by remember { mutableStateOf<GeoPoint?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // Bildauswahl-Launcher
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            uri?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    imageBitmap = ImageDecoder.decodeBitmap(source).asImageBitmap()
                } else {
                    @Suppress("DEPRECATION")
                    val bitmap = BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(it)
                    )
                    imageBitmap = bitmap?.asImageBitmap()
                }
            }
        }

    // osmdroid-Konfiguration laden
    Configuration.getInstance()
        .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    // MapView merken, damit Marker aktualisiert werden können
    val mapView = remember { MapView(context) }

    // Marker auf Karte aktualisieren, wenn sich markerPosition ändert
    LaunchedEffect(markerPosition) {
        mapView.overlays.removeAll { it is Marker }
        markerPosition?.let { pos ->
            val marker = Marker(mapView)
            marker.position = pos
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
            mapView.controller.setCenter(pos)
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(52.52, 13.405))
                        if (overlays.none { it is MapEventsOverlay }) {
                            val mapEventsReceiver = object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                    markerPosition = p
                                    showDialog = true
                                    return true
                                }
                                override fun longPressHelper(p: GeoPoint): Boolean = false
                            }
                            overlays.add(MapEventsOverlay(mapEventsReceiver))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getCurrentLocation(
                        context = context,
                        onLocation = { lat, lon ->
                            markerPosition = GeoPoint(lat, lon)
                            title = ""
                            description = ""
                            imageUri = null
                            imageBitmap = null
                            mapView.controller.setCenter(markerPosition)
                            showDialog = true
                        },
                        onError = { errorMsg ->
                            println(errorMsg)
                        }
                    )
                } else {
                    println("Standort-Berechtigung nicht erteilt")
                }
            }

            // Buttons am unteren Rand
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = { navController.navigate("start") }) {
                    Text("Zurück zum Startscreen")
                }
                Button(onClick = {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        getCurrentLocation(
                            context = context,
                            onLocation = { lat, lon ->
                                markerPosition = GeoPoint(lat, lon)
                                title = ""
                                description = ""
                                imageUri = null
                                imageBitmap = null
                                mapView.controller.setCenter(markerPosition)
                                showDialog = true
                            },
                            onError = { errorMsg ->
                                println(errorMsg)
                            }
                        )
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) {
                    Text("Aktuellen Standort verwenden")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))



            // Dialog anzeigen
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Neuer Ort") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Titel") }
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Beschreibung") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { launcher.launch("image/*") }) {
                                Text("Bild auswählen")
                            }
                            imageBitmap?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            markerPosition?.let { pos ->
                                placesViewModel.addPlace(
                                    Place(
                                        title = title,
                                        description = description,
                                        image = imageUri?.toString(),
                                        position = pos
                                    )
                                )
                            }
                            showDialog = false
                            title = ""
                            description = ""
                            imageUri = null
                            imageBitmap = null
                        }) {
                            Text("Speichern")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Abbrechen")
                        }
                    }
                )
            }
        }
    }
}


@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getCurrentLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocation(location.latitude, location.longitude)
        } else {
            // Fallback: request new location
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000
            ).setMaxUpdates(1).build()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val loc = result.lastLocation
                        if (loc != null) {
                            onLocation(loc.latitude, loc.longitude)
                        } else {
                            onError("Standort konnte nicht ermittelt werden")
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                },
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener {
        onError("Standort konnte nicht ermittelt werden")
    }
}