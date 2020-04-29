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
import com.example.movieapp.DAO.MovieDAO
import com.example.movieapp.DAO.StatusDAO
import com.example.movieapp.DB.MovieDB
import com.example.movieapp.Responses.MovieStatus
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

//MOVIEDETAILACTIVITYDEVELOP2BRANCH
class MovieDetailActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvTime: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvPopularity: TextView
    private lateinit var tvAdult: TextView
    private lateinit var tvDate: TextView
    private lateinit var imgMovie: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvTitleMini: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvFullHD: TextView
    private lateinit var tvAge: TextView
    private lateinit var playImg: ImageView
    private lateinit var trailerImg: ImageButton
    private lateinit var downloadImg: ImageButton
    private lateinit var shareImg: ImageButton
    private lateinit var favImg: ImageButton
    private var isLiked: Boolean = false
    private lateinit var tvDateContent: TextView
    private lateinit var tvAdultContent: TextView
    private lateinit var tvRatingContent: TextView
    private lateinit var tvPopularityContent: TextView
    private lateinit var tvTimeContent: TextView
    private var movieStatus: MovieStatus? = null
    private var notConnected = false
    private val baseImageUrl: String = "https://image.tmdb.org/t/p/w500"
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var movieDao: MovieDAO? = null
    private var statusDao: StatusDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        movieDao = MovieDB.getDB(context = this).movieDao()
        statusDao = MovieDB.getDB(context = this).statusDao()
        progressBar = findViewById(R.id.progressBar)
        tvDescription = findViewById(R.id.tvDescription)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitleMini = findViewById(R.id.tvTitleMini)
        imgMovie = findViewById(R.id.imgMovie)
        tvDate = findViewById(R.id.date)
        tvAdult = findViewById(R.id.adult)
        tvRating = findViewById(R.id.rating)
        tvPopularity = findViewById(R.id.popularity)
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
        getMovieCoroutines(id = movieId)
    }

    override fun onResume() {
        super.onResume()
        isLiked = false
        val movieId = intent.getIntExtra("movie_id", 1)
        getMovieCoroutines(id = movieId)
    }

    //GETTING MOVIE USING COROUTINES
    private fun getMovieCoroutines(id: Int) {
        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi()
                        .getMovieByIdCoroutines(id, RetrofitService.getApiKey()).await()

                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        notConnected = true
                        movieDao?.getMovie(id) ?: null
                    }
                } catch (e: Exception) {
                    notConnected = true
                    movieDao?.getMovie(id) ?: null
                }
            }

            progressBar.visibility = View.GONE
            if (movie != null) {
                if (CurrentUser.favoritList != null) {
                    for (m in CurrentUser.favoritList!!) {
                        if (movie.movieId == m.movieId) {
                            isLiked = true
                        }
                    }
                }

                if (isLiked) {
                    Glide.with(this@MovieDetailActivity)
                        .load(R.drawable.favorites2)
                        .into(favImg)
                } else {
                    Glide.with(this@MovieDetailActivity)
                        .load(R.drawable.favorites1)
                        .into(favImg)
                }

                tvDescription.text = movie.overview
                tvTitle.text = movie.title
                tvTitleMini.text = movie.title
                tvDate.text = "Date: "
                tvAdult.text = "Adult: "
                tvRating.text = "Rating: "
                tvPopularity.text = "Popularity: "
                tvTime.text = "Time: "
                tvFullHD.text = "Full HD"
                if (movie.isForAdult == true) {
                    tvAge.text = "18+"
                } else
                    tvAge.text = "0+"

                Glide.with(this@MovieDetailActivity)
                    .load(R.drawable.ic_play_circle_filled_black_24dp)
                    .into(playImg)
                Glide.with(this@MovieDetailActivity)
                    .load(R.drawable.trailer)
                    .into(trailerImg)

                Glide.with(this@MovieDetailActivity)
                    .load(R.drawable.download)
                    .into(downloadImg)

                Glide.with(this@MovieDetailActivity)
                    .load(R.drawable.share)
                    .into(shareImg)
                tvDateContent.text = movie.date
                if (movie.isForAdult == false)
                    tvAdultContent.text = "No"
                else
                    tvAdultContent.text = "Yes"
                tvRatingContent.text = movie.rating.toString()
                tvPopularityContent.text = movie.popularity.toString()
                tvTimeContent.text = movie.runtime.toString() + " min"

                if (movie.imgPath != null) {
                    Glide.with(this@MovieDetailActivity)
                        .load(baseImageUrl + movie.imgPath)
                        .into(imgMovie)
                }

                favImg.setOnClickListener() {
                    if (isLiked) {
                        if (notConnected) {
                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites1)
                                .into(favImg)
                            notifyEv(movie, false)
                        } else {
                            val body = JsonObject().apply {
                                addProperty("media_type", "movie")
                                addProperty("media_id", movie.movieId)
                                addProperty("favorite", false)
                            }

                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites1)
                                .into(favImg)
                            try {
                                movieDao?.updateMovie(movie)
                            } catch (e: Exception) {
                                Log.d("ERROR", e.toString())
                            }
                            markFavoriteCoroutines(body, movie, false)
                        }
                        isLiked = false

                    } else {
                        if (notConnected) {
                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites2)
                                .into(favImg)
                            notifyEv(movie, true)
                        } else {
                            val body = JsonObject().apply {
                                addProperty("media_type", "movie")
                                addProperty("media_id", movie.movieId)
                                addProperty("favorite", true)
                            }
                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.favorites2)
                                .into(favImg)

                            try {
                                movieDao?.updateMovie(movie)
                            } catch (e: Exception) {
                                Log.d("ERROR", e.toString())
                            }
                            markFavoriteCoroutines(body, movie, true)
                            isLiked = true
                        }
                        isLiked = true
                    }
                }
            }
        }
    }

    fun notifyEv(movie: Movie, liked: Boolean) {
        Toast.makeText(
            this,
            "You are offline! Your actions will be processed after you are back online!",
            Toast.LENGTH_SHORT
        )
            .show()
        if (liked) {
            if (!CurrentUser.offlineDislikedMovieList.isNullOrEmpty()) {
                if (CurrentUser.offlineDislikedMovieList!!.contains(movie)) {
                    CurrentUser.offlineDislikedMovieList?.remove(movie)
                }
            }
            CurrentUser.offlineLikedMovieList?.add(movie)
            CurrentUser.favoritList?.add(movie)
        } else {
            if (!CurrentUser.offlineLikedMovieList.isNullOrEmpty()) {
                if (CurrentUser.offlineLikedMovieList!!.contains(movie)) {
                    CurrentUser.offlineLikedMovieList?.remove(movie)
                }
            }
            CurrentUser.offlineDislikedMovieList?.add(movie)
            CurrentUser.favoritList?.remove(movie)
        }
    }

    //MARKING AS FAVORITE USING COROUTINES
    fun markFavoriteCoroutines(body: JsonObject, movie: Movie, liked: Boolean) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi().markAsFavoriteCoroutines(
                        CurrentUser.user?.accountId,
                        RetrofitService.getApiKey(), CurrentUser.user?.sessionId.toString(), body
                    ).await()
                    if (!response.isSuccessful) {
                        notConnected = true
                    } else {
                        Log.d("COOL", response.toString())
                    }
                } catch (e: Exception) {
                    notConnected = true
                    Log.d("ERROR", e.toString())
                }
            }

        }
    }
}