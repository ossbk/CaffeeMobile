package com.example.loginform.ui.productdetails

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.loginform.adminui.productdetails.RatingProductAdapter
import com.example.loginform.databinding.ActivityAdminProductDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminProductDetails : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProductDetailsBinding

    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()
    lateinit var ratingProductAdapter: RatingProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = productDetailsViewModel.selectedProduct
        binding.apply {
            product?.let { mProduct ->
                Glide.with(this@AdminProductDetails).load(mProduct.prodImage).into(imageView3)
                textViewName.text = mProduct.prodName
                textViewDescription.text = mProduct.prodDes
                cPrice.text = "$" + mProduct.prodPrice.toString()

                if (mProduct.prodNoOfPeopleRated > 0) {
                    val rating = mProduct.prodRating / mProduct.prodNoOfPeopleRated
                    if (rating == 0) {
                        ratingBar.visibility = View.GONE
                    } else {
                        ratingBar.rating = rating.toFloat()
                        ratingBar.visibility = View.VISIBLE
                    }
                } else
                    ratingBar.visibility = View.GONE

            }




            rvRating.apply {
                ratingProductAdapter = RatingProductAdapter()
                layoutManager = LinearLayoutManager(context)
                adapter = ratingProductAdapter
            }
            ratingProductAdapter.submitList(product?.ratings)
            binding.ivBack.setOnClickListener {
                finish()
            }
        }

    }
}