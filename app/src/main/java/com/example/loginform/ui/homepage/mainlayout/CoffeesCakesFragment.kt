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
import com.example.loginform.Model.ProductItem
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.R
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentCoffeeCakesBinding
import com.example.loginform.ui.add_edit_product.AddEditProductActivity
import com.example.loginform.ui.authentication.SignInActivity
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.example.loginform.ui.productdetails.AdminProductDetails
import com.example.loginform.ui.productdetails.ProductDetails
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CoffeesCakesFragment : Fragment() {
    lateinit var binding: FragmentCoffeeCakesBinding
    lateinit var coffeeCartAdapter: CoffeeCartAdapter

    private val coffeeCartViewModel: CoffeeCartViewModel by activityViewModels()
    var job: Job? = null

    @Inject
    lateinit var prefRepository: PrefRepository
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
            coffeeCartAdapter = CoffeeCartAdapter(context, prefRepository.currentUser.stringToCustomer()?.cusIsAdmin==true)
            layoutManager = LinearLayoutManager(context)
            adapter = coffeeCartAdapter
        }
        coffeeCartAdapter.deleteClicked = {
            showDeleteDialog(it)
        }

        coffeeCartAdapter.procuctClicked = {
            coffeeCartViewModel.setSelectedProduct(it)
            if (prefRepository.currentUser.stringToCustomer()?.cusIsAdmin == true) {
                startActivity(Intent(context, AdminProductDetails::class.java))
            } else
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

            fabAdd.setOnClickListener {
                Intent(context, AddEditProductActivity::class.java).apply {
                    startActivity(this)
                }
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

    private fun showDeleteDialog(it: ProductItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { dialog, which ->
                lifecycleScope.launch {
                    ProgressDialogUtil.showProgressDialog(context)
                    val result = coffeeCartViewModel.deleteProduct(it)
                    ProgressDialogUtil.dismissProgressDialog()
                    coffeeCartViewModel.reloadProducts()
                    when (result) {
                        is Resource.Failure -> {
                             Toast.makeText(
                                context,
                                result.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Success -> {
                            Toast.makeText(
                                context,
                                "Product deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {

                        }
                    }
                }
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
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