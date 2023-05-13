package com.example.sunandmoon.ui.screens

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunandmoon.viewModel.ShootInfoViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.sunandmoon.R
import com.example.sunandmoon.data.util.Shoot
import com.example.sunandmoon.ui.components.CalendarComponent
import com.example.sunandmoon.ui.components.NavigationComposable
import com.example.sunandmoon.ui.components.buttonComponents.GoBackEditDeleteBar
import com.example.sunandmoon.ui.components.infoComponents.SunPositionsCard
import com.example.sunandmoon.ui.components.infoComponents.UVCard
import com.example.sunandmoon.ui.components.infoComponents.WeatherCard
import com.example.sunandmoon.ui.components.infoComponents.WindCard
import com.example.sunandmoon.ui.theme.UVLowColor
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShootInfoScreen(modifier: Modifier, navigateBack: () -> Unit, shootInfoViewModel: ShootInfoViewModel = hiltViewModel(), shootId: Int, navigateToCreateShootScreen: (parentProductionId: Int?, shootToEditId: Int?) -> Unit){

    val shootInfoUIState by shootInfoViewModel.shootInfoUIState.collectAsState()

    if (shootInfoUIState.shoot == null) {
        shootInfoViewModel.getShoot(shootId)
        return
    }

    val dateAndTime = shootInfoUIState.shoot!!.date
    val date = dateAndTime.toLocalDate()
    val timeWithSeconds = dateAndTime.toLocalTime()
    val time = timeWithSeconds.truncatedTo(ChronoUnit.MINUTES)


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier.fillMaxWidth().padding(top = 10.dp)) {
                GoBackEditDeleteBar(
                    modifier,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    navigateBack,
                    { navigateToCreateShootScreen(null, shootId) },
                    {
                        shootInfoViewModel.deleteShoot()
                        navigateBack()
                    }
                )

                //Header for shoot name
                Text(text = shootInfoUIState.shoot!!.name, modifier = modifier.fillMaxWidth().align(CenterHorizontally), fontSize = 50.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center )

                //Location for shoot
                Row(modifier = modifier.align(CenterHorizontally)) {
                    Icon(painter = painterResource(id = R.drawable.location1), "Location Icon", modifier = modifier.size(35.dp).padding(end = 5.dp), MaterialTheme.colorScheme.primary)
                    Text(text = shootInfoUIState.shoot!!.locationName, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = modifier.size(40.dp))

                //Calendar and time
                Row(modifier = modifier.align(CenterHorizontally)){
                    Icon(painter = painterResource(id = R.drawable.calendar), "Calendar Icon", modifier = modifier.size(35.dp).padding(end = 5.dp), MaterialTheme.colorScheme.primary)
                    Text(text = date.toString(), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = modifier.size(30.dp))

                    Icon(painter = painterResource(id = R.drawable.clock), "Clock Icon", modifier = modifier.size(35.dp).padding(end = 5.dp), MaterialTheme.colorScheme.primary)
                    Text(text = time.toString(), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        },


    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 20.dp)
        )

        {
            item {

                SunPositionsCard(
                    modifier = modifier,
                    sunriseTime = shootInfoUIState.sunriseTime,
                    solarNoonTime = shootInfoUIState.solarNoonTime,
                    sunsetTime = shootInfoUIState.sunsetTime
                )

                WeatherCard(
                    modifier = modifier,
                    time,
                    shootInfoUIState.weatherData?.properties?.timeseries?.
                    firstOrNull({ it -> it.toString() == "2023-05-12T10:00:00Z" })?.data?.instant?.details?.air_temperature
                )

                WindCard(
                    modifier = modifier, time
                )

                UVCard(
                    modifier = modifier, time
                )


            }
        }

    }

}