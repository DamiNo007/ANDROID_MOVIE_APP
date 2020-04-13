package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName

//SESSIONRESPONSE
data class SessionResponse(
    @SerializedName("success") val isSuccess: Boolean? = null,
    @SerializedName("session_id") val session_id: String? = null
)