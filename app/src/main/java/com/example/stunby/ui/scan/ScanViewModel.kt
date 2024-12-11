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



    suspend fun addMeasure(
        baby_photo_url: MultipartBody.Part,
        levelActivity: RequestBody,
        statusAsi: RequestBody?,
        age: RequestBody?,
        weight: RequestBody?,
        date: RequestBody?,
    ): AddMeasureResponse {
        return try {
            repository.addMeasure(baby_photo_url, levelActivity, statusAsi, age, weight, date ).also {
            }
        } catch (e: Exception) {
            throw e
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
