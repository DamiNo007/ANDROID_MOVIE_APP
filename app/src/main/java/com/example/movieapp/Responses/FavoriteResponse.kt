package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName

//FAVORITERESPONSEDEVELOP2BRANCH
data class FavoriteResponse(
    @SerializedName("status_code") val status_code: Int? = null,
    @SerializedName("status_message") val status_message: String? = null
)
