package com.example.arendainstrumenta.fragment.LognReg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.activities.ShoppingActivity
import com.example.arendainstrumenta.data.User
import com.example.arendainstrumenta.databinding.FragmentRegisterBinding
import com.example.arendainstrumenta.util.Resource
import com.example.arendainstrumenta.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    edNameReg.text.toString().trim(),
                    edFamiliaReg.text.toString().trim(),
                    edEmailLogin.text.toString().trim(),

                )
                val password = edPasswordLogin.text.toString()
                viewModel.createAccountWithEmailAndPassword(user,password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when(it){
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()

                    }
                    is Resource.Success -> {
                        Log.d("test",it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()

                    }
                    else -> Unit
                }
            }
        }
    }

}