package com.example.stunby.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.UserResponse
import kotlinx.coroutines.launch

class ProfileViewModel (private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user


    init {
        getUser()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            _user.value = response
        }
    }
}