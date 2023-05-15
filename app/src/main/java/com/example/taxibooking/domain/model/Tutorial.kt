package com.example.taxibooking.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tutorial(
    @DrawableRes var image: Int,
    var title: String,
    var description: String
): Parcelable
