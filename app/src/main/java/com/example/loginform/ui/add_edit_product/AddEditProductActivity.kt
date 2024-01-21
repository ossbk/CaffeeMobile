package com.example.loginform.ui.add_edit_product

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.loginform.Model.ItemType
import com.example.loginform.Model.ProductItem
import com.example.loginform.Model.toItemType
import com.example.loginform.data.Resource
import com.example.loginform.databinding.ActivityAddEditProductBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddEditProductBinding

    companion object {
        var product: ProductItem = ProductItem()
    }

    private val addProductViewModel: AddProductViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                selectedImageUri = data?.data
                binding.ivProduct.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val isIdEmpty = intent.getStringExtra("id").isNullOrEmpty()
        if (isIdEmpty) {
            product = ProductItem()
        }
        with(binding) {
            ivProductCamera.setOnClickListener {
                openGalleryForImage()
            }
            if (product.prodId.isNotEmpty()) {
                etProductTitle.setText(product.prodName)
                etProductDescription.setText(product.prodDes)
                etProductPrice.setText(product.prodPrice.toString())
                if (product.productType == ItemType.COFFEE)
                    spinner.setSelection(0)
                else
                    spinner.setSelection(1)
                Glide.with(this@AddEditProductActivity).load(product.prodImage).into(ivProduct)
            }

            lifecycleScope.launch {
                addProductViewModel.addServiceResponse.collectLatest {
                    when (it) {
                        is Resource.Failure -> {
                            ProgressDialogUtil.dismissProgressDialog()
                            showToast(it.message)
                        }

                        Resource.Loading -> {
                            ProgressDialogUtil.showProgressDialog(this@AddEditProductActivity)
                        }

                        is Resource.Success -> {
                            ProgressDialogUtil.dismissProgressDialog()
                            showToast("Product added successfully")
                            finish()

                        }

                        null -> {

                        }
                    }
                }
            }

            btnUploadProduct.setOnClickListener {
                val title = etProductTitle.text.toString()
                val description = etProductDescription.text.toString()
                val price = etProductPrice.text.toString()
                if (title.isEmpty()) {
                    showToast("Please enter title")
                } else if (description.isEmpty()) {
                    showToast("Please enter description")
                } else if (price.isEmpty()) {
                    showToast("Please enter price")
                } else if (selectedImageUri == null && product.prodId.isEmpty()) {
                    showToast("Please select image")
                } else {
                    product.apply {
                        this.prodName = title
                        this.prodDes = description
                        this.prodPrice = price.toDouble()
                        this.imageUri = selectedImageUri
                        this.productType = spinner.selectedItem.toString().toItemType()
                    }
                    ProgressDialogUtil.showProgressDialog(this@AddEditProductActivity)
                    addProductViewModel.addProduct(product)
                }

            }
        }
    }

    private fun openGalleryForImage() {
        imageResultLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        })
    }


}


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
