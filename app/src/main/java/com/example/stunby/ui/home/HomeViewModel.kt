package com.example.stunby.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.UserResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<Data>()
    val user: LiveData<Data> get() = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading



    init {
        fetchUser()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getUser()
                if (response != null) {
                    _user.value = response
                } else {
                    logout()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching user data: ${e.message}", e)
                logout()
            } finally {
                _loading.value = false
            }
        }
    }


}