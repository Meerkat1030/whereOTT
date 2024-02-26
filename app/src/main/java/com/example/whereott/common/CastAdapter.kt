package com.example.whereott.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.whereott.R

class CastAdapter(val castList: MutableList<Cast>) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    inner class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.cast_name)
        private val characterTextView: TextView = itemView.findViewById(R.id.cast_character)
        private val profileImageView: ImageView = itemView.findViewById(R.id.cast_profile_image)

        fun bind(cast: Cast) {
            nameTextView.text = cast.name
            characterTextView.text = cast.character

            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w185${cast.profilePath}")
                .transform(CenterCrop())
                .into(profileImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(castList[position])
    }

    override fun getItemCount(): Int = castList.size

    fun appendCast(cast: List<Cast>) {
        castList.addAll(cast)
        notifyDataSetChanged()
    }
}