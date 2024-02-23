package com.example.whereott.ui.tv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.whereott.R
import com.example.whereott.common.TV


class TVAdapter (var tvlist: MutableList<TV>, var onTVClick: (tv: TV) -> Unit
) : RecyclerView.Adapter<TVAdapter.TvViewHolder>(){

    inner class TvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.item_tv_poster)
        private val titleTextView: TextView = itemView.findViewById(R.id.item_tv_title)
        private val firstAirDateView: TextView = itemView.findViewById(R.id.item_tv_date)
        private val tvstarImageView: ImageView = itemView.findViewById(R.id.tvstarImageView)
        private var isStarFilled = false

        fun bind(tv: TV) {
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${tv.poster_path}")
                .transform(CenterCrop())
                .into(poster)
            titleTextView.text = tv.name
            firstAirDateView.text = tv.first_air_date

            // 별 이미지 클릭 시 상태 변경
            tvstarImageView.setOnClickListener {
                isStarFilled = !isStarFilled
                if (isStarFilled) {
                    // 별이 채워져있는 이미지로 변경
                    tvstarImageView.setImageResource(R.drawable.fill_color_star)
                } else {
                    // 비어있는 별 이미지로 변경
                    tvstarImageView.setImageResource(R.drawable.non_color_star)
                }
            }

            itemView.setOnClickListener { onTVClick.invoke(tv) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_tv, parent, false)
        return TvViewHolder(view)
    }

    override fun getItemCount(): Int = tvlist.size

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {
        holder.bind(tvlist[position])
    }

    fun appendTV(tvlist: List<TV>) {
        this.tvlist.addAll(tvlist)
        notifyItemRangeInserted(
            this.tvlist.size,
            tvlist.size - 1
        )
    }

    fun clear() {
        tvlist.clear()
        notifyDataSetChanged()
    }
}
