import android.content.Context
import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movieapp.*
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.Activities.MovieDetailActivity
import com.example.movieapp.Adapters.RecyclerViewAdapters.MovieAdapter
import com.example.movieapp.Adapters.RecyclerViewAdapters.StoriesAdapter
import com.example.movieapp.DAO.GenreDAO
import com.example.movieapp.DAO.MovieDAO
import com.example.movieapp.DB.MovieDB
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres
import com.example.movieapp.Responses.MoviesResponse
import com.example.movieapp.Responses.Story
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

//FIRSTFRAGMENTDEVELOP2BRANCH
public class FirstFragment : Fragment(),
    MovieAdapter.RecyclerViewItemClick, CoroutineScope {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storiesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieAdapter: MovieAdapter? = null
    private var storiesAdapter: StoriesAdapter? = null
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

        var view: View = inflater?.inflate(R.layout.first_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        storiesRecyclerView = view.findViewById(R.id.recyclerViewStories)
        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        storiesRecyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)

        movieDao = MovieDB.getDB(context = this.activity!!).movieDao()
        genreDao = MovieDB.getDB(context = this.activity!!).genreDao()
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            movieAdapter?.clearAll()
            getGenresCoroutines()
            getMoviesCoroutines()
        }

        movieAdapter =
            MovieAdapter(
                itemClickListener = this
            )
        storiesAdapter =
            StoriesAdapter(
                storyGenerator()
            )
        recyclerView.adapter = movieAdapter
        storiesRecyclerView.adapter = storiesAdapter

        getGenresCoroutines()
        getMoviesCoroutines()

        return view
    }

    //GETTIN GENRES USING COROUTINES
    fun getGenresCoroutines() {
        launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi()
                        .getGenresCoroutines(RetrofitService.getApiKey()).await()
                    if (response.isSuccessful) {
                        CurrentUser.favoritList?.clear()
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

            movieAdapter?.genreList = list
            movieAdapter?.notifyDataSetChanged()
        }
    }

    //GETTING MOVIES USING COROUTINES
    private fun getMoviesCoroutines() {
        swipeRefreshLayout.isRefreshing = true
        launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi()
                        .getMovieListCoroutines(RetrofitService.getApiKey()).await()
                    if (response.isSuccessful) {
                        processOfflineActions()
                        val result = response.body()?.results
                        if (!result.isNullOrEmpty()) {
                            movieDao?.insertAll(result)
                        }
                        result
                    } else {
                        movieDao?.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getAll() ?: emptyList()
                }
            }
            movieAdapter?.list = list
            movieAdapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun storyGenerator(): ArrayList<Story> {
        var listStories = Stories.stories
        listStories.add(
            Story(
                1,
                "Премьеры",
                R.drawable.a2
            )
        )
        listStories.add(
            Story(
                2,
                "В Топе",
                R.drawable.a3
            )
        )
        listStories.add(
            Story(
                3,
                "Лучшее",
                R.drawable.a4
            )
        )
        listStories.add(
            Story(
                4,
                "Недавние",
                R.drawable.a5
            )
        )
        listStories.add(
            Story(
                5,
                "Всех Времен",
                R.drawable.a6
            )
        )
        listStories.add(
            Story(
                6,
                "Премьеры",
                R.drawable.a2
            )
        )
        listStories.add(
            Story(
                7,
                "В Топе",
                R.drawable.a3
            )
        )
        listStories.add(
            Story(
                8,
                "Лучшее",
                R.drawable.a4
            )
        )
        listStories.add(
            Story(
                9,
                "Недавние",
                R.drawable.a5
            )
        )
        listStories.add(
            Story(
                10,
                "Всех Времен",
                R.drawable.a6
            )
        )
        return listStories
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