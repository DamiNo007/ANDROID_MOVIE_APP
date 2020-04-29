package com.example.movieapp.Responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//ACCOUNTRESPONSEDEVELOP2BRANCH
@Entity(tableName = "accounts_table")
data class AccountResponse(
    @PrimaryKey
    @SerializedName("id") val accountId: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val userName: String? = null,
    @SerializedName("include_adult") val isAdult: Boolean? = null,
    var sessionId: String? = null
)
