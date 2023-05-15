package com.example.taxibooking.presentation.screen.map_screen

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxibooking.data.repository.TaxiBookingRepository
import com.example.taxibooking.domain.model.TaxiRequest
import com.example.taxibooking.domain.model.User
import com.example.taxibooking.domain.model.UserType
import com.example.taxibooking.presentation.extension.MutableLiveDataSingle
import com.example.taxibooking.presentation.extension.postValueOnce
import com.example.taxibooking.presentation.util.Constants
import com.example.taxibooking.presentation.util.LocationUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val taxiBookingRepository: TaxiBookingRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : ViewModel() {
    val switchStateFlow = MutableStateFlow<Boolean>(false)

    val userLocation: MutableLiveDataSingle<Location> = MutableLiveDataSingle()

    // Flow<Pair<Location, Location>>

    private val _customerDriverLocationsState: MutableStateFlow<Pair<Location?, Location?>> = MutableStateFlow(Pair(null, null))

    val customerDriverLocationsState: StateFlow<Pair<Location?, Location?>>
        get() = _customerDriverLocationsState

    var requestID: String? = null

    private val _activeUsers: MutableStateFlow<List<User>> =
        MutableStateFlow(listOf())

    val activeUsers: StateFlow<List<User>>
        get() = _activeUsers.asStateFlow()

    private var flowReference: Flow<List<User>>? = null

    fun updateUserInfo(user: User) {
        Constants.setCurrentUser(user)
        viewModelScope.launch {
            taxiBookingRepository.updateUserInfo(user)
        }
    }


    fun getActiveUsers(userType: UserType) {
        viewModelScope.launch {
            flowReference = taxiBookingRepository.getActiveUsers(userType, switchStateFlow)
            flowReference?.collect {
                val activeUsers: ArrayList<User> = arrayListOf()
                it.forEach { user ->
                    activeUsers.add(user)
                }
                filterActiveUsers(activeUsers)
            }
        }

    }

    private fun createLocationRequest(): LocationRequest {
        val defaultLocationRequest = LocationRequest.create()
        return LocationRequest.Builder(defaultLocationRequest)
            .setIntervalMillis(10000) // 10 seconds
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    fun listenForTaxiRequests(driverId: String, taxiRequest: (TaxiRequest) -> Unit) {
        taxiBookingRepository.listenForTaxiRequests(driverId) {
            taxiRequest.invoke(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = createLocationRequest()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun createTaxiRequest(driverId: String, customerId: String) {
        requestID = "${Constants.getCurrentUser().id} - $driverId"
        taxiBookingRepository.createTaxiRequest(driverId, customerId)
    }

    fun updateTaxiRequestStatus(requestId: String, status: String) {
        taxiBookingRepository.updateTaxiRequestStatus(requestId, status)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                userLocation.postValueOnce(location)
            }
        }
    }

    fun deleteDriverCustomerRelationAndRequest() {
        Log.e("DocumentID-deleteDriverCustomerRelationAndRequest", requestID.toString())
        requestID?.let {
            taxiBookingRepository.deleteDriverCustomerRelationAndRequest(it)
        }
    }

    fun getDriverByID(driverID: String, clickedDriver: (User?) -> Unit) {
        viewModelScope.launch {
            val driver = taxiBookingRepository.getDriverByID(driverID)
            clickedDriver.invoke(driver)
        }
    }

    fun createCustomerDriverRelation(customerID: String, driver: User) {
        viewModelScope.launch {
            taxiBookingRepository.createCustomerDriverRelation(customerID, driver)
        }
    }


    private fun filterActiveUsers(activeUsers: ArrayList<User>) {
        val list = arrayListOf<User>()
        activeUsers.forEach { user ->
            if (user.status) {
                list.add(user)
            }
        }
        if (userLocation.value?.peekContent()?.latitude != null && userLocation.value?.peekContent()?.longitude != null) {
            val filteredList = list.filter { user ->
                user.status && LocationUtil.isLatLngWithin15kmRadius(
                    LatLng(userLocation.value!!.peekContent().latitude, userLocation.value!!.peekContent().longitude),
                    LatLng(user.latitude, user.longitude)
                ) && user.id != Constants.getCurrentUser().email
            }
            _activeUsers.update {
                filteredList
            }
        }
    }

    fun listenDriverCustomerLocationChange(driverId: String) {
        viewModelScope.launch {
            taxiBookingRepository.listenDriverCustomerLocationChange(driverId) { locations ->
                _customerDriverLocationsState.update {
                    locations
                }
            }
        }
    }

}