package com.example.whereott.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whereott.MainActivity
import com.example.whereott.R
import com.example.whereott.common.Api
import com.example.whereott.common.GetMoviesResponse
import com.example.whereott.common.Movie
import com.example.whereott.common.MoviesRepository
import com.example.whereott.common.ProviderAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MovieFragment : Fragment() {
    private lateinit var root: View

    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularMoviesLayoutMgr: LinearLayoutManager
    private var popularMoviesPage = 1
    // SearchView 및 RecyclerView 변수 추가
    private lateinit var movieSearchView: SearchView

    private lateinit var providerAdapter: ProviderAdapter


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
        // SearchView 초기화
        movieSearchView = root.findViewById(R.id.movie_search_view)

        // SearchView 리스너 설정
        movieSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 쿼리를 처리하고 영화 또는 배우를 검색하여 결과를 표시하는 메서드 호출
                query?.let { performSearch(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 텍스트 변경 시에도 검색을 수행할 수 있도록 필요한 경우에 대비하여 onQueryTextSubmit을 호출
                newText?.let { performSearch(it) }

                // 입력된 텍스트가 없을 때는 '인기순' 텍스트를 moviePopular에 설정
                if (newText.isNullOrEmpty()) {
                    root.findViewById<TextView>(R.id.moviePopular).text = getString(R.string.popular)
                } else {
                    // 입력된 텍스트가 있을 때는 '검색결과' 텍스트를 moviePopular에 설정
                    root.findViewById<TextView>(R.id.moviePopular).text = getString(R.string.search)
                }
                return false
            }
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        // onResume() 메서드에서 인기 영화 페이지를 다시 로드
        reloadPopularMovies()
    }

    // 인기 영화 목록을 가져오는 메서드
    private fun getPopularMovies() {
        MoviesRepository().getPopularMovies(
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
        intent.putExtra(MainActivity.MOVIE_ID, movie.id)
        intent.putExtra(MainActivity.MOVIE_BACKDROP, movie.backdrop_path)
        intent.putExtra(MainActivity.MOVIE_POSTER, movie.poster_path)
        intent.putExtra(MainActivity.MOVIE_TITLE, movie.title)
        intent.putExtra(MainActivity.MOVIE_RATING, movie.rating)
        intent.putExtra(MainActivity.MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MainActivity.MOVIE_OVERVIEW, movie.overview)
        intent.putExtra(MainActivity.TYPE, "movie")
        startActivity(intent)
    }
    // Retrofit 인터페이스 구현체 생성
    private val api = RetrofitClient.createService(Api::class.java)
    // 검색을 수행하고 결과를 표시하는 메서드
    private fun performSearch(query: String) {
        // 여기서 검색된 결과에 따라 UI를 업데이트하고 필요한 작업을 수행합니다.
        // 예를 들어, 영화 검색이라면 검색된 영화를 표시하기 위해 RecyclerView에 Adapter를 설정할 수 있습니다.
        // 배우 검색이라면 해당 배우의 출연작을 표시하기 위한 작업을 수행할 수 있습니다.
        // 이는 실제 데이터 소스 및 API에 따라 달라질 수 있습니다.
        // 영화 검색 API를 호출하고 결과를 처리하는 콜백 정의
        api.searchMovies("beb95ad63fd33a8568136afbb01979a1", query, 1).enqueue(object :
            Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    // API 호출 성공 시 결과를 처리
                    val getMoviesResponse = response.body()
                    if (getMoviesResponse != null) {
                        val movies = getMoviesResponse.movies
                        updateUIWithSearchResults(movies)
                    } else {
                        // API 응답이 null인 경우 빈 목록으로 처리
                        updateUIWithSearchResults(emptyList())
                    }

                } else {
                    // API 호출 실패 시 오류 메시지 출력
                    onError()
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                // API 호출 실패 시 오류 메시지 출력
                onError()
            }
        })

    }

    // 검색 결과를 처리하고 UI를 업데이트하는 메서드
    private fun updateUIWithSearchResults(movies: List<Movie>) {
        // 검색된 영화 목록을 어댑터에 설정
        popularMoviesAdapter.clear()
        popularMoviesAdapter.appendMovies(movies)
    }


    private fun getMovieProviders(type:String , movieId: Long) {
        if (movieId != -1L) {
            val moviesRepository = MoviesRepository()
            moviesRepository.getMovieProviders(type, movieId,
                onSuccess = { providers ->
                    providerAdapter.appendProviders(providers)
                },
                onError = {
                    // 오류 처리
                }
            )
        }
    }

}