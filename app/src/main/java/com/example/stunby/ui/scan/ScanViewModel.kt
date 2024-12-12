package com.example.stunby.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.AddMeasureResponse
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.MeasureResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ScanViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading



    suspend fun addMeasure(
        baby_photo_url: MultipartBody.Part,
        levelActivity: RequestBody,
        statusAsi: RequestBody?,
        age: RequestBody?,
        weight: RequestBody?,
        date: RequestBody?,
    ): AddMeasureResponse {
        _isLoading.value = true
        return try {
            repository.addMeasure(baby_photo_url, levelActivity, statusAsi, age, weight, date ).also {
            }
        } catch (e: Exception) {
            throw e
        } finally {
            _isLoading.value = false
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

    suspend fun calculateAge(currentDate: String): String {
        val birthDate = getUserSync().birthDay
        val (birthYear, birthMonth, birthDay) = birthDate.split("-").map { it.toInt() }
        val (currentYear, currentMonth, currentDay) = currentDate.split("-").map { it.toInt() }

        var ageInMonths = (currentYear - birthYear) * 12 + (currentMonth - birthMonth)
        if (currentDay < birthDay) {
            ageInMonths--
        }
        val ageFormated = "$ageInMonths bulan"
        return ageFormated
    }

}
