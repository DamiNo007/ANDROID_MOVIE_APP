package com.example.movieapp.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.movieapp.CurrentUser
import com.example.movieapp.R
import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.DAO.AccountDAO
import com.example.movieapp.DAO.GenreDAO
import com.example.movieapp.DB.MovieDB
import com.example.movieapp.Responses.Movie
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

//MAINACTIVITYDEVELOP2BRANCH
class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var progressBar: ProgressBar
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var accountDao: AccountDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accountDao = MovieDB.getDB(context = this!!).accountDao()
        val sharedPref: SharedPreferences =
            this.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        val gson = Gson()
        var json: String? = sharedPref.getString("currentUser", null)
        var type: Type = object : TypeToken<AccountResponse>() {}.type
        CurrentUser.user = gson.fromJson<AccountResponse>(json, type)

        if (CurrentUser.user != null && CurrentUser.user?.sessionId != null) {
            getAccountCoroutines(CurrentUser.user?.sessionId.toString())
        } else {
            val intent = Intent(this, LogRegActivity::class.java)
            startActivity(intent)
        }

        val sharedPrefFavList: SharedPreferences =
            this.getSharedPreferences("CURRENT_USER_FAVORITE_LIST", Context.MODE_PRIVATE)
        val gsonFavList = Gson()
        var jsonFavList: String? = sharedPrefFavList.getString("currentUserFavList", null)
        var typeFavList: Type = object : TypeToken<ArrayList<Movie>>() {}.type
        CurrentUser.favoritList = gsonFavList.fromJson<ArrayList<Movie>>(jsonFavList, typeFavList)
        progressBar = findViewById(R.id.progressBar)
    }

    //GETTING ACCOUNT USING COROUTINES
    fun getAccountCoroutines(session: String?) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi()
                        .getAccountCoroutines(RetrofitService.getApiKey(), session!!).await()
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            accountDao?.insert(result)
                            welcome(result, CurrentUser.user?.sessionId)
                        }
                        result
                    } else {
                        val account = accountDao?.get()
                        accountDao?.get()
                        if (account != null) {
                            getInOffline(account, CurrentUser.user?.sessionId)
                        } else {
                            login()
                        }

                    }
                } catch (e: Exception) {
                    val account = accountDao?.get()
                    accountDao?.get()
                    if (account != null) {
                        getInOffline(account, CurrentUser.user?.sessionId)
                    } else {
                        login()
                    }
                }
            }
            progressBar.visibility = View.GONE
        }

    }

    fun getInOffline(user: AccountResponse, session: String?) {
        val intent = Intent(this, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.overridePendingTransition(0, 0)
        ActivityCompat.finishAffinity(this)
    }

    fun welcome(user: AccountResponse, session: String?) {
        CurrentUser.user = user
        CurrentUser.user?.sessionId = session;
        val intent = Intent(this, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.overridePendingTransition(0, 0)
        ActivityCompat.finishAffinity(this)
    }

    fun login() {
        val intent = Intent(this, LogRegActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.overridePendingTransition(0, 0)
        ActivityCompat.finishAffinity(this)
    }
}

