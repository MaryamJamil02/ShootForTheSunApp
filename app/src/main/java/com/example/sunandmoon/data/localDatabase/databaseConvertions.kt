package com.example.sunandmoon.data.localDatabase

import android.location.Location
import com.example.sunandmoon.data.localDatabase.dataEntities.StorableShoot
import com.example.sunandmoon.data.dataUtil.Shoot

fun storableShootToNormalShoot(storableShoot: StorableShoot): Shoot {
    return Shoot(
        id = storableShoot.uid,
        name = storableShoot.name,
        locationName = storableShoot.locationName,
        location = Location("").apply {
            latitude = storableShoot.latitude
            longitude = storableShoot.longitude
        },
        dateTime = storableShoot.dateTime,
        timeZoneOffset = storableShoot.timeZoneOffset,
        parentProductionId = storableShoot.parentProductionId,
        preferredWeather = storableShoot.preferredWeather
    )
}

fun storableShootsToNormalShoots(storableShoots: List<StorableShoot>?): List<Shoot> {
    var shootList = mutableListOf<Shoot>()

    if(storableShoots == null) return shootList

    storableShoots.forEach() { storableShoot ->
        shootList.add(
            storableShootToNormalShoot(storableShoot)
        )
    }

    return shootList
}