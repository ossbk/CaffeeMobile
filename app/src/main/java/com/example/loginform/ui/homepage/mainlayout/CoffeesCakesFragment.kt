package com.example.loginform.ui.homepage.mainlayout

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginform.Model.ItemType
import com.example.loginform.R
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentCoffeeCakesBinding
import com.example.loginform.ui.authentication.SignInActivity
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.example.loginform.ui.productdetails.ProductDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoffeesCakesFragment : Fragment() {
    lateinit var binding: FragmentCoffeeCakesBinding
    lateinit var coffeeCartAdapter: CoffeeCartAdapter

    private val coffeeCartViewModel: CoffeeCartViewModel by activityViewModels()
    var job: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoffeeCakesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemRecyclerView.apply {
            coffeeCartAdapter = CoffeeCartAdapter()
            layoutManager = LinearLayoutManager(context)
            adapter = coffeeCartAdapter
        }

        coffeeCartAdapter.procuctClicked = {
            coffeeCartViewModel.setSelectedProduct(it)
            startActivity(Intent(context, ProductDetails::class.java))
        }

        binding.ivLogout.setOnClickListener {
            coffeeCartViewModel.logout()
            startActivity(Intent(context, SignInActivity::class.java))
            activity?.finish()
        }

        binding.apply {
            showCoffeeButton.setOnClickListener {
                coffeeCartViewModel.setSelectedType(ItemType.COFFEE)
                coffeeCartViewModel.getCoffeeProducts()
            }

            showCakeButton.setOnClickListener {
                coffeeCartViewModel.setSelectedType(ItemType.CAKE)
                coffeeCartViewModel.getCakesProducts()
            }
        }

        lifecycleScope.launch {
            coffeeCartViewModel.selectedItemType.collectLatest {
                selectItem(it)
                job?.cancel()
                job = launch {
                    coffeeCartViewModel.getSelectedFlow(it).collectLatest { listResource ->
                        when (listResource) {
                            is Resource.Failure -> {
                                ProgressDialogUtil.dismissProgressDialog()

                                Toast.makeText(
                                    context,
                                    "${it.name}" + listResource.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }

                            Resource.Loading -> {
                                ProgressDialogUtil.showProgressDialog(context)

                                coffeeCartAdapter.submitList(emptyList())

                            }

                            is Resource.Success -> {
                                ProgressDialogUtil.dismissProgressDialog()
                                coffeeCartAdapter.submitList(ArrayList(listResource.data))
                            }

                            null -> {

                            }
                        }
                    }
                }

            }
        }
    }

    private fun selectItem(type: ItemType) {
        with(binding) {

            if (type == ItemType.CAKE) {
                makeUnselected(showCoffeeButton)
                makeSelected(showCakeButton)
            } else {
                makeUnselected(showCakeButton)
                makeSelected(showCoffeeButton)
            }

        }
    }

    fun makeUnselected(button: Button) {
        button.background = null
        button.setTextColor(Color.BLACK)
    }

    fun makeSelected(button: Button) {
        button.setBackgroundResource(R.drawable.bg_slight_round)
        button.setTextColor(Color.WHITE)
    }
}