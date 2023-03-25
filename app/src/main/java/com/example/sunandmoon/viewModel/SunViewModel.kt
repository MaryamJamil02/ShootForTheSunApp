package com.example.sunandmoon.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunandmoon.checkPermissions
import com.example.sunandmoon.data.DataSource
import com.example.sunandmoon.data.SunUiState
import com.example.sunandmoon.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SunViewModel : ViewModel() {

    private val sunDataSource = DataSource()
     //sunDataSource.fetchSunrise3Data("sun", 59.933333, 10.716667, "2022-12-18", "+01:00" ).properties.sunrise.time
    private val _sunUiState = MutableStateFlow(
        SunUiState(
            sunRiseTime = "not loaded", sunSetTime = "not loaded", solarNoon = "not loaded", locationSearchResults = listOf(), locationEnabled = true, chosenLocation = Pair(0.0,0.0)
        )
    )

    val sunUiState: StateFlow<SunUiState> = _sunUiState.asStateFlow()

    init {
        loadSunInformation()
    }



    private fun loadSunInformation(){
        viewModelScope.launch {
            try{
                val sunRiseTime = sunDataSource.fetchSunrise3Data("sun", 59.933333, 10.716667, "2022-12-18", "+01:00" ).properties.sunrise.time

                val sunSetTime = sunDataSource.fetchSunrise3Data("sun", 59.933333, 10.716667, "2022-12-18", "+01:00" ).properties.sunset.time

                val solarNoon = sunDataSource.fetchSunrise3Data("sun", 59.933333, 10.716667, "2022-12-18", "+01:00" ).properties.solarnoon.time

                _sunUiState.update { currentState ->
                    currentState.copy(
                        sunRiseTime = sunRiseTime,
                        sunSetTime = sunSetTime,
                        solarNoon = solarNoon,
                        locationSearchResults = listOf(),
                        )
                }

                Log.d("test",sunUiState.value.sunRiseTime + sunUiState.value.sunSetTime + sunUiState.value.solarNoon)
            }catch (e: Throwable){

                Log.d("error", "uh oh")
            }
        }
    }

    fun loadLocationSearchResults(query: String) {
        viewModelScope.launch {
            try{
                val locationSearchResults = sunDataSource.fetchLocationSearchResults(query, 10)

                _sunUiState.update { currentState ->
                    currentState.copy(
                    sunRiseTime = _sunUiState.value.sunRiseTime,
                    sunSetTime = _sunUiState.value.sunSetTime,
                    solarNoon = _sunUiState.value.solarNoon,
                    locationSearchResults = locationSearchResults
                    )
                }

            }catch (e: Throwable){
                Log.d("error", "uh oh" + e.toString())
            }
        }
    }

   fun updateLocation(newValue: Boolean){
        _sunUiState.update { currentState ->
            currentState.copy(
                locationEnabled = newValue
            )
        }
    }
    fun getCurrentPosition(fusedLocationProviderClient: FusedLocationProviderClient){
        viewModelScope.launch() {
            val location = fetchLocation(fusedLocationProviderClient)
            if (location != null){
                _sunUiState.update { currentState ->
                    currentState.copy(
                        chosenLocation = location
                    )
                }
            }

        }
    }
    }
