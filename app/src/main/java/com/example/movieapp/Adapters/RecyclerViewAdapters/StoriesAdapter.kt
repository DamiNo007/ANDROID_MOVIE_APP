package com.example.movieapp.Adapters.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.Responses.Story

class StoriesAdapter(
    var list: ArrayList<Story>
) : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    private var mContext: Context? = null
    private var thisList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        this.mContext = parent.context
        return StoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_story,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val mStory = thisList!!.get(position)

        Glide.with(mContext!!)
            .load(mStory.storyImgPath)
            .into(holder.imgStory)

        holder.storyTitle.setText(mStory.title)
    }

    inner class StoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imgStory: ImageView = itemView.findViewById(R.id.imgStory)
        val storyTitle: TextView = itemView.findViewById(R.id.storyTitle)
    }

    override fun getItemCount(): Int = list?.size ?: 0
}