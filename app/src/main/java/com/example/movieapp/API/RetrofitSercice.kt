package com.example.movieapp.API

import android.util.Log
import com.example.movieapp.Responses.*
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

//RETROFITSERVICEDEVELOP2BRANCH
object RetrofitService {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "87ff9b092f9a54d2d746be621c2f01d5"

    fun getApiKey(): String {
        return API_KEY
    }

    fun getMovieApi(): MovieApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
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

    //USING COROUTINES
    @GET("authentication/token/new")
    fun getNewTokenCoroutines(@Query("api_key") apiKey: String): Deferred<Response<Token>>

    @POST("authentication/token/validate_with_login")
    fun loginCoroutines(@Query("api_key") apiKey: String, @Body body: JsonObject): Deferred<Response<LoginResponse>>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    fun deleteSessionCoroutines(@Query("api_key") apiKey: String, @Body body: JsonObject): Deferred<Response<JsonObject>>

    @POST("authentication/session/new")
    fun getSessionCoroutines(@Query("api_key") apiKey: String, @Body body: JsonObject): Deferred<Response<SessionResponse>>

    @GET("account")
    fun getAccountCoroutines(@Query("api_key") apiKey: String, @Query("session_id") sessionId: String): Deferred<Response<AccountResponse>>


    @GET("movie/popular")
    fun getMovieListCoroutines(@Query("api_key") apiKey: String?): Deferred<Response<MoviesResponse>>

    @GET("genre/movie/list")
    fun getGenresCoroutines(@Query("api_key") apiKey: String?): Deferred<Response<MoviesResponse>>

    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteMovieListCoroutines(
        @Path("account_id") id: Int?, @Query("api_key") apiKey: String?, @Query(
            "session_id"
        ) sessionId: String
    ): Deferred<Response<MoviesResponse>>

    @POST("account/{account_id}/favorite")
    fun markAsFavoriteCoroutines(
        @Path("account_id") id: Int?, @Query("api_key") apiKey: String?, @Query(
            "session_id"
        ) sessionId: String, @Body body: JsonObject
    ): Deferred<Response<FavoriteResponse>>

    @GET("movie/{id}")
    fun getMovieByIdCoroutines(@Path("id") id: Int, @Query("api_key") apiKey: String?): Deferred<Response<Movie>>
}

