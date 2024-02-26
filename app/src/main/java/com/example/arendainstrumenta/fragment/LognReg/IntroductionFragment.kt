package com.example.arendainstrumenta.fragment.LognReg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.activities.ShoppingActivity
import com.example.arendainstrumenta.databinding.FragmentIntrodcutionBinding
import com.example.arendainstrumenta.databinding.FragmentLoginBinding
import com.example.arendainstrumenta.viewmodel.IntroductionViewModel
import com.example.arendainstrumenta.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.arendainstrumenta.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_introdcution){
    private lateinit var binding: FragmentIntrodcutionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntrodcutionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }

        binding.buttonRegisterAccountOptions.setOnClickListener{
            findNavController().navigate(R.id.action_introductionFragment_to_registerFragment)
        }

        binding.buttonLoginAccountOptions.setOnClickListener{
            findNavController().navigate(R.id.action_introductionFragment_to_loginFragment22)
        }
    }
}