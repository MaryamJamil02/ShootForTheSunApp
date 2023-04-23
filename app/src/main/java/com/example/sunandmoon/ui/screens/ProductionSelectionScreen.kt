package com.example.sunandmoon.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunandmoon.R
import com.example.sunandmoon.ui.components.CalendarComponent
import com.example.sunandmoon.ui.components.NavigationComposable
import com.example.sunandmoon.ui.components.SunCard
import com.example.sunandmoon.viewModel.ShootInfoViewModel
import java.time.LocalDateTime



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionSelectionScreen(modifier: Modifier, navigateToNext: (localDateTime: LocalDateTime, location: Location) -> Unit, shootInfoViewModel: ShootInfoViewModel = viewModel()){

    val sunUiState by shootInfoViewModel.shootInfoUIState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column() {
                LocationSearch(modifier = modifier)
                CalendarComponent(modifier)
            }
        },


        content = {innerPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = 20.dp)
            )

            {

                item {
                    SunCard(modifier, "Sunrise", painterResource(id = R.drawable.sunrise), sunUiState.sunriseTime)
                    SunCard(modifier, "Solar noon", painterResource(id = R.drawable.solarnoon), sunUiState.solarNoonTime)
                    SunCard(modifier, "Sunset", painterResource(id = R.drawable.sunset), sunUiState.sunsetTime)
                }
            }

        },
        bottomBar = {
            NavigationComposable(page = 0, navigateToNext)
        }
    )

}