package com.example.arendainstrumenta.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.Resource
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.activities.ShoppingActivity
import com.example.arendainstrumenta.data.CartProduct
import com.example.arendainstrumenta.databinding.FragmentProductDetailsBinding
import com.example.arendainstrumenta.util.hideBottomNavigationView
import com.example.arendainstrumenta.viewmodel.DetailsViewModel
import com.example.kleine.adapters.recyclerview.SizeAdapter
import com.example.kleine.adapters.viewpager.ViewPager2Images
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizeAdapter by lazy { SizeAdapter() }
    private var selectedSize: String?=null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        SetupSizesRv()
        setupViewpager()


        binding.imageClose.setOnClickListener{
            findNavController().navigateUp()
        }

        sizeAdapter.onItemClick = {
            selectedSize = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product,1,selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is com.example.arendainstrumenta.util.Resource.Loading ->{
                        binding.buttonAddToCart.startAnimation()
                    }

                    is com.example.arendainstrumenta.util.Resource.Success ->{
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }

                    is com.example.arendainstrumenta.util.Resource.Error ->{
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }


        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "₽ ${product.price}"
            tvProductDescription.text = product.description
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.sizes?.let { sizeAdapter.differ.submitList(it) }

    }

    private fun setupViewpager() {
        binding.apply {
            viewpagerHome.adapter = viewPagerAdapter
        }
    }

    private fun SetupSizesRv() {
        binding.rvSrok.apply {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}