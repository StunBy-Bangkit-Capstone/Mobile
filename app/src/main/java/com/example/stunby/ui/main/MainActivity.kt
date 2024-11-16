package com.example.stunby.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.stunby.databinding.ActivityMainBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            viewModel.getSession().observe(this) { user ->
                val token = user.token
                Log.d("MainActivity", "Token: $token")
                if (!user.isLogin) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }

    }
}