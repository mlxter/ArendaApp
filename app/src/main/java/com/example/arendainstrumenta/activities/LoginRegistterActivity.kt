package com.example.arendainstrumenta.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arendainstrumenta.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegistterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }
}