package com.example.stunby.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.pref.UserModel
import kotlinx.coroutines.launch


class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
        }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}