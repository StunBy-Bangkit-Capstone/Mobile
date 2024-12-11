package com.example.stunby.ui.nutrition.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.DataNutrition
import com.example.stunby.data.remote.response.DataUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailNutritionViewModel (private val repository: UserRepository) : ViewModel() {

    private val _nutrition = MutableLiveData<DataNutrition?>()
    val nutrition: MutableLiveData<DataNutrition?> get() = _nutrition

    private val _user = MutableLiveData<DataUser?>()
    val user: MutableLiveData<DataUser?> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading



    suspend fun getNutrition(id: String) {
        _isLoading.postValue(true)
        try {
            val response = withContext(Dispatchers.IO) { repository.getNutrition(id) }
            _nutrition.postValue(response.data)
            Log.d("DetailViewModel", "getMeasure: $response")
        } catch (e: Exception) {
            _nutrition.postValue(null) // Handle error case
            Log.e("DetailViewModel", "Error fetching stories: ${e.message}", e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}
