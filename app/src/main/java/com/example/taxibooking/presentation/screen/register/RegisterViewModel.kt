package com.example.taxibooking.presentation.screen.register

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxibooking.data.repository.TaxiBookingRepository
import com.example.taxibooking.domain.model.User
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val taxiBookingRepository: TaxiBookingRepository
) : ViewModel() {
    var verificationID: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var isLoginSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
        private set

    fun verifyPhoneNumberWithCode(code: String) {
        if (verificationID.isNotBlank()) {
            val credential = PhoneAuthProvider.getCredential(verificationID, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser
                    if (isNewUser == true) {
                        // first time
                    } else {
                        // user already signed in before
                    }
                    val user = task.result?.user
                    isLoginSuccessful.value = true

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // the verification code entered was invalid
                        print("x")
                    }
                    isLoginSuccessful.value = false
                }
            }
    }

    fun startPhoneNumberVerification(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun checkIsUserNumberRegistered(phoneNumber: String, checkUserCallback: (Pair<Boolean, User?>) -> Unit) {
        viewModelScope.launch {
            val user = taxiBookingRepository.getUserByPhoneNumber(phoneNumber)
            if (user == null) {
                Log.e("USERR", user.toString())
                checkUserCallback.invoke(Pair(false, null))
            }
            else {
                Log.e("USERR", user.toString())
                checkUserCallback.invoke(Pair(true, user))
            }
        }
    }




}