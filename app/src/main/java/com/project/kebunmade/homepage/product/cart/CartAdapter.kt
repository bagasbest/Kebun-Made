package com.project.kebunmade.homepage.product.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ItemCartBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val cartList = ArrayList<CartModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CartModel>) {
        cartList.clear()
        cartList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemCartBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: CartModel) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                name.text = model.name
                qty.text = "Kuantitas: ${model.qty.toString()}"
                price.text = "Rp. ${formatter.format(model.price)}"

                delete.setOnClickListener {
                    model.cartId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("cart")
                            .document(it1)
                            .delete()
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    cartList.removeAt(adapterPosition)
                                    notifyDataSetChanged()
                                } else {
                                    Toast.makeText(itemView.context, "Gagal menghapus produk ini, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size
}