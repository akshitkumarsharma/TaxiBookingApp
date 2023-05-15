package com.example.taxibooking.presentation.screen.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taxibooking.R
import com.example.taxibooking.databinding.FragmentRegisterScreenBinding
import com.example.taxibooking.domain.model.UserType
import com.example.taxibooking.presentation.util.Constants
import com.google.android.material.chip.Chip
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterScreen : Fragment() {

    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var ccp: CountryCodePicker
    private lateinit var phoneNumber: EditText
    private val registerViewModel: RegisterViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ccp = binding.countryCodePicker
        phoneNumber = binding.phoneNumber
        ccp.registerCarrierNumberEditText(phoneNumber)

        binding.apply {
            if (getSelectedChipType() == UserType.DRIVER) {
                titleNumberPicker.visibility = View.VISIBLE
                numberPicker.visibility = View.VISIBLE
                numberPicker.minValue = 1
                numberPicker.maxValue = 10
                numberPicker.wrapSelectorWheel = false
            } else {
                titleNumberPicker.visibility = View.GONE
                numberPicker.visibility = View.GONE
            }
            verify.setOnClickListener {
                //sendVerificationCode("+${ccp.fullNumber}")
                registerViewModel.checkIsUserNumberRegistered(countryCodePicker.fullNumberWithPlus) { userCheck ->
                    if (userCheck.first) {
                        Constants.setCurrentUser(userCheck.second!!)
                        val action = RegisterScreenDirections.actionRegisterScreenToMapFragment(countryCodePicker.fullNumber)
                        findNavController().navigate(action)
                    } else {
                        getSelectedChipType()?.let {
                            val action = RegisterScreenDirections.actionRegisterScreenToPhoneVerificationScreen(countryCodePicker.fullNumber.toLong(), it, numberPicker.value)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
            chipDriver.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    titleNumberPicker.visibility = View.VISIBLE
                    numberPicker.visibility = View.VISIBLE
                    numberPicker.minValue = 1
                    numberPicker.maxValue = 5
                    numberPicker.wrapSelectorWheel = false
                } else {
                    titleNumberPicker.visibility = View.GONE
                    numberPicker.visibility = View.GONE
                }
            }
        }


        ccp.setOnCountryChangeListener {
            phoneNumber.text.clear()
        }

    }

    private fun getSelectedChipType(): UserType? {
        var selectedChip: Chip? = null
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedChip = chip
                break
            }
        }

        return if (selectedChip != null) {
            val selectedChipText = selectedChip.text
            return if (selectedChipText.toString() == requireContext().getString(R.string.i_am_a_customer)) {
                UserType.CUSTOMER
            } else {
                UserType.DRIVER
            }
        } else {
            null
        }
    }

}



