package com.example.arendainstrumenta.fragment.LognReg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.databinding.FragmentIntrodcutionBinding
import com.example.arendainstrumenta.databinding.FragmentLoginBinding

class IntroductionFragment: Fragment(R.layout.fragment_introdcution){
    private lateinit var binding: FragmentIntrodcutionBinding

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

        binding.buttonRegisterAccountOptions.setOnClickListener{
            findNavController().navigate(R.id.action_introductionFragment_to_registerFragment)
        }

        binding.buttonLoginAccountOptions.setOnClickListener{
            findNavController().navigate(R.id.action_introductionFragment_to_loginFragment22)
        }
    }
}