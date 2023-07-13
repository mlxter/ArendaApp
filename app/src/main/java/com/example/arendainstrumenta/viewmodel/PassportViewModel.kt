package com.example.arendainstrumenta.viewmodel

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arendainstrumenta.data.Passport
import com.example.arendainstrumenta.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {


    private val _addNewPassport = MutableStateFlow<Resource<Passport>>(Resource.Unspecified())
    val addNewPassport = _addNewPassport.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addPassport(passport: Passport){
        val validateInputs = validateInputs(passport)

        if (validateInputs) {
            viewModelScope.launch { _addNewPassport.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("passport").document()
                .set(passport).addOnSuccessListener {
                    viewModelScope.launch { _addNewPassport.emit(Resource.Success(passport)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewPassport.emit(Resource.Error(it.message.toString())) }
                }
        }else{
            viewModelScope.launch {
                _error.emit("Все поля обязательны для заполнения")
            }

        }

    }

    private fun validateInputs(passport: Passport): Boolean {
        return passport.passportFIO.trim().isNotEmpty() &&
                passport.seria.trim().isNotEmpty() &&
                passport.nomer.trim().isNotEmpty() &&
                passport.data.trim().isNotEmpty() &&
                passport.kod.trim().isNotEmpty() &&
                passport.vidan.trim().isNotEmpty()
    }
}