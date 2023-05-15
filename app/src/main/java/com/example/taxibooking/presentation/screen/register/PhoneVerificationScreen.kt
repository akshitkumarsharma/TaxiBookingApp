package com.example.taxibooking.presentation.screen.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taxibooking.databinding.FragmentPhoneVerificationScreenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PhoneVerificationScreen : Fragment() {

    private lateinit var binding: FragmentPhoneVerificationScreenBinding
    private val args: PhoneVerificationScreenArgs by navArgs()
    private val registerViewModel: RegisterViewModel by viewModels({requireActivity()})
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneVerificationScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val phoneNumber = "+${args.phoneNumber}"
        val userType = args.userType
        val seats = args.seats
        binding.registerPhoneNumber.text = phoneNumber
        registerViewModel.isLoginSuccessful.observe(viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                Log.e("PHONEEE", "Başarılı")
                Snackbar.make(
                    requireContext(),
                    view,
                    "Başarılı",
                    Snackbar.LENGTH_SHORT
                ).show()
                val action =
                    PhoneVerificationScreenDirections.actionPhoneVerificationScreenToMailScreen(phoneNumber, userType, seats)
                this.findNavController().navigate(action)
            } else {
                Log.e("PHONEEE", "Başarısız")
                Snackbar.make(
                    requireContext(),
                    view,
                    "Başarısız",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                registerViewModel.signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                registerViewModel.verificationID = verificationId
                registerViewModel.resendToken = token
            }
        }
        registerViewModel.startPhoneNumberVerification(requireActivity(), phoneNumber, callbacks)
        binding.next.setOnClickListener {
            val code = binding.firstDigit.text.toString() + binding.secondDigit.text.toString() + binding.thirdDigit.text.toString() + binding.fourthDigit.text.toString() + binding.fifthDigit.text.toString() + binding.sixthDigit.text.toString()
            registerViewModel.verifyPhoneNumberWithCode(code)
        }
    }









}