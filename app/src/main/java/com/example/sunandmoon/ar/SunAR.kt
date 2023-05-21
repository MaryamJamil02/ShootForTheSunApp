package com.example.sunandmoon.ar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sunandmoon.camera.CameraPreview
import com.example.sunandmoon.util.Permission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.example.sunandmoon.data.ARUIState
import com.example.sunandmoon.ui.components.NavigationComposable
import com.example.sunandmoon.viewModel.ARViewModel


@Composable
fun SunAR(
    modifier: Modifier,
    navigateToNextBottomBar: (index: Int) -> Unit,
    packageManager: PackageManager,
    arViewModel: ARViewModel = hiltViewModel()
) {
    val arUIState by arViewModel.arUIState.collectAsState()

    Surface() {
        CameraContent(Modifier.fillMaxSize())
    }

    val hasMagnetometer = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)
    val hasGyroscope = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
    val hasAccelerometer = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)

    Log.i(
        "sensorStatus",
        "hasMagnetometer: $hasMagnetometer, hasGyroscope: $hasGyroscope, hasAccelerometer: $hasAccelerometer"
    )

    val sensorStatus = remember {
        mutableStateOf(FloatArray(0) { 0f })
    }

    if (hasMagnetometer && hasGyroscope && hasAccelerometer) {
        val localContext = LocalContext.current

        val sensorManager: SensorManager =
            localContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        var rotationVectorSensor: Sensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val rotationVectorSensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    when (accuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> Log.i(
                            "sensorStatus",
                            "SENSOR_STATUS_ACCURACY: low"
                        )
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> Log.i(
                            "sensorStatus",
                            "SENSOR_STATUS_ACCURACY: medium"
                        )
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> Log.i(
                            "sensorStatus",
                            "SENSOR_STATUS_ACCURACY: high"
                        )
                    }
                }
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {

                    val rotationMatrix = FloatArray(16)
                    SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event.values
                    );

                    val transformedRotationMatrix = FloatArray(16)
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        transformedRotationMatrix
                    )

                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(transformedRotationMatrix, orientation)

                    sensorStatus.value = orientation

                    //println(sensorStatus.value[0])
                }
            }
        }

        sensorManager.registerListener(
            rotationVectorSensorEventListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    SunARUI(Modifier, sensorStatus, hasMagnetometer, navigateToNextBottomBar, arUIState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SunARUI(
    modifier: Modifier,
    sensorStatus: MutableState<FloatArray>,
    hasMagnetometer: Boolean,
    navigateToNextBottomBar: (index: Int) -> Unit,
    arUIState: ARUIState,
) {
    // for the AR functionality
    val sunZenith = arUIState.sunZenith
    val sunAzimuth = arUIState.sunAzimuth
    if(sunZenith != null && sunAzimuth != null) {
        SunFinder(modifier, sensorStatus.value, sunZenith, sunAzimuth)
    }

    // for the non-AR UI part for this screen
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { NavigationComposable(modifier = modifier, page = 1, navigateToNextBottomBar = navigateToNextBottomBar)}
    ) { val p = it /* just to remove the error message */ }
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.CAMERA,
        rationale = "To use AR-mode, you need to give the app permission to use your camera.",
        permissionNotAvailableContent = {
            Column(modifier) {
                Text("O noes! No Camera!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text("Open Settings")
                }
            }
        }
    ) {
        CameraPreview(modifier)
    }
}