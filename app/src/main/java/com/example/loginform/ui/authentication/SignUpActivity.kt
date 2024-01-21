package com.example.loginform.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginform.Model.Customer
import com.example.loginform.data.Resource
import com.example.loginform.databinding.ActivitySignUpBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val name = binding.nameEt.text.toString()
            val phoneNo = binding.phoneEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (name.isNotEmpty() && phoneNo.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    val customer = Customer(
                        cusId = "",
                        cusFullName = name,
                        cusEmail = email,
                        cusPhoneNo = phoneNo,
                        cusUserName = name,
                        cusPassword = pass,
                        cusIsActive = true,
                        cusIsAdmin = binding.checkBox.isChecked
                    )
                    authenticationViewModel.signUp(customer)

                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }


        lifecycleScope.launch {
            authenticationViewModel.signUpFlow.collectLatest {
                when (it) {
                    is Resource.Failure -> {
                        Toast.makeText(this@SignUpActivity, it.message, Toast.LENGTH_SHORT).show()
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    Resource.Loading -> {
                        ProgressDialogUtil.showProgressDialog(this@SignUpActivity)
                    }

                    is Resource.Success -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        Toast.makeText(
                            this@SignUpActivity,
                            "Sign up successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {

                    }
                }

            }
        }
    }
}

