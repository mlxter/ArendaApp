package com.example.arendainstrumenta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arendainstrumenta.data.Product
import com.example.arendainstrumenta.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _Product = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val Products: StateFlow<Resource<List<Product>>> = _Product


    init{
        fetchProduct()
    }


    fun fetchProduct(){
        viewModelScope.launch {
            _Product.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Инструмент").limit(10).get().addOnSuccessListener { result ->
                val ProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _Product.emit(Resource.Success(ProductList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _Product.emit(Resource.Error(it.message.toString()))
                }
            }

    }
}