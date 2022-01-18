package com.project.kebunmade.homepage.product.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.kebunmade.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {


    private var binding: ActivityCartBinding ? = null
    private var adapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initCartList()


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initCartList() {
        binding?.rvCart?.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter()
        binding?.rvCart?.adapter = adapter

        val viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
       viewModel.setListCart()
        viewModel.getCartList().observe(this) { cart ->
            if (cart.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.GONE
                adapter!!.setData(cart)
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}