package com.example.taxibooking.presentation.tutorial.taxibooking

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.taxibooking.R
import com.example.taxibooking.databinding.DialogTutorialBinding
import com.example.taxibooking.domain.model.Tutorial
import com.example.taxibooking.presentation.adapter.TutorialPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator

class TaxiBookingTutorialDialog(
    private val dialogDismissed: () -> Unit
): DialogFragment() {
    private lateinit var binding: DialogTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogTutorialBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tutorialViewPager.adapter = TutorialPagerAdapter(this, createPagerContentList())
        TabLayoutMediator(binding.dots, binding.tutorialViewPager, object: TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: Tab, position: Int) {
                tab.view.isClickable = false
            }
        }).attach()
        binding.closeButton.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissed.invoke()

    }

    private fun createPagerContentList(): ArrayList<Tutorial> {
        val list = arrayListOf<Tutorial>()
        return list.apply {
            add(Tutorial(R.drawable.taxi_booking_1, "Taxi Booking is your best friend!", "With this application, customers can easily book a ride and track their driver's progress in real-time. No more waiting on the side of the road hoping for a taxi to drive by - the app brings the taxi right to your location."))
            add(Tutorial(R.drawable.taxi_booking_2, "Makes your life easier!", "Taxi mobile applications offer a convenient and hassle-free way for customers to pay for their rides. No more fumbling for cash or worrying about the meter - the app automatically charges the fare to the customer's preferred payment method."))
            add(Tutorial(R.drawable.taxi_booking_3, "Saves your time!", "Taxi mobile applications also offer features such as driver ratings and reviews, allowing customers to make informed decisions about which driver to choose. This helps to create a more transparent and trustworthy system, giving customers peace of mind when they're on the road."))
        }
    }


}