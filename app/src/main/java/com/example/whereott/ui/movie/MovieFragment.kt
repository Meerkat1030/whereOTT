package com.example.whereott.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whereott.MainActivity
import com.example.whereott.common.Movie
import com.example.whereott.common.MoviesRepository


class MovieFragment : Fragment() {
    private lateinit var root: View

    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularMoviesLayoutMgr: LinearLayoutManager
    private var popularMoviesPage = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Fragment의 레이아웃을 inflate하여 root에 할당
        root = inflater.inflate(com.example.whereott.R.layout.fragment_movie, container, false)

        // RecyclerView 초기화
        popularMovies = root.findViewById(com.example.whereott.R.id.popular_movies)
        popularMoviesLayoutMgr = LinearLayoutManager(context)
        popularMovies.layoutManager = popularMoviesLayoutMgr

        // MoviesAdapter 초기화
        popularMoviesAdapter = MoviesAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }
        popularMovies.adapter = popularMoviesAdapter


        // 인기 영화 목록 가져오기
        getPopularMovies()

        return root
    }

    override fun onResume() {
        super.onResume()
        // onResume() 메서드에서 인기 영화 페이지를 다시 로드
        reloadPopularMovies()
    }

    // 인기 영화 목록을 가져오는 메서드
    private fun getPopularMovies() {
        MoviesRepository.getPopularMovies(
            popularMoviesPage,
            ::onPopularMoviesFetched,
            ::onError
        )
    }

    // 인기 영화 목록 가져오기 성공 시 호출되는 콜백
    private fun onPopularMoviesFetched(movies: List<Movie>) {
        // 가져온 영화 목록을 어댑터에 추가
        popularMoviesAdapter.appendMovies(movies)
        // 스크롤 리스너 추가
        attachPopularMoviesOnScrollListener()
    }

    // 스크롤 리스너를 RecyclerView에 추가하는 메서드
    private fun attachPopularMoviesOnScrollListener() {
        popularMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = popularMoviesLayoutMgr.itemCount
                val visibleItemCount = popularMoviesLayoutMgr.childCount
                val firstVisibleItem = popularMoviesLayoutMgr.findFirstVisibleItemPosition()

                // 화면의 하단에 도달하면 추가 데이터를 로드
                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    popularMoviesPage++
                    getPopularMovies()
                }
            }
        })
    }

    // 인기 영화 목록을 다시 로드하는 메서드
    private fun reloadPopularMovies() {
        // 이전 페이지의 영화 목록을 비우고 새로운 페이지를 가져오기 위해 어댑터의 clear() 메서드 호출
        popularMoviesAdapter.clear()
        popularMoviesPage = 1 // 페이지를 초기화
        getPopularMovies()
    }


    // 네트워크 오류 등으로 가져오기 실패 시 호출되는 콜백
    private fun onError() {
        Toast.makeText(activity, "Failed to fetch movies", Toast.LENGTH_SHORT).show()
    }

    // 영화 상세보기 인텐트 추가
    private fun showMovieDetails(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra(MainActivity.MOVIE_BACKDROP, movie.backdrop_path)
        intent.putExtra(MainActivity.MOVIE_POSTER, movie.poster_path)
        intent.putExtra(MainActivity.MOVIE_TITLE, movie.title)
        intent.putExtra(MainActivity.MOVIE_RATING, movie.rating)
        intent.putExtra(MainActivity.MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MainActivity.MOVIE_OVERVIEW, movie.overview)
        startActivity(intent)
    }


}
