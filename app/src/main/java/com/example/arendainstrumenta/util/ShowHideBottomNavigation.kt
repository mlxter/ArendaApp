package com.example.arendainstrumenta.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.arendainstrumenta.activities.ShoppingActivity
import com.example.arendainstrumenta.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.arendainstrumenta.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.arendainstrumenta.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}