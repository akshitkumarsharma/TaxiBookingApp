package com.example.taxibooking.presentation.screen.register

import androidx.lifecycle.ViewModel
import com.example.taxibooking.data.repository.TaxiBookingRepository
import com.example.taxibooking.domain.model.User
import com.example.taxibooking.domain.model.UserType
import com.example.taxibooking.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MailViewModel @Inject constructor(
    private val taxiBookingRepository: TaxiBookingRepository
): ViewModel() {

    fun saveUserToDatabase(
        firstName: String,
        secondName: String,
        email: String,
        type: UserType,
        phoneNumber: String,
        seats: Long = 0L
    ) {
        val user = User(
            id = "${UUID.randomUUID()}$email",
            firstName = firstName,
            secondName = secondName,
            email = email,
            type = type,
            phoneNumber = phoneNumber,
            seats = seats
        )
        Constants.setCurrentUser(user)
        taxiBookingRepository.saveUserToFirebase(
            user
        )
    }
}