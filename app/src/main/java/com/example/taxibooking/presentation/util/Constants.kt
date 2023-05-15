package com.example.taxibooking.presentation.util

import android.widget.Toast
import com.example.taxibooking.domain.model.User


object Constants {
    private lateinit var currentUser: User

    const val DEFAULT_TOAST_DURATION = Toast.LENGTH_LONG

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun getCurrentUser(): User {
        return currentUser
    }

    fun <T> convertToList(list: List<T>): ArrayList<T> {
        return ArrayList(list)
    }
}