package com.example.taxibooking.presentation.screen.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taxibooking.R
import com.example.taxibooking.databinding.FragmentMailScreenBinding
import com.example.taxibooking.presentation.extension.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MailScreen : Fragment() {

    private lateinit var binding: FragmentMailScreenBinding
    private val mailViewModel: MailViewModel by viewModels({ requireActivity() })
    private val args: MailScreenArgs by navArgs()
    private lateinit var firstName: String
    private lateinit var secondName: String
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMailScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.next.setOnClickListener {
            binding.apply {
                this@MailScreen.firstName = firstName.text.toString()
                this@MailScreen.secondName = secondName.text.toString()
                this@MailScreen.email = email.text.toString()
            }
            signUp()
        }
    }

    // 37.421982756980334, -122.08448813127144
    private fun signUp() {
        val phoneNumber = args.phoneNumber
        val userType = args.userType
        val seats = args.seats
        if (firstName.isNotBlank() && secondName.isNotBlank() && email.isNotBlank()) {
            mailViewModel.saveUserToDatabase(firstName, secondName, email, userType, phoneNumber, seats.toLong())
            findNavController().navigate(R.id.action_mailScreen_to_mapFragment)
        } else {
            showError("You must fill all the empty areas")
        }
    }

}