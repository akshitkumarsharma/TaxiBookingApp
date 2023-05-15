package com.example.taxibooking.presentation.extension

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.example.taxibooking.R
import com.example.taxibooking.presentation.enum.InfoTypeEnum
import com.example.taxibooking.presentation.util.Constants
import es.dmoral.toasty.Toasty

fun Fragment.navigateToSettings(resultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    //requireContext().startActivity(intent)
    resultLauncher.launch(intent)
}

fun Fragment.checkLocationPermissionsGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}

fun <T : Fragment> T.showError(message: String, duration: Int = Constants.DEFAULT_TOAST_DURATION, backPress: Boolean = false) {
    val notifEnabled = NotificationManagerCompat.from(this.requireContext()).areNotificationsEnabled()
    if (notifEnabled) {
        val toast = Toasty.error(this.requireContext(), message, duration, true)
        toast.setGravity(Gravity.BOTTOM, 0,70)
        toast.show()
        if (backPress) this.requireActivity().onBackPressed()
    } else {
        showInfoDialog(message, backPress, InfoTypeEnum.ERROR)
    }
}

fun <T: Fragment> T.showInfoDialog(message: String, backPress: Boolean, type: InfoTypeEnum) {

    if (this.requireActivity().isFinishing) return

    if (this.requireActivity().isDestroyed) return

    val titleResId = when (type) {
        InfoTypeEnum.ERROR -> R.string.error //"error"
        InfoTypeEnum.INFO -> R.string.info //"info"
        InfoTypeEnum.SUCCESS -> R.string.success //"success"
        InfoTypeEnum.WARNING -> R.string.warning //"warning"
    }
    MaterialDialog(this.requireContext())
        .title(titleResId)
        .message(text = message)
        //.positiveButton(R.string.apply)
        .cancelOnTouchOutside(false)
        .cancelable(false)
        .positiveButton {
            if (backPress) this.requireActivity().onBackPressed()
        }
        .show()
}

/*
if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    )
 */