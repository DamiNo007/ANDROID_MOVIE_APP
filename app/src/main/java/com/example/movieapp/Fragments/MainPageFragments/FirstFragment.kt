import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieapp.*
import com.example.movieapp.API.RetrofitService
import com.example.movieapp.Activities.MovieDetailActivity
import com.example.movieapp.Adapters.RecyclerViewAdapters.MovieAdapter
import com.example.movieapp.Adapters.RecyclerViewAdapters.StoriesAdapter
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres
import com.example.movieapp.Responses.MoviesResponse
import com.example.movieapp.Responses.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public class FirstFragment : Fragment(),
    MovieAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storiesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieAdapter: MovieAdapter? = null
    private var storiesAdapter: StoriesAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view:View = inflater!!.inflate(R.layout.first_fragment,container,false)
        recyclerView = view.findViewById(R.id.recyclerView)
        storiesRecyclerView = view.findViewById(R.id.recyclerViewStories)
        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        storiesRecyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            movieAdapter?.clearAll()
            getMovies()
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

        getMovies()
        getGenres()

        return view
    }

    fun getGenres(){
        RetrofitService.getMovieApi().getGenres(
            RetrofitService.getApiKey()).enqueue(object :
            Callback<MoviesResponse> {

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {}

            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                var genres:List<MovieGenres>?=null
                Log.d("Genres", response.body().toString())
                if (response.isSuccessful) {
                    genres = response.body()?.genres
                    movieAdapter?.genreList = genres
                    movieAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getMovies() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getMovieApi().getMovieList(
            RetrofitService.getApiKey()).enqueue(object :
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
                    movieAdapter?.list = list
                    movieAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }


    private fun storyGenerator() : ArrayList<Story>{
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

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(this.activity, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.movieId)
        startActivity(intent)
    }
}