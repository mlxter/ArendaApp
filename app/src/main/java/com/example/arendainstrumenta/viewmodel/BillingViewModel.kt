package com.example.arendainstrumenta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.arendainstrumenta.data.Passport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _passport = MutableStateFlow<com.example.arendainstrumenta.util.Resource<List<Passport>>>(com.example.arendainstrumenta.util.Resource.Unspecified())
    val passport = _passport.asStateFlow()

    init {
        getUserPassports()
    }

    fun getUserPassports() {
        viewModelScope.launch { _passport.emit(com.example.arendainstrumenta.util.Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("passport")
            .addSnapshotListener{ value, error ->
                if (error != null) {
                    viewModelScope.launch { _passport.emit(com.example.arendainstrumenta.util.Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val passports = value?.toObjects(Passport::class.java)
                viewModelScope.launch { _passport.emit(com.example.arendainstrumenta.util.Resource.Success(passports!!)) }
            }
    }

}