package com.example.stunby.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<Data>()
    val user: LiveData<Data> get() = _user

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            _user.postValue(response)
        }
    }

    suspend fun getUserSync(): Data {
        return withContext(Dispatchers.IO) {
            repository.getUser()
        }
    }

    suspend fun calculateAge(currentDate: String): Int {
        val birthDate = getUserSync().birthDay
        val (birthDay, birthMonth, birthYear) = birthDate.split("/").map { it.toInt() }
        val (currentDay, currentMonth, currentYear) = currentDate.split("/").map { it.toInt() }

        var age = currentYear - birthYear
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--
        }
        return age
    }
}
