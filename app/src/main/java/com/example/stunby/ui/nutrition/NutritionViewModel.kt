package com.example.stunby.ui.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.MeasureResponse
import com.example.stunby.data.remote.response.NutritionResponse
import com.example.stunby.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NutritionViewModel (private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user



    private val _nutritionResult = MutableLiveData<Result<NutritionResponse>>()
    val nutritionResult: LiveData<Result<NutritionResponse>> = _nutritionResult


    fun addNutrition(food_name: String,portion: Double,date: String){
//        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.addNutrition(food_name,portion,date)
                _nutritionResult.value = Result.success(response)
            }catch (e: Exception){
                _nutritionResult.value = Result.failure(e)
            } finally {
//                _isLoading.value = false
            }
        }
    }

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            _user.postValue(response)
        }
    }

    suspend fun getUserSync(): DataUser {
        return withContext(Dispatchers.IO) {
            repository.getUser()
        }
    }

    suspend fun calculateAge(currentDate: String): Int {
        val birthDate = getUserSync().birthDay
        // Ubah split ke "-" karena formatnya sekarang yyyy-MM-dd
        val (birthYear, birthMonth, birthDay) = birthDate.split("-").map { it.toInt() }
        val (currentYear, currentMonth, currentDay) = currentDate.split("-").map { it.toInt() }

        var age = currentYear - birthYear
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--
        }
        return age
    }

}