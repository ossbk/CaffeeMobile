package com.example.loginform.ui.productdetails

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.loginform.databinding.ActivityProductDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetails : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = productDetailsViewModel.selectedProduct
        binding.apply {
            product?.let { mProduct ->
                Glide.with(this@ProductDetails).load(mProduct.prodImage).into(imageView3)
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
            ratingBar.setIsIndicator(true)

            addCoffee.setOnClickListener {
                productDetailsViewModel.incQuantity()
            }

            removeCoffee.setOnClickListener {
                productDetailsViewModel.decQuantity()
            }

            button.setOnClickListener {
                productDetailsViewModel.addToCart(
                    product!!.prodId,
                    coffeeCount.text.toString().toLong()
                )

                Toast.makeText(this@ProductDetails, "Items added to cart", Toast.LENGTH_SHORT)
                    .show()
            }

            lifecycleScope.launch {
                productDetailsViewModel.quantityFlow.collectLatest {
                    coffeeCount.text = it.toString()
                }
            }

            binding.ivBack.setOnClickListener {
                finish()
            }
        }

    }
}