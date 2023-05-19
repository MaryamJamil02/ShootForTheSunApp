package com.example.sunandmoon.viewModel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunandmoon.data.DataSource
import com.example.sunandmoon.data.TableUIState
import com.example.sunandmoon.getSunRiseNoonFall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class TableViewModel : ViewModel() {

    private val dataSource = DataSource()

    private val _tableUIState = MutableStateFlow(
        TableUIState(
            apiDateTableList = listOf(),
            calculationsDateTableList = listOf("", "", "", "", "", "", "", "", "", "", "", ""),
            locationSearchQuery = "UiO",
            locationSearchResults = listOf(),
            location = Location("").apply {
                latitude = 59.943965
                longitude = 10.7178129
            },
            chosenDate = LocalDateTime.now(),
            chosenSunType = "Sunrise",
            timeZoneOffset = 2.0,
            timezone_id = "Europe/Oslo",
            offsetStringForApi = "02:00",
            timeZoneListTableScreen = listOf("", "", "", "", "", "", "", "", "", "", "", "")
        )
    )

    val tableUIState: StateFlow<TableUIState> = _tableUIState.asStateFlow()

    init {
        loadSunInformation()
    }

    fun loadSunInformation(){
        viewModelScope.launch {

            var sunRiseTime: String
            var sunSetTime: String
            var solarNoon: String

            val apiDateTableList = mutableListOf<String>()
            val calculationsDateTableList = mutableListOf<String>()
            val timeZoneListTableScreen = mutableListOf<String>()

            val sameDaysList = getSameDaysInYear(tableUIState.value.chosenDate.toLocalDate())
            val sameDaysListFromJanuary = getSameDaysInYearFromJanuary(tableUIState.value.chosenDate.toLocalDate())


            for (date in sameDaysListFromJanuary.sorted()){
                val offsetToFindSign = findOffset(tableUIState.value.timezone_id, date.toString())
                var offsetString: String

                Log.d("offsetToFindSign", offsetToFindSign.toString())


                val offsetList = offsetToFindSign.toString().split(".")
                Log.d("offsetList", offsetList.toString())

                val hours = offsetList[0]

                Log.d("hours", hours)
                val hoursWithOutSign = hours.split("")[2]
                Log.d("hoursWithOutSign", hoursWithOutSign)
                val minutes = offsetList[1]
                Log.d("minutesDecimal", minutes)

                var convertToMinutes = convertToMinutesFunction(minutes)
                Log.d("convertToMinutes", convertToMinutes)


                if (offsetToFindSign in -9.9..9.9) {

                    //if it is hours and minutes
                    if (convertToMinutes == "0") {


                        //if negative and decimal
                        offsetString = if(hours.split("")[1] == "-") {
                            "-0" + hoursWithOutSign  + ":00"
                        }

                        //not negative
                        else{
                            "+0" + hours + ":00"
                        }
                    }

                    //not minutes, only hours
                    else{
                        //if negative
                        offsetString = if(offsetToFindSign.toString().split("")[1] == "-") {

                            "-0" + hours + ":" + convertToMinutes
                        }
                        //not negative
                        else{
                            "+0" + hours + ":" + convertToMinutes

                        }
                    }


                    //same as above, but checks for values over 10 og under -10
                } else if (offsetToFindSign in 10.0..25.0 || offsetToFindSign in -25.0..-10.0) {

                    if (convertToMinutes == "0") {

                        offsetString = if (offsetToFindSign.toString().split("")[1] == "-") {
                            "-" + hours + ":00"
                        }

                        else {
                            "+" + hours + ":00"
                        }

                    }

                    else{
                        offsetString = if(offsetToFindSign.toString().split("")[1] == "-") {
                            "-" + hours + ":" + convertToMinutes
                        }
                        else{
                            "+" + hours + ":" + convertToMinutes
                        }
                    }

                }

                else {
                    val offsetFromUiState = tableUIState.value.timeZoneOffset.toString().split(".")[0]
                    offsetString = "+0" + offsetFromUiState + ":00"
                }

                //update UiState
                setTimeZoneStringApi(offsetString)
                timeZoneListTableScreen.add(offsetString)

                Log.d("offsetString", offsetString + date,)



                val stringDate = date.toString() + " 12:00"
                Log.d("stringDate", stringDate)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val dateTime = LocalDateTime.parse(stringDate, formatter)

                val calculationSunTime = getSunRiseNoonFall(dateTime, tableUIState.value.timeZoneOffset, tableUIState.value.location)

                if (tableUIState.value.chosenSunType == "Sunrise"){
                    calculationsDateTableList.add(calculationSunTime[0])
                }

                if (tableUIState.value.chosenSunType == "SolarNoon"){
                    calculationsDateTableList.add(calculationSunTime[1])
                }

                if (tableUIState.value.chosenSunType == "Sunset"){
                    calculationsDateTableList.add(calculationSunTime[2])
                }

            }

            for (date in sameDaysList.sorted()){
                println(tableUIState.value.timeZoneListTableScreen[(date.toString().split("-")[1]).toInt()-1])



                if (tableUIState.value.chosenSunType == "Sunrise"){
                    sunRiseTime = dataSource.fetchSunrise3Data("sun", tableUIState.value.location.latitude, tableUIState.value.location.longitude, date.toString(), tableUIState.value.offsetStringForApi).properties.sunrise.time
                    apiDateTableList.add(sunRiseTime)
                }

                if (tableUIState.value.chosenSunType == "SolarNoon"){
                    solarNoon = dataSource.fetchSunrise3Data("sun", tableUIState.value.location.latitude, tableUIState.value.location.longitude, date.toString(), tableUIState.value.offsetStringForApi).properties.solarnoon.time
                    apiDateTableList.add(solarNoon)
                }

                if (tableUIState.value.chosenSunType == "Sunset"){
                    sunSetTime = dataSource.fetchSunrise3Data("sun", tableUIState.value.location.latitude, tableUIState.value.location.longitude, date.toString(), tableUIState.value.offsetStringForApi).properties.sunset.time
                    apiDateTableList.add(sunSetTime)
                }

            }



            _tableUIState.update{currentState ->
                currentState.copy(
                    apiDateTableList = apiDateTableList,
                    calculationsDateTableList = calculationsDateTableList,
                    timeZoneListTableScreen = timeZoneListTableScreen

                )
            }
        }
    }

    private fun getSameDaysInYear(date: LocalDate): List<LocalDate> {

        val daysInMonth = listOf(31, 28, 31, 30, 31, 30, 31, 31 ,30, 31, 30, 31)
        val year = date.year
        val sameDays = mutableListOf<LocalDate>()

        for (month in 1..12) {
            val daysInThisMonth = daysInMonth[month-1]
            val dayToUse = daysInThisMonth.coerceAtMost(date.dayOfMonth)
            if (month < date.month.value){
                val nextYear = year+1
                val sameDayOfMonth = LocalDate.of(nextYear, month, dayToUse)
                sameDays.add(sameDayOfMonth)
            }

            else{
                val sameDayOfMonth = LocalDate.of(year, month, dayToUse)
                sameDays.add(sameDayOfMonth)
            }
        }

        return sameDays.sorted()
    }
    private fun getSameDaysInYearFromJanuary(date: LocalDate): List<LocalDate> {

        val daysInMonth = listOf(31, 28, 31, 30, 31, 30, 31, 31 ,30, 31, 30, 31)

        val year = date.year
        val sameDays = mutableListOf<LocalDate>()

        for (month in 1..12) {
            val daysInThisMonth = daysInMonth[month-1]
            val dayToUse = daysInThisMonth.coerceAtMost(date.dayOfMonth)
            val sameDayOfMonth = LocalDate.of(year, month, dayToUse)
            sameDays.add(sameDayOfMonth)

        }

        return sameDays.sorted()
    }


    fun setSunType(sunType: String){
        _tableUIState.update{ currentState ->
            currentState.copy(
                chosenSunType = sunType,
            )
        }
    }
    fun setTimeZoneStringApi(offsetStringForApi: String){
        _tableUIState.update { currentState ->
            currentState.copy(
                offsetStringForApi = offsetStringForApi,

            )
        }
    }

    fun setTheSunset(month: String): List<String>{

        val sortedElements1 = tableUIState.value.timeZoneListTableScreen.subList(4, 12).sorted()
// Sort elements from index 1 to 3
        val sortedElements2 = tableUIState.value.timeZoneListTableScreen.subList(1, 4).sorted()

// Combine the sorted elements into a new list
        return sortedElements1 + sortedElements2
    }



    fun setLocationSearchQuery(inputQuery: String) {
        _tableUIState.update { currentState ->
            currentState.copy(
                locationSearchQuery = inputQuery
            )
        }
    }

    fun loadLocationSearchResults(query: String) {
        viewModelScope.launch {
            try {
                val locationSearchResults = dataSource.fetchLocationSearchResults(query, 10)

                _tableUIState.update { currentState ->
                    currentState.copy(
                        locationSearchResults = locationSearchResults
                    )
                }

            } catch (e: Throwable) {
                Log.d("error", "uh oh")
            }
        }
    }


    private fun setTimeZoneOffset(timeZoneOffset: Double, timezone_id: String) {
        _tableUIState.update { currentState ->
            currentState.copy(
                timeZoneOffset = timeZoneOffset,
                timezone_id = timezone_id
            )
        }
    }

    fun setCoordinates(newLatitude: Double, newLongitude: Double, setTimeZoneOffset: Boolean) {
        viewModelScope.launch {
            if(setTimeZoneOffset) {
                val locationTimeZoneOffsetResult = dataSource.fetchLocationTimezoneOffset(newLatitude, newLongitude)
                setTimeZoneOffset(locationTimeZoneOffsetResult.offset, locationTimeZoneOffsetResult.timezone_id)
            }


            _tableUIState.update { currentState ->
                currentState.copy(
                    location = Location("").apply {
                        latitude = newLatitude
                        longitude = newLongitude
                    }
                )
            }
            loadSunInformation()
        }
    }
    fun updateDay(day: Int){
        setNewDate(_tableUIState.value.chosenDate.year, _tableUIState.value.chosenDate.monthValue, day)

    }

    fun updateMonth(month: Int, maxDate: Int){
        var day = _tableUIState.value.chosenDate.dayOfMonth
        if (maxDate < day){
            day = maxDate
        }
        setNewDate(_tableUIState.value.chosenDate.year, month, day)
    }

    fun updateYear(year: Int){
        setNewDate(year, _tableUIState.value.chosenDate.monthValue, _tableUIState.value.chosenDate.dayOfMonth)
    }

    private fun setNewDate(year: Int, month: Int, day: Int){
        _tableUIState.update { currentState ->
            currentState.copy(
                chosenDate = LocalDateTime.of(year, month, day, 12, 0, 0)
            )

        }
        loadSunInformation()
    }

    private fun findOffset(location: String, date: String): Double {
        val dateTime = LocalDateTime.of(convertStringToLocalDate(date), LocalDateTime.now().toLocalTime())
        val zoneId = ZoneId.of(location)
        val zoneOffset = zoneId.rules.getOffset(dateTime)

        val offsetHours = zoneOffset.totalSeconds / 3600
        val offsetMinutes = (zoneOffset.totalSeconds % 3600) / 60

        val offset = offsetHours + (offsetMinutes.toDouble() / 60)
        return offset

    }


    private fun convertToMinutesFunction(minutes: String): String{
        var doubleMinutes = ("0." + minutes).toDouble()
        var minutes = doubleMinutes * 60
        return minutes.toString().split(".")[0]



    }

    private fun convertStringToLocalDate(dateString: String): LocalDate {

        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
    }








}



