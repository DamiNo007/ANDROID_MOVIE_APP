package com.example.movieapp

import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.Responses.Movie

//CURRENTUSER
class CurrentUser {
    companion object {
        var user: AccountResponse? = null
        var favoritList: List<Movie>? = null
    }
}
