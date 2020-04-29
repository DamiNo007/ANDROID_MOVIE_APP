package com.example.movieapp.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieapp.DAO.AccountDAO
import com.example.movieapp.DAO.GenreDAO
import com.example.movieapp.DAO.MovieDAO
import com.example.movieapp.DAO.StatusDAO
import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres
import com.example.movieapp.Responses.MovieStatus

@Database(
    entities = [Movie::class, MovieGenres::class, MovieStatus::class, AccountResponse::class],
    version = 8
)
@TypeConverters(Converters::class)
abstract class MovieDB : RoomDatabase() {

    abstract fun movieDao(): MovieDAO
    abstract fun genreDao(): GenreDAO
    abstract fun statusDao(): StatusDAO
    abstract fun accountDao(): AccountDAO

    companion object {

        var INSTANCE: MovieDB? = null

        fun getDB(context: Context): MovieDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDB::class.java,
                    "movie_db.db"
                ).fallbackToDestructiveMigration().build()
            }
            return INSTANCE!!
        }
    }
}