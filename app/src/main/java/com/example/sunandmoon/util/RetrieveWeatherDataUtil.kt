package com.example.sunandmoon.util

import android.util.Log
import com.example.sunandmoon.model.LocationForecastModel.LocationForecast
import com.example.sunandmoon.model.LocationForecastModel.Timeseries
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCorrectTimeObject(dateAndTime: LocalDateTime, weatherData: LocationForecast?): Timeseries? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    var dateTimeObjectForApiUse: LocalDateTime = dateAndTime.withMinute(0).withSecond(0).withNano(0)

    var formattedDateAndTime: String = dateTimeObjectForApiUse.format(formatter)
    var correctTimeObject =  weatherData?.properties?.timeseries?.
    firstOrNull({ it.time.toString() == formattedDateAndTime })

    if(correctTimeObject != null) return correctTimeObject

    // if there were no data for the given hour, check for 6 hour interval
    dateTimeObjectForApiUse = dateTimeObjectForApiUse.minusHours((dateTimeObjectForApiUse.hour % 6).toLong())
    formattedDateAndTime = dateTimeObjectForApiUse.format(formatter)
    return weatherData?.properties?.timeseries?.
    firstOrNull({ it.time.toString() == formattedDateAndTime })
}

fun getWeatherIcon(weatherIconCode: String?): Int? {
    if(weatherIconCode == null) return null

    val weatherIconSplit: List<String> = weatherIconCode.split("_")
    Log.i("wheaterIcon", weatherIconCode)
    val isDay: Boolean = weatherIconSplit.size < 2 || weatherIconSplit[1] == "day"
    return weatherIcons[weatherIconSplit[0]]?.get(if(isDay) 0 else 1)
}