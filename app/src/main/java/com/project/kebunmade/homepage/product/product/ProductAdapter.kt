package com.project.kebunmade.homepage.product.product

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.kebunmade.databinding.ItemProductGridBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private val productList = ArrayList<ProductModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<ProductModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemProductGridBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: ProductModel) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                title.text = model.name
                description.text = model.description
                price.text = "Rp. ${formatter.format(model.price)}"


                cv.setOnClickListener {

                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}