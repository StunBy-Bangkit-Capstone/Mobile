package com.example.stunby.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataDetail
import com.example.stunby.data.remote.response.DataUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _measure = MutableLiveData<Data?>()
    val measure: MutableLiveData<Data?> get() = _measure

    private val _user = MutableLiveData<DataUser?>()
    val user: MutableLiveData<DataUser?> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading



    suspend fun getMeasure(id: String) {
        _isLoading.postValue(true)
        try {
            val response = withContext(Dispatchers.IO) { repository.getMeasure(id) }
            _measure.postValue(response.data)
            Log.d("DetailViewModel", "getMeasure: $response")
            getUser()
        } catch (e: Exception) {
            _measure.postValue(null) // Handle error case
            Log.e("DetailViewModel", "Error fetching stories: ${e.message}", e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    private suspend fun getUser() {
        _isLoading.postValue(true)
        try {
            val response = withContext(Dispatchers.IO) { repository.getUser() }
            _user.postValue(response)
        } catch (e: Exception) {
            _user.postValue(null) // Handle error case
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}
