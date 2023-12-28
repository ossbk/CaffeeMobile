package com.example.loginform.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginform.data.Resource
import com.example.loginform.databinding.ActivitySignInBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.example.loginform.ui.homepage.HomePageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val authenticationViewModel: AuthenticationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authenticationViewModel.login(email, pass)
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

        lifecycleScope.launch {
            authenticationViewModel.loginFlow.collectLatest {
                when (it) {
                    is Resource.Failure -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        Toast.makeText(
                            this@SignInActivity, it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    Resource.Loading -> {
                        ProgressDialogUtil.showProgressDialog(this@SignInActivity)
                    }

                    is Resource.Success -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        val intent = Intent(this@SignInActivity, HomePageActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                    else -> {

                    }
                }
            }

        }

    }
}