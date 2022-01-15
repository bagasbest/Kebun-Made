package com.project.kebunmade.homepage.product.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProductViewModel : ViewModel() {

    private val productList = MutableLiveData<ArrayList<ProductModel>>()
    private val listItems = ArrayList<ProductModel>()
    private val TAG = ProductViewModel::class.java.simpleName

    fun setListCategory(category: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getProductList() : LiveData<ArrayList<ProductModel>> {
        return productList
    }
}