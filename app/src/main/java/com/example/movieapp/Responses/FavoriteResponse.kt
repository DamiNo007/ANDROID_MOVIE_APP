package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName

//FAVORITERESPONSEDEVELOP2BRANCH
data class FavoriteResponse(
    @SerializedName("status_code") val statusСode: Int? = null,
    @SerializedName("status_message") val statusMessage: String? = null
)
