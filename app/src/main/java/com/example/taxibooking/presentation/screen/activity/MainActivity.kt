package com.example.taxibooking.presentation.screen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taxibooking.R
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
    }
}