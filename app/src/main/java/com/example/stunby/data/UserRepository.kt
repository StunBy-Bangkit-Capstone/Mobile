package com.example.stunby.data

import androidx.lifecycle.LiveData
import com.example.stunby.data.pref.UserModel
import com.example.stunby.data.pref.UserPreference
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.LoginResponse
import com.example.stunby.data.remote.response.RegisterResponse
import com.example.stunby.data.remote.response.UserResponse
import com.example.stunby.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun register(email: String,name: String,birth_day: String , gender: String,  password: String): RegisterResponse {
        return apiService.register(email,name,birth_day,gender, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    private fun getToken(): String {
        return runBlocking { userPreference.getSession().first().token }
    }

    suspend fun getUser(): Data {
        return apiService.getUser("Bearer ${getToken()}").dataUsers
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}