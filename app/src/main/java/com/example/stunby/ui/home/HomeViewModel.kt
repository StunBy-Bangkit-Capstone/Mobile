package com.example.stunby.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataItem
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.MeasuresResponse
import com.example.stunby.data.remote.response.UserResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _measure = MutableLiveData<List<DataItem>>()
    val measure: LiveData<List<DataItem>> get() = _measure



    init {
        fetchUser()
        fetchMeasures()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private fun fetchMeasures() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getMeasures()
                _measure.value = response.data

                }catch (e: Exception) {
                // Menangani exception lain dan memberikan feedback ke pengguna
                _measure.value = emptyList()
                Log.e("HomeViewModel", "Error fetching stories: ${e.message}", e)
            } finally {
                 _loading.value = false
            }
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