package com.example.movieapp.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.Responses.MovieGenres

@Dao
interface AccountDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: AccountResponse?)

    @Query("SELECT * FROM accounts_table")
    fun get(): AccountResponse
}