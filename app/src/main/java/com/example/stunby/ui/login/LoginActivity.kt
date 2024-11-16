package com.example.stunby.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.view.login.LoginViewModel
import com.example.stunby.ui.main.MainActivity
import com.example.stunby.R
import com.example.stunby.data.pref.UserModel
import com.example.stunby.databinding.ActivityLoginBinding
import com.example.stunby.ui.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeLogin()
    }

    private fun observeLogin() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    if (!response.error) {
                        val email = binding.edLoginEmail.text.toString()
                        val token= response.dataLogin?.token
                        viewModel.saveSession(UserModel(email, token.toString()))
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.sukses_login))
                            setMessage(
                                getString(
                                    R.string.welcome_login,
                                    response.message
                                ))
                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        showError(response.message)
                    }
                },
                onFailure = { exception ->
                    showError(exception.message ?: getString(R.string.Error))
                }
            )
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(loading: Boolean?): Boolean {
        binding.loginButton.isEnabled = !loading!!
        binding.edLoginEmail.isEnabled = !loading
        binding.edLoginPassword.isEnabled = !loading
        binding.progressBar.visibility = if (loading) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
        return loading
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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
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
                    viewModel.login(email, password)
                }
            }


        }
    }

}