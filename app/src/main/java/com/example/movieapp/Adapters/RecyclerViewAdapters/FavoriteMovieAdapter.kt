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

class FavoriteMovieAdapter(
    var list: List<Movie>? = null,
    var genreList: List<MovieGenres>? = null,
    val itemClickListener: RecyclerViewItemClick? = null

) : RecyclerView.Adapter<FavoriteMovieAdapter.FavoriteMovieViewHolder>() {

    private var mContext: Context? = null
    private val baseImageUrl:String = "https://image.tmdb.org/t/p/w500"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        this.mContext = parent.context

        return FavoriteMovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_fav_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val mMovie = list!!.get(position)

        if(mMovie.imgPath != null){
            Glide.with(mContext!!)
                .load(baseImageUrl + mMovie.imgPath)
                .into(holder.imgMovie)
        }

        var cnt:Int=0
        holder.tvTitle.setText("Title: ")
        holder.tvGenre.setText("Genre: ")
        holder.tvDate.setText("Date: ")
        holder.tvRating.setText("Rating: ")

        if(mMovie.title != null){
            holder.tvTitleContent.setText(mMovie.title)
        }

        if(mMovie.date != null){
            holder.tvDateContent.text = mMovie.date.toString()
        }

        if(mMovie.rating!=null){
            holder.tvRatingContent.text = mMovie.rating.toString()
        }
        var id: Int?=0
        if(mMovie.genreIds != null){
            try{
                id = mMovie.genreIds.get(0)
            }catch(e:Exception){
                Log.d("Exception:",e.toString())
            }

            if (id != null) {
                for(genre in genreList!!){
                    if (genre.genreId===id){
                        holder.tvGenreContent.text =  genre.genreName
                    }
                }
            }
        }

        holder.itemView.setOnClickListener{
            itemClickListener?.itemClick(mMovie.movieId!!, mMovie!!)
        }
    }

    fun clearAll() {
        (list as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class FavoriteMovieViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
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
