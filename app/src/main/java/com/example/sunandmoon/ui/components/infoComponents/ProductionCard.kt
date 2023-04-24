package com.example.sunandmoon.ui.components.infoComponents

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sunandmoon.data.util.Shoot
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ProductionCard(modifier: Modifier, shoot: Shoot, navigateToNext: (shoot: Shoot) -> Unit) {
    ElevatedCard(
        modifier = modifier
        .fillMaxWidth()
        .padding(15.dp).clickable{ navigateToNext(shoot) },
    ){
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = shoot.name, modifier = modifier.padding(20.dp))
            Text(text = shoot.date.toLocalDate().toString(), modifier = modifier.padding(20.dp))
        }
    }
}