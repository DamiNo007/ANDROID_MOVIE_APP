package com.example.movieapp.Fragments.MainPageFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.Activities.LogRegActivity
import com.example.movieapp.CurrentUser
import com.example.movieapp.R
import com.example.movieapp.Responses.SessionResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.registration.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

//THIRDFRAGMENTDEVELOP2BRANCH
class ThirdFragment : Fragment(), CoroutineScope {

    private lateinit var userName: TextView
    private lateinit var userFullName: TextView
    private lateinit var logOutBtn: Button
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater?.inflate(R.layout.third_fragment, container, false)
        userName = view.findViewById(R.id.userName)
        userFullName = view.findViewById(R.id.userFullName)
        logOutBtn = view.findViewById(R.id.logOutBtn)
        userName.text = CurrentUser.user?.userName
        userFullName.text = CurrentUser.user?.name

        logOutBtn.setOnClickListener() {
            val body = JsonObject().apply {
                addProperty("session_id", CurrentUser.user!!.sessionId)
            }
            logout(body)
        }
        return view
    }

    //LOGOUT USING COROUTINES
    fun logout(body: JsonObject) {
        launch {
            val response = RetrofitService.getMovieApi()
                .deleteSessionCoroutines(RetrofitService.getApiKey(), body).await()
            if (response.isSuccessful) {
                goodbye()
            }
        }
    }

    fun goodbye() {
        val intent = Intent(this.activity, LogRegActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val sharedPreference: SharedPreferences =
            this.activity!!.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        sharedPreference.edit().remove("currentUser").commit()
        Toast.makeText(this.context, "See you, " + CurrentUser.user?.userName, Toast.LENGTH_SHORT)
            .show()
        startActivity(intent)
        this.activity!!.overridePendingTransition(0, 0)
        ActivityCompat.finishAffinity(this.activity!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        val currUserFavListSharedPreference: SharedPreferences =
            this.activity!!.getSharedPreferences("CURRENT_USER_FAVORITE_LIST", Context.MODE_PRIVATE)
        var currUserFavListEditor = currUserFavListSharedPreference.edit()
        val gson = Gson()
        val json: String = gson?.toJson(CurrentUser.favoritList)
        currUserFavListEditor.putString("currentUserFavList", json)
        currUserFavListEditor.commit()
    }
}
