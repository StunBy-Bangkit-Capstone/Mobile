package com.example.stunby.ui.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.view.signup.SignupViewModel
import com.example.stunby.R
import com.example.stunby.databinding.ActivitySignupBinding
import com.example.stunby.ui.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: SignupViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeSignup()
    }

    private fun observeSignup() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.signupResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    if (!response.error!!) {
                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage("${response.message}. Yuk, login dan belajar coding.")
                            setPositiveButton("Lanjut") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        response.message?.let { showError(it) }
                    }
                },
                onFailure = { exception ->
                    showError(exception.message ?: "Terjadi kesalahan")
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.signupButton.isEnabled = !isLoading
        binding.edRegisterName.isEnabled = !isLoading
        binding.edRegisterEmail.isEnabled = !isLoading
        binding.edRegisterPassword.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val gender = "pria"
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                password.length < 8 -> {
                    binding.passwordEditTextLayout.error = "Password minimal 8 karakter"
                }
                else -> {
                    viewModel.register(name,gender, email, password)
                }
            }
        }
    }
}