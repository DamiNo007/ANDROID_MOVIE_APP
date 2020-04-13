package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName

//ACCOUNTRESPONSEDEVELOP2BRANCH
data class AccountResponse(
    @SerializedName("id") val accountId: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val userName: String? = null,
    @SerializedName("include_adult") val isAdult: Boolean? = null,
    var sessionId: String? = null
)
