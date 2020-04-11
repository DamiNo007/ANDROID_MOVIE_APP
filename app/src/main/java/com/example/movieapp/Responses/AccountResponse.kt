package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName
//ACCOUNTRESPONSE
data class AccountResponse(
    @SerializedName("id") val account_id:Int?=null,
    @SerializedName("name") val name:String?=null,
    @SerializedName("username") val userName:String?=null,
    @SerializedName("include_adult") val isAdult:Boolean?=null,
    var sessionId:String?=null
)
