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
import com.bumptech.glide.load.engine.Resource
import com.example.arendainstrumenta.data.Passport
import com.example.arendainstrumenta.databinding.FragmentPassportBinding
import com.example.arendainstrumenta.viewmodel.PassportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest



@AndroidEntryPoint
class PassportFragment: Fragment() {
    private lateinit var binding: FragmentPassportBinding
    val viewModel by viewModels<PassportViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewPassport.collectLatest {
                when (it) {
                    is com.example.arendainstrumenta.util.Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is com.example.arendainstrumenta.util.Resource.Success ->{
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is com.example.arendainstrumenta.util.Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassportBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        binding.apply {
            buttonSave.setOnClickListener {
                val passportFIO = edAddressTitle.text.toString()
                val passportvidan = edFullName.text.toString()
                val passportkod = edStreet.text.toString()
                val passportdata = edPhone.text.toString()
                val passportseria = edCity.text.toString()
                val passportnomer = edState.text.toString()
                val passport = Passport(passportFIO, passportvidan, passportkod, passportdata, passportseria, passportnomer)

                viewModel.addPassport(passport)
            }
        }
    }
}