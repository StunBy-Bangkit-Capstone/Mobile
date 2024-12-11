package com.example.stunby.ui.history

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stunby.data.UserRepository
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataItem
import com.example.stunby.data.remote.response.DataNutrition
import com.example.stunby.data.remote.response.DataNutritions
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.HistoriesItem
import com.example.stunby.data.remote.response.NutritionsResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HistoryViewModel (private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<DataUser>()
    val user: LiveData<DataUser> get() = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _measure = MutableLiveData<List<DataItem>>()
    val measure: LiveData<List<DataItem>> get() = _measure

    private val _nutrition = MutableLiveData<NutritionsResponse>()
    val nutrition: LiveData<NutritionsResponse> = _nutrition



    init {
        fetchMeasures()
        fetchNutrition()

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
            } finally {
                _loading.value = false
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun fetchNutrition() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getNutritions()
                Log.d("NutritionViewModel", "Data fetched: $response")
                _nutrition.value = response
            } catch (e: Exception) {
                Log.e("NutritionViewModel", "Error fetching data: $e")
                _nutrition.value = null
            } finally {
                _loading.value = false
            }
        }
    }





}