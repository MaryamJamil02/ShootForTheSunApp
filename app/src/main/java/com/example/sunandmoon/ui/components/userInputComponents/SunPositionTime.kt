package com.example.sunandmoon.ui.components.userInputComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sunandmoon.R
import com.example.sunandmoon.ui.components.buttonComponents.CreateShootSunPositionCard
import com.example.sunandmoon.ui.theme.GreyColor
import java.time.LocalTime

@Composable
fun SunPositionTime(
    modifier: Modifier,
    updateTimePicker: (enabled: Boolean) -> Unit,
    chosenSunIndex: Int,
    updateChosenIndex: (index: Int) -> Unit
) {

    Card(
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GreyColor)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val color = MaterialTheme.colorScheme.secondary

            CreateShootSunPositionCard(
                modifier = modifier,

                painter = painterResource(
                    R.drawable.unavailable
                ),
                containerColor = color,
                chosen = chosenSunIndex==0 ,
                updateTimePicker = {updateTimePicker(true)},
                updatePositionIndex = {updateChosenIndex(0)}
                )

            CreateShootSunPositionCard(
                modifier = modifier,
                painter = painterResource(
                    R.drawable.sunrise
                ),
                containerColor = color,
                chosen = chosenSunIndex==1 ,
                updateTimePicker = {updateTimePicker(false)},
                updatePositionIndex = {updateChosenIndex(1)}

                )
            CreateShootSunPositionCard(
                modifier = modifier,
                painter = painterResource(
                    R.drawable.sun
                ),
                containerColor = color,
                chosen = chosenSunIndex==2 ,
                updateTimePicker = {updateTimePicker(false)},
                updatePositionIndex = {updateChosenIndex(2)}
            )
            CreateShootSunPositionCard(
                modifier = modifier,
                painter = painterResource(
                    R.drawable.sunset
                ),
                containerColor = color,
                chosen = chosenSunIndex==3 ,
                updateTimePicker = {updateTimePicker(false)},
                updatePositionIndex = {updateChosenIndex(3)}
            )
        }
    }

}