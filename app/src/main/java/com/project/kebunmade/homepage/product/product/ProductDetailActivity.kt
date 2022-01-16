package com.project.kebunmade.homepage.product.product

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityProductDetailBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class ProductDetailActivity : AppCompatActivity() {

    private var binding: ActivityProductDetailBinding? = null
    private var model: ProductModel? = null
    private var productAdapter: ProductFragmentAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()

        model = intent.getParcelableExtra(EXTRA_PRODUCT)
        val formatter: NumberFormat = DecimalFormat("#,###")
        initSameCategoryProduct()


        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        binding?.name?.text = model?.name
        binding?.description?.text = model?.description
        binding?.price?.text = "Rp. ${formatter.format(model?.price)}"
        binding?.info?.text = model?.info
        binding?.caraPenyimpanan?.text = model?.caraPenyimpanan


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initSameCategoryProduct() {
        binding?.sameCategoryProductRv?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        productAdapter = ProductFragmentAdapter()
        binding?.sameCategoryProductRv?.adapter = productAdapter

        val viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListProduct(model?.category!!)
        viewModel.getProductList().observe(this) { product ->
            if (product.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.GONE
                productAdapter!!.setData(product)
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.VISIBLE
            }
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
                val role = "" + it.data?.get("role")
                if (role == "admin") {
                    binding?.edit?.visibility = View.VISIBLE
                    binding?.delete?.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_PRODUCT = "product"
    }
}