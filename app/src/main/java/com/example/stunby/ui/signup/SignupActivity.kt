package com.example.stunby.ui.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.stunby.ui.login.LoginActivity
import java.util.Calendar

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
                            setMessage("${response.message}. Silahkan login untuk melanjutkan")
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
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()
            val birthDay = binding.edRegisterDob.text.toString()
            val genderId = binding.genderRadioGroup.checkedRadioButtonId
            val genderString = when (genderId) {
                R.id.rb_male -> "male"
                R.id.rb_female -> "female"
                else -> ""
            }

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
                confirmPassword.isEmpty() -> {
                    binding.confirmPasswordEditTextLayout.error = "Konfirmasi password"
                }
                password != confirmPassword -> {
                    binding.confirmPasswordEditTextLayout.error = "Password tidak cocok"
                }
                password.length < 8 -> {
                    binding.passwordEditTextLayout.error = "Password minimal 8 karakter"
                }
                birthDay.isEmpty() -> {
                    binding.dobEditTextLayout.error = "Masukkan tanggal lahir"
                }
                genderString.isEmpty() -> {
                    Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.register(email, name, birthDay, genderString, password)
                }
            }
            binding.loginText.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))

            }
        }

        // Show date picker when clicking on DOB field
        binding.edRegisterDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedYear}-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
                binding.edRegisterDob.setText(formattedDate)
            }, year, month, day).show()
        }
    }
}