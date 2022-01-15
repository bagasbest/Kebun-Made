package com.project.kebunmade.homepage.product.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityProductBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class ProductActivity : AppCompatActivity() {

    private var binding: ActivityProductBinding? = null
    private var role: String? = null
    private var adapter: ProductAdapter? = null

    override fun onResume() {
        super.onResume()
        initProduct()
    }

    private fun initProduct() {
        binding?.rvProduct?.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        adapter = ProductAdapter()
        binding?.rvProduct?.adapter = adapter

        val viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        intent.getStringExtra(EXTRA_CATEGORY)?.let { viewModel.setListCategory(it) }
        viewModel.getProductList().observe(this) { product ->
            if (product.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.GONE
                adapter!!.setData(product)
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()

        binding?.category?.text =  intent.getStringExtra(EXTRA_CATEGORY)

        binding?.productAdd?.setOnClickListener {
            val intent = Intent(this, ProductAddActivity::class.java)
            intent.putExtra(ProductAddActivity.EXTRA_CATEGORY, intent.getStringExtra(EXTRA_CATEGORY))
            startActivity(intent)
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = "" + it.data?.get("role")
                if(role == "admin") {
                    binding?.productAdd?.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_CATEGORY = "category"
    }
}