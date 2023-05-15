package com.example.taxibooking.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String,
    val secondName: String,
    val email: String,
    val type: UserType,
    val status: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phoneNumber: String,
    val travelStatus: Boolean = false,
    val seats: Long = 0L
): Parcelable
@Parcelize
enum class UserType: Parcelable {
    DRIVER, CUSTOMER
}
