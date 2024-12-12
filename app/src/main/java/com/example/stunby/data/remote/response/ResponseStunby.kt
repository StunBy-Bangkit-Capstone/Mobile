package com.example.stunby.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseStunby(

	@field:SerializedName("login_response")
	val loginResponse: LoginResponse? = null,

	@field:SerializedName("registration_response")
	val registerResponse: RegisterResponse? = null
)

data class RegisterResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("dataRegis")
	val dataRegis: DataRegis? = null
)

data class DataRegis(

	@field:SerializedName("birth_day")
	val birthDay: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("foto_url")
	val fotoUrl: Any? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class LoginResponse(

	@field:SerializedName("data")
	val data: String,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class UserResponse(
	@field:SerializedName("data")
	val dataUsers: DataUser ,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataUser(
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("birth_day")
	val birthDay: String,

	@field:SerializedName("foto_url")
	val fotoUrl: String,
)

data class MeasureResponse(
	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class AddMeasureResponse(
	@field:SerializedName("data")
	val data: DataAddMeasure,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataAddMeasure(
	val imtResult: ImtResult? = null,
	val measurementResult: MeasurementResult? = null,
	val measurement: Measurement? = null
)

data class Data(

	@field:SerializedName("IMT_Result")
	val iMTResult: ImtResult? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("level_activity")
	val levelActivity: String? = null,

	@field:SerializedName("status_asi")
	val statusAsi: String? = null,

	@field:SerializedName("baby_photo_url")
	val babyPhotoUrl: String? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("measuremenet_result")
	val measuremenetResult: MeasuremenetResult? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("date_measure")
	val dateMeasure: String? = null
)

data class Measurement(
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("date_measure")
	val dateMeasure: String,

	@field:SerializedName("level_activity")
	val levelActivity: String,

	@field:SerializedName("weight")
	val weight: Double,

	@field:SerializedName("baby_photo_url")
	val babyPhotoUrl: String,

	@field:SerializedName("status_asi")
	val statusAsi: String
)

data class ImtResult(
	@field:SerializedName("baby_length")
	val babyLength: Any? = null,

	@field:SerializedName("imt")
	val imt: Any? = null,

	@field:SerializedName("z_score_weight")
	val zScoreWeight: Any? = null,

	@field:SerializedName("nitritional_status_length")
	val nitritionalStatusLength: String? = null,

	@field:SerializedName("status_bb_tb")
	val statusBbTb: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("measure_id")
	val measureId: String? = null,

	@field:SerializedName("z_score_bb_tb")
	val zScoreBbTb: Any? = null,

	@field:SerializedName("status_imt")
	val statusImt: String? = null,

	@field:SerializedName("z_score_length")
	val zScoreLength: Any? = null,

	@field:SerializedName("nitritional_status_weight")
	val nitritionalStatusWeight: String? = null
)

data class MeasurementResult(
	@field:SerializedName("fat_needed")
	val fatNeeded: Any? = null,

	@field:SerializedName("protein_needed")
	val proteinNeeded: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("measure_id")
	val measureId: String? = null,

	@field:SerializedName("calories_needed")
	val caloriesNeeded: Any? = null,

	@field:SerializedName("carbohydrate_needed")
	val carbohydrateNeeded: Any? = null
)

data class MeasuresResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("birth_day")
	val birthDay: String? = null,

	@field:SerializedName("foto_url")
	val fotoUrl: String? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class DataItem(

	@field:SerializedName("IMT_Result")
	val iMTResult: ImtResult? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("level_activity")
	val levelActivity: String? = null,

	@field:SerializedName("status_asi")
	val statusAsi: String? = null,

	@field:SerializedName("baby_photo_url")
	val babyPhotoUrl: String? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("measuremenet_result")
	val measuremenetResult: MeasuremenetResult? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("date_measure")
	val dateMeasure: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class MeasuremenetResult(

	@field:SerializedName("fat_needed")
	val fatNeeded: Any? = null,

	@field:SerializedName("protein_needed")
	val proteinNeeded: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("measure_id")
	val measureId: String? = null,

	@field:SerializedName("calories_needed")
	val caloriesNeeded: Any? = null,

	@field:SerializedName("carbohydrate_needed")
	val carbohydrateNeeded: Any? = null
)


data class DetailResponse(
	val data: DataDetail? = null,
	val error: Boolean? = null,
	val message: String? = null
)




data class DataDetail(
	val IMTResult: ImtResult? = null,
	val updated_at: String? = null,
	val user_id: String? = null,
	val level_activity: String? = null,
	val status_asi: String? = null,
	val baby_photo_url: String? = null,
	val weight: Double? = null,
	val created_at: String? = null,
	val measuremenet_result: MeasuremenetResult? = null,
	val id: String? = null,
	val date_measure: String? = null
)



data class ArticlesResponse(

	@field:SerializedName("data")
	val data: List<DataItemArticle>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)


data class DataItemArticle(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("author")
	val author: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("article_img_url")
	val articleImgUrl: String? = null,

	@field:SerializedName("constent")
	val constent: String? = null
)

data class ArticleResponse(

	@field:SerializedName("data")
	val data: DataItemArticle? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)


data class NutritionResponse(
	val data: DataNutrition? = null,
	val succes: Boolean? = null,
	val message: String? = null
)

data class DataNutrition(
	val date: String? = null,
	val food_name: String? = null,
	val updated_at: String? = null,
	val user_id: String? = null,
	val portion: Double? = null,
	val created_at: String? = null,
	val nutrition_result: NutritionResult? = null,
	val id: String? = null
)

data class NutritionResult(
	val carbohydrates: Double? = null,
	val notes: String? = null,
	val nutrition_id: String? = null,
	val updated_at: String? = null,
	val fats: Double? = null,
	val proteins: Double? = null,
	val calciums: Double? = null,
	val created_at: String? = null,
	val id: String? = null,
	val calories: Double? = null
)



data class NutritionsResponse(
	val data: DataNutritions,
	val error: Boolean? = null,
	val message: String? = null
)

data class TotalNutrition(
	val total_calories: Double? = null,
	val total_carbohydrates: Double? = null,
	val total_calciums: Double? = null,
	val total_proteins: Any? = null,
	val total_fats: Any? = null
)

data class NutritionDetails(
	val carbohydrates: Double? = null,
	val notes: String? = null,
	val fats: Any? = null,
	val proteins: Any? = null,
	val calciums: Double? = null,
	val calories: Double? = null
)

data class HistoriesItem(
	val date: String? = null,
	val food_name: String? = null,
	val nutrition_details: NutritionDetails? = null,
	val portion: Double? = null,
	val created_at: String? = null,
	val id: String? = null
)

data class DataNutritions(
	val total_nutrition: TotalNutrition? = null,
	val histories: List<HistoriesItem>? = null
)


