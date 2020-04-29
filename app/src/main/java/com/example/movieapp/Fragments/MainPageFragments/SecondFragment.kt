package com.example.movieapp.Fragments.MainPageFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieapp.Activities.MovieDetailActivity
import com.example.movieapp.CurrentUser
import com.example.movieapp.Adapters.RecyclerViewAdapters.FavoriteMovieAdapter
import com.example.movieapp.R
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres
import com.example.movieapp.Responses.MoviesResponse
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.DAO.GenreDAO
import com.example.movieapp.DAO.MovieDAO
import com.example.movieapp.DB.MovieDB
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

//SECONDFRAGMENTDEVELOP2BRANCH
class SecondFragment : Fragment(),
    FavoriteMovieAdapter.RecyclerViewItemClick, CoroutineScope {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var favMovieAdapter: FavoriteMovieAdapter? = null
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job
    private var movieDao: MovieDAO? = null
    private var genreDao: GenreDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater?.inflate(R.layout.second_fragment, container, false)
        movieDao = MovieDB.getDB(context = this.activity!!).movieDao()
        genreDao = MovieDB.getDB(context = this.activity!!).genreDao()
        recyclerView = view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            favMovieAdapter?.clearAll()
            getGenresCoroutines()
            getFavoriteMoviesCoroutines()
        }

        favMovieAdapter =
            FavoriteMovieAdapter(
                itemClickListener = this
            )
        recyclerView.adapter = favMovieAdapter

        getFavoriteMoviesCoroutines()
        getGenresCoroutines()

        return view
    }


    override fun onResume() {
        super.onResume()
        favMovieAdapter?.clearAll()
        getGenresCoroutines()
        getFavoriteMoviesCoroutines()
    }

    //GETTING FAVORITE MOVIES USING COROUTINES
    private fun getFavoriteMoviesCoroutines() {
        launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi().getFavoriteMovieListCoroutines(
                        CurrentUser.user?.accountId,
                        RetrofitService.getApiKey(), CurrentUser.user?.sessionId.toString()
                    ).await()
                    if (response.isSuccessful) {
                        processOfflineActions()
                        val result = response.body()?.results
                        if (!result.isNullOrEmpty()) {
                            for (m in result) {
                                m.favorite = 1
                            }
                            movieDao?.updateAll(result)
                            CurrentUser.favoritList = result
                        }
                        result
                    } else {
                        movieDao?.getFavoriteMovies() ?: emptyList<Movie>()
                    }
                } catch (e: Exception) {
                    movieDao?.getFavoriteMovies() ?: emptyList<Movie>()
                }
            }

            favMovieAdapter?.list = list
            favMovieAdapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    //GETTIN GENRES USING COROUTINES
    fun getGenresCoroutines() {
        launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi()
                        .getGenresCoroutines(RetrofitService.getApiKey()).await()
                    if (response.isSuccessful) {
                        val result = response.body()?.genres
                        if (result.isNullOrEmpty()) {
                            genreDao?.insertAll(result)
                        }
                        result
                    } else {
                        genreDao?.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    genreDao?.getAll() ?: emptyList()
                }
            }
            favMovieAdapter?.genreList = list
            favMovieAdapter?.notifyDataSetChanged()
        }
    }

    fun markFavoriteCoroutines(body: JsonObject) {
        launch {
            val response = RetrofitService.getMovieApi().markAsFavoriteCoroutines(
                CurrentUser.user?.accountId,
                RetrofitService.getApiKey(), CurrentUser.user?.sessionId.toString(), body
            ).await()

            if (response.isSuccessful) {
                val favResponse = response.body()
                if (favResponse != null) {
                    //notify(favResponse!!)
                }
            }

        }
    }

    fun processOfflineActions() {
        if (!CurrentUser.offlineLikedMovieList.isNullOrEmpty()) {
            for (movie in CurrentUser.offlineLikedMovieList!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movie.movieId)
                    addProperty("favorite", true)
                }

                try {
                    movieDao?.updateMovie(movie)
                } catch (e: Exception) {
                    Log.d("ERROR", e.toString())
                }
                markFavoriteCoroutines(body)
            }
        }
        if (!CurrentUser.offlineDislikedMovieList.isNullOrEmpty()) {
            for (movie in CurrentUser.offlineDislikedMovieList!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movie.movieId)
                    addProperty("favorite", false)
                }

                try {
                    movieDao?.updateMovie(movie)
                } catch (e: Exception) {
                    Log.d("ERROR", e.toString())
                }
                markFavoriteCoroutines(body)
            }
        }
        getFavoriteMoviesCoroutines()
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(this.activity, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.movieId)
        startActivity(intent)
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