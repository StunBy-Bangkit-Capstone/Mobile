// ApiService.kt
package com.example.stunby.data.remote.retrofit

import com.example.stunby.data.remote.response.AddMeasureResponse
import com.example.stunby.data.remote.response.ArticleResponse
import com.example.stunby.data.remote.response.ArticlesResponse
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataNutritions
import com.example.stunby.data.remote.response.DetailResponse
import com.example.stunby.data.remote.response.LoginResponse
import com.example.stunby.data.remote.response.MeasureResponse
import com.example.stunby.data.remote.response.MeasuresResponse
import com.example.stunby.data.remote.response.NutritionResponse
import com.example.stunby.data.remote.response.NutritionsResponse
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
    @POST("v1/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("full_name") full_name: String,
        @Field("birth_day") birth_day: String,
        @Field("gender") gender: String,
        @Field("password") password: String
    ): RegisterResponse


    @FormUrlEncoded
    @POST("v1/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("v1/me")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): UserResponse

    @Multipart
    @POST("v1/measure")
    suspend fun addMeasure(
        @Header("Authorization") token: String,
        @Part baby_photo_url: MultipartBody.Part,
        @Part("level_activity") levelActivity: RequestBody,
        @Part("status_asi") statusAsi: RequestBody?,
        @Part("age") age: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("date_measure") date: RequestBody?
    ): AddMeasureResponse

    @GET("v1/measures")
    suspend fun getMeasures(
        @Header("Authorization") token: String
    ): MeasuresResponse

    @GET("v1/measure/{measure_id}")
    suspend fun getMeasure(
        @Header("Authorization") token: String,
        @Path("measure_id") id: String
    ): MeasureResponse

    @GET("articles")
    suspend fun getArticles(
        @Header("Authorization") token: String
    ): ArticlesResponse

    @GET("articles/{id}")
    suspend fun getArticle(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ArticleResponse

    @FormUrlEncoded
    @POST("v1/nutrition")
    suspend fun addNutrition(
        @Header("Authorization") token: String,
        @Field("food_name") food_name: String,
        @Field("date") date: String,
        @Field("portion") portion: Int
    ): NutritionResponse


    @GET("v1/nutrition/{nutrition_id}")
    suspend fun getNutrition(
        @Header("Authorization") token: String,
        @Path("nutrition_id") id: String
    ): NutritionResponse

    @GET("v1/nutritions")
    suspend fun getNutritions(
        @Header("Authorization") token: String
    ): NutritionsResponse




}