import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

data class Place(
    val title: String,
    val description: String,
    val image: String?, //
    val position: GeoPoint
)

class PlacesViewModel : ViewModel() {
    var places = mutableStateListOf<Place>()
        private set

    fun addPlace(place: Place) {
        places.add(place)
    }
}