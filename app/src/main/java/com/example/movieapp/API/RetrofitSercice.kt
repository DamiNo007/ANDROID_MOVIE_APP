package com.example.movieapp.API

import android.util.Log
import com.example.movieapp.Responses.MoviesResponse
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

//RETROFITSERVICE
object RetrofitService {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "87ff9b092f9a54d2d746be621c2f01d5"

    fun getApiKey(): String {
        return API_KEY
    }

    fun getMovieApi(): MovieApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttp())
            .build()
        return retrofit.create(MovieApi::class.java)
    }

    private fun getOkHttp(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
        return okHttpClient.build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("OkHttp", message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}

interface MovieApi {

    @GET("authentication/token/new")
    fun getNewToken(@Query("api_key") apiKey: String): Call<JsonObject>

    @POST("authentication/token/validate_with_login")
    fun login(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    fun deleteSession(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @POST("authentication/session/new")
    fun getSession(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @POST("account/{account_id}/favorite")
    fun markAsFavorite(@Path("account_id") id: Int?, @Query("api_key") apiKey: String?, @Query("session_id") sessionId: String, @Body body: JsonObject): Call<JsonObject>

    @GET("account")
    fun getAccount(@Query("api_key") apiKey: String, @Query("session_id") sessionId: String): Call<JsonObject>

    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteMovieList(
        @Path("account_id") id: Int?, @Query("api_key") apiKey: String?, @Query(
            "session_id"
        ) sessionId: String
    ): Call<MoviesResponse>

    @GET("movie/popular")
    fun getMovieList(@Query("api_key") apiKey: String?): Call<MoviesResponse>

    @GET("movie/{id}")
    fun getMovieById(@Path("id") id: Int, @Query("api_key") apiKey: String?): Call<JsonObject>

    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String?): Call<MoviesResponse>
}