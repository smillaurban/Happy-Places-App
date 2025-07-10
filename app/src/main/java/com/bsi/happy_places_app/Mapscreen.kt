import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(navController: NavHostController,
              placesViewModel: PlacesViewModel = viewModel()) {
    val context = LocalContext.current
    // osmdroid initialisieren
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    AndroidView(
        factory = {
            MapView(it).apply {
                setMultiTouchControls(true)
                controller.setZoom(10.0)
                val firstPlace = placesViewModel.places.firstOrNull()
                if (firstPlace != null) {
                    controller.setCenter(GeoPoint(firstPlace.position.latitude, firstPlace.position.longitude))
                }
                // Marker für alle Orte
                for (place in placesViewModel.places) {
                    val marker = Marker(this)
                    marker.position = GeoPoint(place.position.latitude, place.position.longitude)
                    marker.title = place.title
                    marker.snippet = place.description
                    overlays.add(marker)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}