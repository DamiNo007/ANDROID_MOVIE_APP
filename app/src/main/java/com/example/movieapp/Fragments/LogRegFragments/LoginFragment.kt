package com.example.movieapp.Fragments.LogRegFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.movieapp.Activities.MainPageActivity
import com.example.movieapp.CurrentUser
import com.example.movieapp.R
import com.example.movieapp.Responses.AccountResponse
import com.example.movieapp.Responses.LoginResponse
import com.example.movieapp.Responses.SessionResponse
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.Responses.Token
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

//LOGINFRAGMENTDEVELOP2BRANCH
class LoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var loginBtn: Button
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater?.inflate(R.layout.login, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        login = view.findViewById(R.id.login)
        password = view.findViewById(R.id.password)
        loginBtn = view.findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener() {
            val userLogin: String = login.text.toString().trim()
            val userPassword: String = password.text.toString().trim()
            login(userLogin, userPassword)
        }
        return view
    }

    fun login(username: String, password: String) {
        var requestTokenResponse: Token? = null
        progressBar.visibility = View.VISIBLE

        RetrofitService.getMovieApi()
            .getNewToken(RetrofitService.getApiKey()).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    Log.d("My_token_response", response.body().toString())
                    if (response.isSuccessful) {
                        val type: Type = object : TypeToken<Token>() {}.type
                        requestTokenResponse = gson.fromJson(response.body(), Token::class.java)
                        if (requestTokenResponse != null) {
                            val requestToken: String? = requestTokenResponse?.requestToken
                            val body = JsonObject().apply {
                                addProperty("username", username)
                                addProperty("password", password)
                                addProperty("request_token", requestToken)
                            }
                            getLoginResponse(body)
                        }
                    }
                }
            })
    }

    fun getLoginResponse(body: JsonObject) {
        var logRseponse: LoginResponse? = null
        RetrofitService.getMovieApi()
            .login(RetrofitService.getApiKey(), body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {}

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson: Gson = Gson()
                    if (response.isSuccessful) {
                        logRseponse = gson.fromJson(response.body(), LoginResponse::class.java)

                        if (logRseponse != null) {
                            val body = JsonObject().apply {
                                addProperty("request_token", logRseponse?.requestToken.toString())
                            }
                            getSession(body)
                        }
                    } else {
                        val error = "Incorrect Login or Password!"
                        error(error)
                    }
                }
            })
    }

    fun getSession(body: JsonObject) {
        var session: SessionResponse? = null
        RetrofitService.getMovieApi()
            .getSession(RetrofitService.getApiKey(), body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson: Gson = Gson()
                    if (response.isSuccessful) {
                        session = gson.fromJson(response.body(), SessionResponse::class.java)
                        if (session != null) {
                            val session_id = session?.sessionId
                            getAccount(session_id)
                        }
                    }
                }
            })
    }

    fun saveSessionOftheCurrentUser() {
        val currUserSharedPreference: SharedPreferences =
            this.activity!!.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        var currUserEditor = currUserSharedPreference.edit()
        val gson = Gson()
        val json: String = gson?.toJson(CurrentUser.user)
        currUserEditor.putString("currentUser", json)
        currUserEditor.commit()
    }

    fun getAccount(session: String?) {
        Toast.makeText(this.context, "Loading...", Toast.LENGTH_SHORT).show()

        var accountResponse: AccountResponse? = null

        RetrofitService.getMovieApi()
            .getAccount(RetrofitService.getApiKey(), session!!).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        val type: Type = object : TypeToken<AccountResponse>() {}.type
                        accountResponse =
                            gson.fromJson(response.body(), AccountResponse::class.java)
                        if (accountResponse != null) {
                            welcome(accountResponse!!, session)
                        }
                    }
                }

            })

    }

    fun error(error: String) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
    }

    fun welcome(user: AccountResponse, session: String?) {
        progressBar.visibility = View.GONE
        CurrentUser.user = user
        CurrentUser.user?.sessionId = session;
        saveSessionOftheCurrentUser()
        val intent = Intent(this.activity, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        Toast.makeText(this.context, "Welcome, " + CurrentUser.user?.userName, Toast.LENGTH_SHORT)
            .show()
        startActivity(intent)
        this.activity!!.overridePendingTransition(0, 0)
        ActivityCompat.finishAffinity(this.activity!!)
    }
}