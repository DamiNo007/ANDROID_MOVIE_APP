package com.example.movieapp.Responses

import android.text.method.MovementMethod
import com.google.gson.annotations.SerializedName

//MOVIE
data class Movie(
    @SerializedName("id") val movieId: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("popularity") val popularity: Float? = null,
    @SerializedName("vote_average") val rating: Float? = null,
    @SerializedName("poster_path") val imgPath: String? = null,
    @SerializedName("release_date") val date: String? = null,
    @SerializedName("adult") val isForAdult: Boolean? = null,
    @SerializedName("runtime") val runtime: Float? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    @SerializedName("favorite") val favorite: Boolean? = null
)

data class Story(
    val storyId: Int? = null,
    val title: String? = null,
    val storyImgPath: Int? = null
)

data class MovieGenres(
    @SerializedName("id") val genreId: Int? = null,
    @SerializedName("name") val genreName: String? = null
)