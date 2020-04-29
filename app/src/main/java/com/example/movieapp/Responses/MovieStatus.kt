package com.example.movieapp.Responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies_status")
data class MovieStatus(
    @PrimaryKey
    @SerializedName("id") val movieId: Int? = null,
    @SerializedName("favorite") val isLiked: Boolean? = null,
    @SerializedName("rated") val isRated: Boolean? = null,
    @SerializedName("watchlist") val isInWatchList: Boolean? = null
)
