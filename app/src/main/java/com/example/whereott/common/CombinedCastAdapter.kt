package com.example.whereott.common

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.whereott.MainActivity
import com.example.whereott.R

class CombinedCastAdapter(private val combinedCastList: MutableList<CombinedCast>) : RecyclerView.Adapter<CombinedCastAdapter.CombinedCastViewHolder>() {

    inner class CombinedCastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.filmography_title)
        private val characterTextView: TextView = itemView.findViewById(R.id.filmography_character)
        private val profileImageView: ImageView = itemView.findViewById(R.id.filmography_poster)

        fun bind(combinedCast: CombinedCast) {
            nameTextView.text = combinedCast.title
            characterTextView.text = combinedCast.character

            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w185${combinedCast.poster_path}")
                .transform(CenterCrop())
                .into(profileImageView)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CombinedCastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_combined_cast, parent, false)
        return CombinedCastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CombinedCastViewHolder, position: Int) {
        holder.bind(combinedCastList[position])
    }

    override fun getItemCount(): Int = combinedCastList.size

    fun appendCombinedCast(combinedCast: List<CombinedCast>) {
        combinedCastList.addAll(combinedCast)
        notifyDataSetChanged()
    }

}