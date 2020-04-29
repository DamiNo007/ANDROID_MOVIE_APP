package com.example.movieapp.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres

@Dao
interface GenreDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<MovieGenres>?)

    @Query("SELECT * FROM genre_table")
    fun getAll(): List<MovieGenres>
}