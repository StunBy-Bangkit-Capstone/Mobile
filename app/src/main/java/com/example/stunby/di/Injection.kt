    package com.example.storyapp.di

import android.content.Context
import com.example.stunby.data.UserRepository
import com.example.stunby.data.pref.UserPreference
import com.example.stunby.data.pref.dataStore
import com.example.stunby.data.remote.retrofit.ApiConfig


    object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()

        return UserRepository.getInstance(apiService, pref)

    }
}