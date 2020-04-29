package com.example.movieapp

import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.Responses.Movie

//CURRENTUSERDEVELOP2BRANCH
class CurrentUser {
    companion object {
        var user: AccountResponse? = null
        var favoritList: MutableList<Movie>? = null
        var offlineLikedMovieList: MutableList<Movie>? = ArrayList()
        var offlineDislikedMovieList: MutableList<Movie>? = ArrayList()
    }
}
