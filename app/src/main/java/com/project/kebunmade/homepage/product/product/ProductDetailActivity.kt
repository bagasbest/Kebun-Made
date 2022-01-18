package com.project.kebunmade.homepage.product.product

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ActivityProductDetailBinding
import com.project.kebunmade.homepage.product.cart.CartActivity
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

        binding?.addToCart?.setOnClickListener {
            showPopupQtyAdd()
        }

        binding?.cart?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun showPopupQtyAdd() {
        val btnSubmit: Button
        val btnCancel: Button
        val etQty: EditText
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_quantity)
        dialog.setCanceledOnTouchOutside(false)
        btnSubmit = dialog.findViewById(R.id.submit)
        btnCancel = dialog.findViewById(R.id.cancel_button)
        etQty = dialog.findViewById(R.id.qty)

        btnSubmit.setOnClickListener{

            val qty = etQty.text.toString().trim()

            /// validasi referral oleh sistem
            if (qty.isEmpty()) {
                Toast.makeText(
                    this,
                    "Kuantitas produk tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val mProgressDialog = ProgressDialog(this)

            mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.show()


            val cartId = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val price = model?.price?.times(qty.toLong())

            val cart = mapOf(
                "cartId" to cartId,
                "userId" to uid,
                "name" to model?.name,
                "qty" to qty.toLong(),
                "price" to price,
                "description" to model?.description,
                "image" to model?.image,
                "productId" to model?.productId,
                "category" to model?.category
            )

            FirebaseFirestore
                .getInstance()
                .collection("cart")
                .document(cartId)
                .set(cart)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        mProgressDialog.dismiss()
                        dialog.dismiss()
                        Toast.makeText(this, "Berhasil menambahkan produk ${model?.name} kedalam keranjang!", Toast.LENGTH_SHORT).show()
                    } else {
                        mProgressDialog.dismiss()
                        dialog.dismiss()
                        Toast.makeText(this, "Ups,anda gagal menambahkan produk ${model?.name} kedalam keranjang!", Toast.LENGTH_SHORT).show()
                    }
                }


        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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
        viewModel.setListProductDetail(model?.category!!, model?.productId!!)
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