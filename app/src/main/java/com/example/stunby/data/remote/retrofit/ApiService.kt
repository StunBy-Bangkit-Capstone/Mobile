// ApiService.kt
package com.example.stunby.data.remote.retrofit

import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.LoginResponse
import com.example.stunby.data.remote.response.RegisterResponse
import com.example.stunby.data.remote.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("full_name") full_name: String,
        @Field("birth_day") birth_day: String,
        @Field("gender") gender: String,
        @Field("password") password: String
    ): RegisterResponse


    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("me")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): UserResponse





}