package com.example.movieapp.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movieapp.Responses.Movie


@Dao
interface MovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Movie>)

    @Query("SELECT * FROM movie_table ORDER BY popularity DESC")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie_table WHERE movieId = :id")
    fun getMovie(id: Int): Movie

    @Query("SELECT * FROM movie_table WHERE favorite = 1")
    fun getFavoriteMovies(): List<Movie>

    @Query("SELECT * FROM movie_table WHERE offlineFavorite = 1")
    fun getOfflineLikedMovies(): List<Movie>


    @Query("UPDATE movie_table SET offlineFavorite = :isLiked WHERE movieId = :id")
    fun updateMovieOfflineLiked(isLiked: Int, id: Int)

    @Query("UPDATE movie_table SET favorite = :favorite WHERE movieId = :id")
    fun updateMovieFavorite(favorite: Int, id: Int)

    @Update
    fun updateMovie(movie: Movie)

    @Update
    fun updateAll(movies: List<Movie>)

}