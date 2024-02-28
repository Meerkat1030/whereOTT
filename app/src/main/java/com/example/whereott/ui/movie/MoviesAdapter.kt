package com.example.whereott.ui.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.whereott.R
import com.example.whereott.common.Movie


class MoviesAdapter (var movies: MutableList<Movie>, var onMovieClick: (movie: Movie) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>(){

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.item_movie_poster)
        private val titleTextView: TextView = itemView.findViewById(R.id.item_movie_title)
        private val releaseDateTextView: TextView = itemView.findViewById(R.id.item_movie_releaseDate)
        private val moviestarImageView: ImageView = itemView.findViewById(R.id.moviestarImageView)
        private val overviewView: TextView = itemView.findViewById(R.id.movie_watch_provider)
        private var isStarFilled = false

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.poster_path}")
                .transform(CenterCrop())
                .into(poster)
            titleTextView.text = movie.title
            releaseDateTextView.text = movie.releaseDate
            overviewView.text = movie.overview

            // 별 이미지 클릭 시 상태 변경
            moviestarImageView.setOnClickListener {
                isStarFilled = !isStarFilled
                if (isStarFilled) {
                    // 별이 채워져있는 이미지로 변경
                    moviestarImageView.setImageResource(R.drawable.fill_color_star)
                } else {
                    // 비어있는 별 이미지로 변경
                    moviestarImageView.setImageResource(R.drawable.non_color_star)
                }
            }

            // 아이템 클릭 시 상세보기로 이동
            itemView.setOnClickListener { onMovieClick.invoke(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun appendMovies(movies: List<Movie>) {
        this.movies.addAll(movies)
        notifyItemRangeInserted(
            this.movies.size,
            movies.size - 1
        )
    }

    fun clear() {
        movies.clear()
        notifyDataSetChanged()
    }

}

