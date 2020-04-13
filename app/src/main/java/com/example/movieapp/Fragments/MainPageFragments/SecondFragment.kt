package com.example.movieapp.Fragments.MainPageFragments

import android.content.Intent
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//SECONDFRAGMENT
class SecondFragment : Fragment(),
    FavoriteMovieAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var favMovieAdapter: FavoriteMovieAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater?.inflate(R.layout.second_fragment, container, false)
        recyclerView = view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            favMovieAdapter?.clearAll()
            getFavoriteMovies()
        }

        favMovieAdapter =
            FavoriteMovieAdapter(
                itemClickListener = this
            )
        recyclerView.adapter = favMovieAdapter

        getFavoriteMovies()
        getGenres()

        return view
    }


    private fun getFavoriteMovies() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getMovieApi().getFavoriteMovieList(
            CurrentUser.user?.account_id,
            RetrofitService.getApiKey(), CurrentUser.user?.sessionId.toString()
        ).enqueue(object :
            Callback<MoviesResponse> {

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                Log.d("My_movie_list", response.body().toString())
                if (response.isSuccessful) {
                    val list: List<Movie>? = response.body()?.results
                    if (list != null) {
                        CurrentUser.favoritList = list
                    }
                    favMovieAdapter?.list = list
                    favMovieAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    fun getGenres() {
        RetrofitService.getMovieApi()
            .getGenres(RetrofitService.getApiKey()).enqueue(object :
                Callback<MoviesResponse> {

                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {}

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>
                ) {
                    var genres: List<MovieGenres>? = null
                    Log.d("Genres", response.body().toString())
                    if (response.isSuccessful) {
                        genres = response.body()?.genres
                        favMovieAdapter?.genreList = genres
                        favMovieAdapter?.notifyDataSetChanged()
                    }
                }
            })
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(this.activity, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.movieId)
        startActivity(intent)
    }
}