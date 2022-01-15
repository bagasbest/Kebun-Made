package com.project.kebunmade.homepage.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.R
import com.project.kebunmade.databinding.FragmentProductBinding
import com.project.kebunmade.homepage.product.category.CategoryAdapter
import com.project.kebunmade.homepage.product.category.CategoryAddActivity
import com.project.kebunmade.homepage.product.category.CategoryViewModel


class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private var role: String? = null
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater, container, false)

        checkRole()
        showOnboardingImage()
        showCategory()

        return binding?.root
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
                    binding?.categoryAdd?.visibility = View.VISIBLE
                }
            }
    }

    private fun showCategory() {
        binding?.categoryRv?.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryAdapter = CategoryAdapter()
        binding?.categoryRv?.adapter = categoryAdapter

        val viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding?.categoryPb?.visibility = View.VISIBLE
        viewModel.setListCategory()
        viewModel.getCategoryList().observe(viewLifecycleOwner) { category ->
            if (category.size > 0) {
                categoryAdapter!!.setData(category)
            }
            binding?.categoryPb?.visibility = View.GONE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.logout?.setOnClickListener {

        }

        binding?.cart?.setOnClickListener {

        }

        binding?.seeAllNewProduct?.setOnClickListener {

        }

        binding?.categoryAdd?.setOnClickListener {
            startActivity(Intent(activity, CategoryAddActivity::class.java))
        }

    }


    /// onboarding merupakan gambar gambar yang otomatis slide pada halaman utama
    private fun showOnboardingImage() {
        val imageList: ArrayList<SlideModel> = ArrayList() // Create image list
        imageList.add(SlideModel(R.drawable.onb1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb3, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb4, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb5, ScaleTypes.CENTER_CROP))
        binding!!.imageView2.setImageList(imageList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}