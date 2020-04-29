package com.example.movieapp.Fragments.LogRegFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.movieapp.Activities.MainPageActivity
import com.example.movieapp.R
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.Responses.Token
import com.example.movieapp.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

//REGISTERFRAGMENTDEVELOP2BRANCH
class RegisterFragment : Fragment(), CoroutineScope {

    lateinit var editfirstName: EditText
    lateinit var editlastName: EditText
    lateinit var editLogin: EditText
    lateinit var editPassword: EditText
    lateinit var confPassword: EditText
    lateinit var registerBtn: Button
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
        var view: View = inflater?.inflate(R.layout.registration, container, false)
        editfirstName = view.findViewById(R.id.name)
        editlastName = view.findViewById(R.id.surname)
        editLogin = view.findViewById(R.id.login)
        editPassword = view.findViewById(R.id.password)
        confPassword = view.findViewById(R.id.confPassword)
        registerBtn = view.findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener() {
            if (editfirstName.text.length > 0 && editlastName.text.length > 0 && editLogin.text.length > 0 && editPassword.text.length > 0) {
                var name: String = editfirstName.text.toString()
                var surname: String = editlastName.text.toString()
                var login: String = editLogin.text.toString()
                var password: String = editPassword.text.toString()
                var confpassword: String = confPassword.text.toString()

                if (editPassword.text.toString().equals(confPassword.text.toString())) {
                    getNewTokenCoroutines()
                } else
                    Toast.makeText(this.context, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
            } else {
                val text = "Заполните все поля!"
                val duration = Toast.LENGTH_SHORT
                Toast.makeText(this.context, text, duration).show()
            }
        }
        return view
    }

    //GETTING TOKEN USING COROUTINES
    fun getNewTokenCoroutines() {
        launch {
            val response =
                RetrofitService.getMovieApi().getNewTokenCoroutines(RetrofitService.getApiKey())
                    .await()
            if (response.isSuccessful) {
                var requestTokenResponse = response.body()
                if (requestTokenResponse != null) {
                    createNewUser(requestTokenResponse)
                }
            }
        }
    }

    fun createNewUser(token: Token) {
        var newUser = User()
        var name: String = editfirstName.text.toString()
        var surname: String = editlastName.text.toString()
        var login: String = editLogin.text.toString()
        var password: String = editPassword.text.toString()
        newUser.userToken = token
        newUser?.userFirstName = name
        newUser?.userLastName = surname
        newUser?.userLogin = login
        newUser?.userPassword = password
        welcome(newUser)
    }

    fun welcome(user: User) {
        val intent = Intent(this.activity, MainPageActivity::class.java)
        Toast.makeText(this.context, "Welcome, " + user.toString(), Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}