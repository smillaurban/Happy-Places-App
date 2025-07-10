package com.bsi.happy_places_app

import MapScreen
import PlacesViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter


@Composable
fun AppNavigation() {
    val placesViewModel: PlacesViewModel = viewModel()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController, placesViewModel) }
        composable("work") { WorkScreen(navController, placesViewModel) }
        composable("map") { MapScreen(navController, placesViewModel) }
    }
}

@Composable
fun StartScreen(
    navController: NavHostController,
    placesViewModel: PlacesViewModel = viewModel()
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Gespeicherte Orte:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            for (place in placesViewModel.places) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(place.title, style = MaterialTheme.typography.titleMedium)
                        Text(place.description, style = MaterialTheme.typography.bodyMedium)
                        place.image?.let { uri ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Position: ${place.position.latitude}, ${place.position.longitude}",
                            style = MaterialTheme.typography.bodySmall
                        )                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.navigate("work") }) {
                Text("Happyplace hinzufügen")
            }
            Button(onClick = { navController.navigate("map") }) {
                Text("Karte anzeigen")
            }
        }
    }
}