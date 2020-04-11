package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName
//TOKEN
data class Token(
    @SerializedName("success") val isSuccess:Boolean? = null,
    @SerializedName("expires_at") val expiredAt:String? = null,
    @SerializedName("request_token") val requestToken:String? = null
)