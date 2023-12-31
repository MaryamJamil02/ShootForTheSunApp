package com.example.sunandmoon.ui.components.infoComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunandmoon.R
import com.example.sunandmoon.ui.theme.CheckmarkColor
import com.example.sunandmoon.ui.theme.RedColor
import com.example.sunandmoon.viewModel.ARViewModel

@Composable
fun CalibrateMagnetometerDialogue(modifier: Modifier, arViewModel: ARViewModel = viewModel()) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = {
            arViewModel.setHasShownCalibrateMagnetMessage(true)
        },
        text = {
            Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                
                Text(text = stringResource(id = R.string.CalibrateMagnetometer), fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.nunito_bold)))

                Icon(
                    painter = painterResource(R.drawable.rotate_icon),
                    stringResource(id = R.string.CalibrateMagnetometer),
                    modifier.size(120.dp),
                )
            }
        },
        confirmButton = {
            Button(onClick = { arViewModel.setHasShownCalibrateMagnetMessage(true) }) {
                Text(text = stringResource(id = R.string.OK), fontFamily = FontFamily(Font(R.font.nunito_bold)))
            }
        }
    )
}

