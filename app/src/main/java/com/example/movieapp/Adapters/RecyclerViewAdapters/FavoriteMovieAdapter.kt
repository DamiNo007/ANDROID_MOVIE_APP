package com.example.movieapp.Adapters.RecyclerViewAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieGenres
import java.lang.Exception

//FAVORITEMOVIEADAPTERDEVELOP2BRANCH
class FavoriteMovieAdapter(
    var list: List<Movie>? = null,
    var genreList: List<MovieGenres>? = null,
    val itemClickListener: RecyclerViewItemClick? = null

) : RecyclerView.Adapter<FavoriteMovieAdapter.FavoriteMovieViewHolder>() {

    private var context: Context? = null
    private val baseImageUrl: String = "https://image.tmdb.org/t/p/w500"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        this.context = parent.context

        return FavoriteMovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_fav_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val movie = list?.get(position)

        if (movie?.imgPath != null) {
            Glide.with(context!!)
                .load(baseImageUrl + movie.imgPath)
                .into(holder.imgMovie)
        }

        var cnt: Int = 0
        holder.tvTitle.setText("Title: ")
        holder.tvGenre.setText("Genre: ")
        holder.tvDate.setText("Date: ")
        holder.tvRating.setText("Rating: ")

        if (movie?.title != null) {
            holder.tvTitleContent.setText(movie.title)
        }

        if (movie?.date != null) {
            holder.tvDateContent.text = movie.date.toString()
        }

        if (movie?.rating != null) {
            holder.tvRatingContent.text = movie.rating.toString()
        }
        var id: Int? = 0
        if (movie?.genreIds != null) {
            try {
                id = movie?.genreIds.get(0)
            } catch (e: Exception) {
                Log.d("Exception:", e.toString())
            }

            if (id != null) {
                for (genre in genreList!!) {
                    if (genre.genreId === id) {
                        holder.tvGenreContent.text = genre.genreName
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.itemClick(movie?.movieId!!, movie!!)
        }
    }

    fun clearAll() {
        (list as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class FavoriteMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMovie: ImageView = itemView.findViewById(R.id.imgMovie)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvTitleContent: TextView = itemView.findViewById(R.id.tvTitleContent)
        val tvGenreContent: TextView = itemView.findViewById(R.id.tvGenreContent)
        val tvDateContent: TextView = itemView.findViewById(R.id.tvDateContent)
        val tvRatingContent: TextView = itemView.findViewById(R.id.tvRatingContent)
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
    }

    override fun getItemCount(): Int = list?.size ?: 0
}
