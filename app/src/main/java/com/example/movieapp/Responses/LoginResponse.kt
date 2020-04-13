package com.example.movieapp.Responses

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Call

//LOGINRESPONSE
data class LoginResponse(
    @SerializedName("success") val isSuccess: Boolean? = null,
    @SerializedName("expires_at") val expiredAt: String? = null,
    @SerializedName("request_token") val requestToken: String? = null
)