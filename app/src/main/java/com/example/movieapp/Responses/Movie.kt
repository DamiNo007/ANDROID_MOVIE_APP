package com.example.movieapp.Responses

import android.text.method.MovementMethod
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//MOVIEDEVELOP2BRANCH
@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
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
    var favorite: Int? = null,
    var offlineFavorite: Int? = null
)

data class Story(
    val storyId: Int? = null,
    val title: String? = null,
    val storyImgPath: Int? = null
)

@Entity(tableName = "genre_table")
data class MovieGenres(
    @PrimaryKey
    @SerializedName("id") val genreId: Int? = null,
    @SerializedName("name") val genreName: String? = null
)