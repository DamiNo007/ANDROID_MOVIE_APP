package com.example.movieapp.DAO

import androidx.room.*
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieStatus

@Dao
interface StatusDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(status: MovieStatus)

    @Query("SELECT * FROM movies_status WHERE movieId = :id")
    fun getMovieStatus(id: Int): MovieStatus
}