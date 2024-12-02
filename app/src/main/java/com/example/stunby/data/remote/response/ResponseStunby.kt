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
	val dataUsers: Data ,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Data(
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