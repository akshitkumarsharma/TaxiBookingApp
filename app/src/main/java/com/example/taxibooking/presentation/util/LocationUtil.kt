package com.example.taxibooking.presentation.util

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

object LocationUtil {
    fun isGPSEnabled(locationManager: LocationManager): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isDistanceLessThan15km(locationOne: LatLng, locationSecond: LatLng): Boolean {
        val location1 = Location("")
        location1.latitude = locationOne.latitude
        location1.longitude = locationOne.longitude

        val location2 = Location("")
        location2.latitude = locationSecond.latitude
        location2.longitude = locationSecond.longitude

        val distance: Float = location1.distanceTo(location2)

        return distance < 150000 // 15km = 15000m
        //return true
    }
    fun isLatLngWithin15kmRadius(centerLatLng: LatLng, targetLatLng: LatLng): Boolean {
        return isDistanceLessThan15km(centerLatLng, targetLatLng)
    }
}