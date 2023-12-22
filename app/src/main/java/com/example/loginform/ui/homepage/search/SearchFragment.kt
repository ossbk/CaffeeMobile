package com.example.loginform.ui.homepage.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentSearchBinding
import com.example.loginform.ui.homepage.mainlayout.CoffeeCartAdapter
import com.example.loginform.ui.productdetails.ProductDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding

    private val searchProductsViewModel: SearchProductsViewModel by viewModels()
    private lateinit var coffeeCartAdapter: CoffeeCartAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchProductsViewModel.searchProducts(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })


            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                coffeeCartAdapter = CoffeeCartAdapter()
                adapter = coffeeCartAdapter
            }
            lifecycleScope.launch {
                searchProductsViewModel.searchResult.collectLatest {
                    when (it) {
                        is Resource.Failure -> {
                            pbLoading.visibility = View.GONE

                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                        Resource.Loading -> {
                            coffeeCartAdapter.submitList(emptyList())
                            pbLoading.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            pbLoading.visibility = View.GONE

                            coffeeCartAdapter.submitList(it.data)
                        }

                        null -> {
                            pbLoading.visibility = View.GONE

                        }
                    }
                }
            }

            coffeeCartAdapter.procuctClicked = {
                searchProductsViewModel.setSelectedProduct(it)
                startActivity(Intent(context, ProductDetails::class.java))
            }
        }
    }
}