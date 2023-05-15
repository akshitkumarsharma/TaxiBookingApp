package com.example.taxibooking.domain.model

import com.google.firebase.Timestamp

data class TaxiRequest(
    val customerID: String,
    val driverID: String,
    val status: String,
    val timeStamp: Timestamp,
    val documentID: String
)
