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
import com.example.movieapp.CurrentUser
import com.example.movieapp.R
import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.API.RetrofitService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        progressBar = findViewById(R.id.progressBar)
    }

    /*
    //GETTING ACCOUNT WITHOUT COROUTINES
    fun getAccount(session: String?) {
        var accountResponse: AccountResponse? = null
        RetrofitService.getMovieApi()
            .getAccount(RetrofitService.getApiKey(), session!!).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        progressBar.visibility = View.GONE
                        val type: Type = object : TypeToken<AccountResponse>() {}.type
                        accountResponse =
                            gson.fromJson(response.body(), AccountResponse::class.java)
                        if (accountResponse != null) {
                            welcome(accountResponse!!, session)
                        } else {
                            CurrentUser.user = null
                            login()
                        }
                    }
                }
            })
    }
    */

    //GETTING ACCOUNT USING COROUTINES
    fun getAccountCoroutines(session: String?) {
        launch {
            val response = RetrofitService.getMovieApi()
                .getAccountCoroutines(RetrofitService.getApiKey(), session!!).await()
            if (response.isSuccessful) {
                progressBar.visibility = View.GONE
                val accountResponse = response.body()
                if (accountResponse != null) {
                    welcome(accountResponse!!, session)
                } else {
                    CurrentUser.user = null
                    login()
                }
            }
        }
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

