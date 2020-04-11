package com.example.movieapp.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movieapp.CurrentUser
import com.example.movieapp.R
import com.example.movieapp.Responses.FavoriteResponse
import com.example.movieapp.Responses.Movie
import com.example.movieapp.API.RetrofitService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
//MOVIEDETAILACTIVITY
class MovieDetailActivity:AppCompatActivity(){

    private lateinit var progressBar:ProgressBar
    private lateinit var tvTime: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvPopularity:TextView
    private lateinit var tvAdult:TextView
    private lateinit var tvDate:TextView
    private lateinit var imgMovie: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvTitleMini: TextView
    private lateinit var tvDescription:TextView
    private lateinit var tvFullHD:TextView
    private lateinit var tvAge:TextView
    private lateinit var playImg:ImageView
    private lateinit var trailerImg:ImageButton
    private lateinit var downloadImg:ImageButton
    private lateinit var shareImg:ImageButton
    private lateinit var favImg: ImageButton
    private lateinit var tvDateContent:TextView
    private lateinit var tvAdultContent:TextView
    private lateinit var tvRatingContent:TextView
    private lateinit var tvPopularityContent:TextView
    private lateinit var tvTimeContent:TextView
    private var isFavorite = false
    private val baseImageUrl:String = "https://image.tmdb.org/t/p/w500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        progressBar = findViewById(R.id.progressBar)
        tvDescription = findViewById(R.id.tvDescription)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitleMini = findViewById(R.id.tvTitleMini)
        imgMovie = findViewById(R.id.imgMovie)
        tvDate = findViewById(R.id.date)
        tvAdult = findViewById(R.id.adult)
        tvRating = findViewById(R.id.rating)
        tvPopularity =findViewById(R.id.popularity)
        tvTime = findViewById(R.id.time)
        tvDateContent = findViewById(R.id.releaseDate)
        tvAdultContent = findViewById(R.id.isAdult)
        tvRatingContent = findViewById(R.id.ratingContent)
        tvPopularityContent = findViewById(R.id.popularityContent)
        tvTimeContent = findViewById(R.id.timeContent)
        playImg = findViewById(R.id.playImg)
        favImg = findViewById(R.id.imgFav)
        downloadImg = findViewById(R.id.imgDownload)
        shareImg = findViewById(R.id.imgShare)
        trailerImg = findViewById(R.id.imgTrailer)
        tvFullHD = findViewById(R.id.tvFullHD)
        tvAge = findViewById(R.id.tvAge)

        val movieId = intent.getIntExtra("movie_id", 1)
        getMovie(id = movieId)
    }

    private fun getMovie(id: Int){
        RetrofitService.getMovieApi()
            .getMovieById(id, RetrofitService.getApiKey()).enqueue(object :
            Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var gson = Gson()
                Log.d("My_movie", response.body().toString())
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    val movie: Movie = gson.fromJson(response.body(),
                        Movie::class.java)
                    if (movie != null) {
                        tvDescription.text = movie.overview
                        tvTitle.text = movie.title
                        tvTitleMini.text = movie.title
                        tvDate.text = "Date: "
                        tvAdult.text = "Adult: "
                        tvRating.text = "Rating: "
                        tvPopularity.text = "Popularity: "
                        tvTime.text = "Time: "
                        tvFullHD.text = "Full HD"
                        if(movie.isForAdult==true){
                            tvAge.text = "18+"
                        }
                        else
                            tvAge.text = "0+"

                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.ic_play_circle_filled_black_24dp)
                            .into(playImg)
                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.trailer)
                            .into(trailerImg)

                        for(fm in CurrentUser.favoritList!!){
                            if(movie.title.equals(fm.title)){
                                isFavorite = true
                            }
                        }

                        if(isFavorite){
                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites2)
                                .into(favImg)
                        }
                        else{
                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites1)
                                .into(favImg)
                        }

                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.download)
                            .into(downloadImg)

                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.share)
                            .into(shareImg)
                        tvDateContent.text = movie.date

                        if(movie.isForAdult==false)
                            tvAdultContent.text = "No"
                        else
                            tvAdultContent.text = "Yes"
                        tvRatingContent.text = movie.rating.toString()
                        tvPopularityContent.text = movie.popularity.toString()
                        tvTimeContent.text = movie.runtime.toString() + " min"

                        if(movie.imgPath != null){
                            Glide.with(this@MovieDetailActivity)
                                .load(baseImageUrl + movie.imgPath)
                                .into(imgMovie)
                        }

                        favImg.setOnClickListener(){
                            if(isFavorite){
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.movieId)
                                    addProperty("favorite", false)
                                }

                                Glide.with(this@MovieDetailActivity)
                                    .load(R.drawable.favorites1)
                                    .into(favImg)

                                markFavorite(body)
                            }
                            else{
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.movieId)
                                    addProperty("favorite", true)
                                }

                                Glide.with(this@MovieDetailActivity)
                                    .load(R.drawable.favorites2)
                                    .into(favImg)

                                markFavorite(body)
                            }
                        }
                    }
                }
            }
        })
    }

    fun markFavorite(body:JsonObject){
        var favResponse: FavoriteResponse?
        RetrofitService.getMovieApi().markAsFavorite(
            CurrentUser.user!!.account_id,
            RetrofitService.getApiKey(), CurrentUser.user!!.sessionId.toString(),body).enqueue(object :
            Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var gson:Gson = Gson()
                val type: Type = object : TypeToken<FavoriteResponse>() {}.type
                favResponse= gson.fromJson(response.body(), FavoriteResponse::class.java)
                if(favResponse!=null){
                    notify(favResponse!!)
                }
            }
        })
    }

    private fun notify(favResponse: FavoriteResponse){
        val status_code = favResponse!!.status_code
        if(status_code==0){
            Toast.makeText(this, favResponse.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}