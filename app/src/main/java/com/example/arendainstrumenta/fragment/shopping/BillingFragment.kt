package com.example.arendainstrumenta.fragment.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.adapters.BillingProductsAdapter
import com.example.arendainstrumenta.adapters.PassportAdapter
import com.example.arendainstrumenta.data.CartProduct
import com.example.arendainstrumenta.data.Passport
import com.example.arendainstrumenta.data.order.Order
import com.example.arendainstrumenta.data.order.OrderStatus
import com.example.arendainstrumenta.databinding.FragmentBillingBinding
import com.example.arendainstrumenta.util.HorizantalItemDecoration
import com.example.arendainstrumenta.util.Resource
import com.example.arendainstrumenta.viewmodel.BillingViewModel
import com.example.arendainstrumenta.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val passportAdapter by lazy { PassportAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f


    private var selectedPassports: Passport? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBillingProductsRv()
        setupPassportRv()


        binding.imageAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_billingFragment_to_passportFragment)
        }

        passportAdapter.onClick = {
            selectedPassports = it
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.passport.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        passportAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error ->{
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonPlaceOrder.stopAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Заказ оформлен",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)


        binding.tvTotalPrice.text = "₽ $totalPrice"


        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedPassports == null){
                Toast.makeText(requireContext(), "Выбирите паспорт", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Заказ товара")
            setMessage("Оформить заказ?")
            setNegativeButton("Отмена") { dialog,_ ->
                dialog.dismiss()
            }
            setPositiveButton("Да"){ dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedPassports!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupPassportRv() {
        binding.rvPassport.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = passportAdapter
            addItemDecoration(HorizantalItemDecoration())
        }
    }

    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizantalItemDecoration())
        }
    }
}