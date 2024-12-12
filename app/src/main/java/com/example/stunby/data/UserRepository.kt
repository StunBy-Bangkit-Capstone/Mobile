package com.example.stunby.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stunby.data.pref.UserModel
import com.example.stunby.data.pref.UserPreference
import com.example.stunby.data.remote.response.AddMeasureResponse
import com.example.stunby.data.remote.response.ArticleResponse
import com.example.stunby.data.remote.response.ArticlesResponse
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataNutritions
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.data.remote.response.DetailResponse
import com.example.stunby.data.remote.response.LoginResponse
import com.example.stunby.data.remote.response.MeasureResponse
import com.example.stunby.data.remote.response.MeasuresResponse
import com.example.stunby.data.remote.response.NutritionResponse
import com.example.stunby.data.remote.response.NutritionsResponse
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

    suspend fun getUser(): DataUser {
        val token = getToken()
        try {
            return apiService.getUser("Bearer $token").dataUsers
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout()
                throw e
            } else {
                throw e
            }
        }
    }


    suspend fun addMeasure(baby_photo_url: MultipartBody.Part, levelActivity: RequestBody, statusAsi: RequestBody?, age: RequestBody?, weight: RequestBody?, date: RequestBody?): AddMeasureResponse {
        val token = getToken()

        return apiService.addMeasure("Bearer $token", baby_photo_url, levelActivity, statusAsi, age, weight, date)
    }



    suspend fun getMeasures(): MeasuresResponse {
        val token = getToken()
        try {
            return apiService.getMeasures("Bearer $token")
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout()
                throw e
            } else {
                throw e
            }
        }
    }

    suspend fun getMeasure(id: String): MeasureResponse {
        val token = getToken()
        return apiService.getMeasure("Bearer $token", id)
    }

    suspend fun getArticles(): ArticlesResponse {
        val token = getToken()
        return apiService.getArticles("Bearer $token")
    }

    suspend fun getArticle(id: String): ArticleResponse {
        val token = getToken()
        return apiService.getArticle("Bearer $token", id)
    }

    suspend fun addNutrition(food_name: String, portion: Double, date: String): NutritionResponse {
        val token = getToken()
        return apiService.addNutrition("Bearer $token", food_name,  date, portion,)
    }

    suspend fun getNutrition(id: String): NutritionResponse {
        val token = getToken()
        return apiService.getNutrition("Bearer $token", id)
    }

    suspend fun getNutritions(): NutritionsResponse {
        val token = getToken()
        val response = apiService.getNutritions("Bearer $token")
        Log.d("NutritionRepository", "API response: $response")
        return response
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