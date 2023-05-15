package com.example.taxibooking.presentation.screen.map_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.taxibooking.R
import com.example.taxibooking.databinding.BottomSheetLayoutBinding
import com.example.taxibooking.domain.model.TaxiRequest
import com.example.taxibooking.presentation.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CallTaxiBottomSheetFragment(
    private val driverID: String,
    private val taxiRequest: (TaxiRequest) -> Unit
):  BottomSheetDialogFragment(){

    private lateinit var binding: BottomSheetLayoutBinding
    private val mapViewModel: MapViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapViewModel.getDriverByID(driverID) { driverFromDatabase ->
            driverFromDatabase?.let {
                binding.taxiSeats.text = it.seats.toString()
            }
            binding.callTaxiButton.setOnClickListener {
                val customerId = Constants.getCurrentUser().id // Implement this function to get the current user's ID
                mapViewModel.createTaxiRequest(driverID, customerId)
                mapViewModel.listenForTaxiRequests(driverID) {
                    taxiRequest.invoke(it)
                }
                this.dismiss()
            }
        }

    }

    companion object {
        private const val BOTTOM_SHEET_TAG = "CallTaxiBottomSheetFragment"
        fun show(fragmentManager: FragmentManager, driverID: String, taxiRequest: (TaxiRequest) -> Unit) {
            val bottomSheet = CallTaxiBottomSheetFragment(driverID, taxiRequest)
            bottomSheet.setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
            bottomSheet.show(fragmentManager, BOTTOM_SHEET_TAG)
        }
    }
}