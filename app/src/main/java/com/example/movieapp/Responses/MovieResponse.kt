package com.example.movieapp.Responses

import com.google.gson.annotations.SerializedName
//MOVIERESPONSE
data class MoviesResponse(
    @SerializedName("page") val page:Int? = null,
    @SerializedName("results") val results: List<Movie>? = null,
    @SerializedName("genres") val genres: List<MovieGenres>? = null,
    @SerializedName("session_id") val session_id: String? = null,
    @SerializedName("total_results") val totalResults : Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null
)