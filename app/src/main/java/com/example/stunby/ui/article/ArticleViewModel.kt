package com.example.stunby.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository

import com.example.stunby.data.remote.response.DataItemArticle
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.UserResponse
import kotlinx.coroutines.launch

class ArticleViewModel (private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user


    private val _article = MutableLiveData<List<DataItemArticle>>()
    val article: LiveData<List<DataItemArticle>> get() = _article



    init {
        fetchMeasures()

    }


    private fun fetchMeasures() {
        viewModelScope.launch {
            try {
//                _loading.value = true
                val response = repository.getArticles()
                _article.value = response.data

            }catch (e: Exception) {
                // Menangani exception lain dan memberikan feedback ke pengguna
                _article.value = emptyList()
            } finally {
//                _loading.value = false
            }
        }
    }



}



