package com.example.arendainstrumenta.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.arendainstrumenta.data.Product

class HomeViewPagerAdapter(
    private val fragments: List<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle

):FragmentStateAdapter(fm,lifecycle) {

    var onItemClick: ((Product) -> Unit)? = null

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]

    }

}