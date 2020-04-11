package com.example.movieapp

import com.example.movieapp.Responses.Token
//USER
data class User(
    var userFirstName:String? = null,
    var userLastName:String? = null,
    var userLogin:String? = null,
    var userPassword:String? = null,
    var userToken: Token? = null
)