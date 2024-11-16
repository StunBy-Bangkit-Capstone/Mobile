package com.example.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _signupResult = MutableLiveData<Result<RegisterResponse>>()
    val signupResult: LiveData<Result<RegisterResponse>> = _signupResult


    fun register(name: String,gender: String ,email: String, password: String){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(name,gender,email,password)
                _signupResult.value = Result.success(response)
            }catch (e: Exception){
                _signupResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}