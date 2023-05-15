package com.example.taxibooking.presentation.screen.map_screen

import android.Manifest
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxibooking.R
import com.example.taxibooking.databinding.BottomSheetLayoutBinding
import com.example.taxibooking.databinding.FragmentMapBinding
import com.example.taxibooking.domain.model.TaxiRequest
import com.example.taxibooking.domain.model.User
import com.example.taxibooking.domain.model.UserMarkerInfo
import com.example.taxibooking.domain.model.UserType
import com.example.taxibooking.presentation.extension.checkLocationPermissionsGranted
import com.example.taxibooking.presentation.extension.navigateToSettings
import com.example.taxibooking.presentation.extension.observeLiveDataSingle
import com.example.taxibooking.presentation.extension.showError
import com.example.taxibooking.presentation.screen.adapter.ProfilesAdapter
import com.example.taxibooking.presentation.tutorial.taxibooking.TaxiBookingTutorialDialog
import com.example.taxibooking.presentation.util.Constants
import com.example.taxibooking.presentation.util.LocationUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val mapViewModel: MapViewModel by viewModels({ requireActivity() })
    private val args: MapFragmentArgs by navArgs()
    private val CIRCLE_RADIUS = 15000f // 15km = 15000m
    private var circle: Circle? = null
    private val markerMap = HashMap<UserMarkerInfo, Marker>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profilesAdapter: ProfilesAdapter
    private lateinit var toggle: ActionBarDrawerToggle

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 0) {
            if (LocationUtil.isGPSEnabled(locationManager)) {
                Toast.makeText(requireContext(), "GPS Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "GPS Not Enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater)
        registerLauncher()
        return binding.root
    }


    fun createUsers(): ArrayList<User> {
        return arrayListOf(
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
            User(
                ",asd",
                "asdads",
                "asdads",
                "asdasd",
                UserType.DRIVER,
                false,
                phoneNumber = "asdasd"
            ),
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.drawerRecyclerView
        drawerLayout = binding.drawerLayout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        profilesAdapter = ProfilesAdapter(createUsers())
        recyclerView.adapter = profilesAdapter

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolBar)
        toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.toolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val dialog = TaxiBookingTutorialDialog() {
            checkLocationPermission()
        }
        dialog.show(parentFragmentManager, "TaxiBooking")

        mapView = binding.map
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)
        binding.zoomIcon.setOnClickListener {
            if (this.checkLocationPermissionsGranted() && LocationUtil.isGPSEnabled(locationManager)) {
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lastLocation?.let {
                    val lastUserLocation = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                }

            } else {
                checkLocationPermission()
            }

        }

        binding.stopIcon.setOnClickListener {
            mapViewModel.requestID?.let {
                mapViewModel.updateTaxiRequestStatus(it, "ended")
            }
        }

        binding.statusSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            mapViewModel.updateUserInfo(Constants.getCurrentUser().copy(status = isChecked))
            mapViewModel.switchStateFlow.value = isChecked
            if (this.checkLocationPermissionsGranted() && LocationUtil.isGPSEnabled(locationManager) && isChecked) {
                mapViewModel.startLocationUpdates()
                if (binding.statusSwitch.isChecked && checkLocationPermissionsGranted()) {
                    disableMapScrolling()
                    binding.radarAnimation.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewLifecycleOwner.lifecycleScope.launch {
                            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                launch {

                                    mapViewModel.activeUsers.collect {
                                        handleUIState(it)
                                    }
                                }
                            }
                        }

                    }, 5000L)

                    viewLifecycleOwner.lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            launch {
                                mapViewModel.customerDriverLocationsState.collect {
                                    // draw line but the billing price
                                }
                            }
                        }
                    }
                } else {
                    showError("You must enable status that top left corner and also grand for location permission")
                }
            } else {
                showError("You must enable status that top left corner and also grand for location permission")
                circle?.remove()
                mapViewModel.stopLocationUpdates()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (Constants.getCurrentUser().type == UserType.DRIVER && !Constants.getCurrentUser().travelStatus) {
            mapViewModel.listenForTaxiRequests(Constants.getCurrentUser().id) {
                showDriverDialog(it)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMapLoadedCallback {
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }



        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {

            override fun onProviderDisabled(provider: String) {
                print("x")
            }

            override fun onProviderEnabled(provider: String) {
                print("x")
            }

            override fun onLocationChanged(location: Location) {
                print("x")
            }
        }

        if (Constants.getCurrentUser().type == UserType.DRIVER) {
            mapViewModel.getActiveUsers(UserType.CUSTOMER)
        } else {
            mapViewModel.getActiveUsers(UserType.DRIVER)
        }

        observeLiveDataSingle(mapViewModel.userLocation) { location ->
            location.let {
                it.let { locationInner ->
                    val latLng = LatLng(locationInner.latitude, locationInner.longitude)
                    if (Constants.getCurrentUser().type == UserType.DRIVER) {
                        addCustomMarker(
                            mMap,
                            LatLng(it.latitude, it.longitude),
                            R.drawable.ic_taxi,
                            userMarkerInfo = UserMarkerInfo(Constants.getCurrentUser().id, UserType.DRIVER)
                        )
                    } else {
                        addCustomMarker(
                            mMap,
                            LatLng(it.latitude, it.longitude),
                            R.drawable.ic_human,
                            userMarkerInfo = UserMarkerInfo(Constants.getCurrentUser().id, UserType.CUSTOMER)
                        )
                    }
                    mapViewModel.updateUserInfo(Constants.getCurrentUser().copy(latitude = it.latitude, longitude = it.longitude))

                    circle?.remove()
                    val circleOptions = CircleOptions()
                        .center(latLng)
                        .radius(CIRCLE_RADIUS.toDouble())
                        .strokeWidth(2f)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(70, 255, 0, 0))
                    circle = mMap.addCircle(circleOptions)

                    val valueAnimator = ValueAnimator.ofFloat(2f, 20f, 2f)
                    valueAnimator.duration = 5000 // Animation duration in milliseconds
                    valueAnimator.repeatCount = ValueAnimator.INFINITE // Repeat indefinitely
                    valueAnimator.repeatMode = ValueAnimator.RESTART // Restart the animation when it ends
                    valueAnimator.addUpdateListener { animator ->
                        val animatedValue = animator.animatedValue as Float
                        circle?.strokeWidth = animatedValue
                    }
                    valueAnimator.start()

                }
                mapViewModel.updateUserInfo(Constants.getCurrentUser().copy(latitude = it.latitude, longitude = it.longitude))
            }
        }
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerClickListener(this)
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (!LocationUtil.isGPSEnabled(locationManager)) {
                        mapViewModel.stopLocationUpdates()
                        this.navigateToSettings(resultLauncher)
                    } else {
                        mapViewModel.startLocationUpdates()
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                        val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                        }
                        mMap.isMyLocationEnabled = true
                    }
                }
            } else {
                //this.navigateToSettings(resultLauncher)
                mapViewModel.stopLocationUpdates()
                Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        onTaxiMarkerClicked(marker)
        return true
    }


    private fun onTaxiMarkerClicked(marker: Marker) {
        val driverId = getClickedMarkerID(marker)
        mapViewModel.getDriverByID(driverId) {
            if ((marker.tag as UserMarkerInfo).userType == UserType.DRIVER && Constants.getCurrentUser().type == UserType.CUSTOMER && it?.travelStatus == false) {
                showBottomSheet(marker, driverId)
            }
        }
    }

    override fun onMapClick(location: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

    }

    private fun showFeedbackDialog() {
        val questions = if (Constants.getCurrentUser().type == UserType.DRIVER) {
            arrayOf(
                "Change my mind",
                "Customer is late",
                "I got lift",
                "Customer too far",
                "Customer ask to cancel",
                "Other"
            )
        } else {
            arrayOf(
                "Change my mind",
                "Driver is late",
                "I got lift",
                "Driver too far",
                "Driver ask to cancel",
                "Other"
            )
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Feedback")
        builder.setItems(questions) { _, which ->
            val selectedQuestion = questions[which]
            showCommentDialog(selectedQuestion)
        }
        builder.setOnDismissListener {
            binding.stopIcon.visibility = View.GONE
            mapViewModel.deleteDriverCustomerRelationAndRequest()
        }
        builder.create().show()
    }

    private fun showCommentDialog(question: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(question)
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Submit") { _, _ ->
            val comment = input.text.toString()
            //saveFeedback(question, comment)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun showBottomSheet(marker: Marker, driverId: String) {


        CallTaxiBottomSheetFragment.show(
            requireActivity().supportFragmentManager,
            driverId,
        ) {
            if (it.status == "accepted" || it.status == "declined") {
                if (it.status == "accepted") binding.stopIcon.visibility = View.VISIBLE
                else binding.stopIcon.visibility = View.GONE
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Taxi Request")
                builder.setMessage("Taxi Request is updated: ${it.status}")
                val alertDialog = builder.create()
                if (it.status == "accepted") {
                    val location = mapViewModel.listenDriverCustomerLocationChange(driverId)
                }
                alertDialog.show()
            } else if (it.status == "ended") {
                showFeedbackDialog()
            }

        }


    }


    private var lastTaxiRequest: TaxiRequest? = null
    private fun showDriverDialog(taxiRequest: TaxiRequest) {
        if (Constants.getCurrentUser().type == UserType.DRIVER && taxiRequest.status == "pending") {
            lastTaxiRequest = taxiRequest
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Taxi Request")
            builder.setMessage("A customer wants to call you. Do you accept?")
            builder.setPositiveButton("Accept") { dialog, _ ->
                mapViewModel.createCustomerDriverRelation(taxiRequest.customerID, Constants.getCurrentUser())
                mapViewModel.listenDriverCustomerLocationChange(taxiRequest.driverID)
                mapViewModel.updateTaxiRequestStatus(taxiRequest.documentID, "accepted")
                binding.stopIcon.visibility = View.VISIBLE
                dialog.dismiss()
            }
            builder.setNegativeButton("Decline") { dialog, _ ->
                mapViewModel.updateTaxiRequestStatus(taxiRequest.documentID, "declined")
                binding.stopIcon.visibility = View.GONE
                dialog
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        } else if (taxiRequest.status == "ended") {
            showFeedbackDialog()
        }

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            requireContext().startActivity(intent)
        }
        return false
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.root, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
            }
        }
    }

    private fun handleUIState(activeUsers: List<User>) {
        enableMapScrolling()
        binding.radarAnimation.visibility = View.GONE
        val filteredList = activeUsers.filter { it.type == UserType.DRIVER }
        profilesAdapter.setUserList(Constants.convertToList(filteredList))
        activeUsers.forEach {
            if (it.type == UserType.CUSTOMER) {
                addCustomMarker(
                    mMap,
                    LatLng(it.latitude, it.longitude),
                    R.drawable.ic_human,
                    userMarkerInfo = UserMarkerInfo(it.id, it.type)
                )
            } else {
                addCustomMarker(
                    mMap,
                    LatLng(it.latitude, it.longitude),
                    R.drawable.ic_taxi,
                    userMarkerInfo = UserMarkerInfo(it.id, it.type)
                )
            }

        }
    }

    private fun addCustomMarker(
        map: GoogleMap,
        position: LatLng,
        drawableResId: Int,
        anchorX: Float = 0.5f,
        anchorY: Float = 1.0f,
        userMarkerInfo: UserMarkerInfo
    ) {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(requireContext(), drawableResId))
        val markerOptions = MarkerOptions()
            .position(position)
            .anchor(anchorX, anchorY)
            .icon(bitmapDescriptor)

        deleteDuplicateMarker(userMarkerInfo)
        val marker = map.addMarker(markerOptions)
        marker?.tag = userMarkerInfo
        markerMap[marker!!.tag as UserMarkerInfo] = marker
    }

    private fun getClickedMarkerID(marker: Marker): String {
        val userMarkerInfo = marker.tag as UserMarkerInfo
        return userMarkerInfo.userId
    }

    private fun getBitmapFromDrawable(context: Context, drawableResId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: throw IllegalArgumentException("Invalid drawable resource ID")
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun disableMapScrolling() {
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    private fun enableMapScrolling() {
        mMap.uiSettings.isScrollGesturesEnabled = true
    }

    private fun deleteDuplicateMarker(userMarkerInfo: UserMarkerInfo) {
        markerMap[userMarkerInfo]?.remove()
    }


}