package com.example.stunby.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.UserResponse
import kotlinx.coroutines.launch

class ArticleViewModel (private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<Data>()
    val user: LiveData<Data> get() = _user

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text


    fun getUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            _user.value = response
        }
    }
}



