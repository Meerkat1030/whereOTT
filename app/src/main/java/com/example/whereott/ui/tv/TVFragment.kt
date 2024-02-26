package com.example.whereott.ui.tv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whereott.MainActivity
import com.example.whereott.R
import com.example.whereott.common.Api
import com.example.whereott.common.GetTVResponse
import com.example.whereott.common.TV
import com.example.whereott.common.TVRepository
import com.example.whereott.ui.movie.MovieDetailsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TVFragment : Fragment() {
    lateinit var root : View

    private lateinit var popularTV: RecyclerView
    private lateinit var popularTVAdapter: TVAdapter
    private lateinit var popularTVLayoutMgr: LinearLayoutManager
    private var popularTVPage = 1
    // SearchView 및 RecyclerView 변수 추가
    private lateinit var tvSearchView: SearchView

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        root = inflater.inflate(R.layout.fragment_tv, container, false)

        popularTV = root.findViewById(R.id.popular_tv)
        popularTVLayoutMgr = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        popularTV.layoutManager = popularTVLayoutMgr
        popularTVAdapter = TVAdapter(mutableListOf()) { tv -> showTVDetails(tv) }
        popularTV.adapter = popularTVAdapter


        getPopularTV()
        tvSearchView = root.findViewById(R.id.tv_search_view)
        // SearchView 리스너 설정
        tvSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 쿼리를 처리하고 영화 또는 배우를 검색하여 결과를 표시하는 메서드 호출
                query?.let { performSearch(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 텍스트 변경 시에도 검색을 수행할 수 있도록 필요한 경우에 대비하여 onQueryTextSubmit을 호출
                newText?.let { performSearch(it) }
                return false
            }
        })

        return root
    }


    private fun getPopularTV() {
        TVRepository.getPopularTV(
            popularTVPage,
            ::onPopularTVFetched,
            ::onError
        )
    }

    private fun onPopularTVFetched(tvlist: List<TV>) {
        popularTVAdapter.appendTV(tvlist)
        attachPopularTVOnScrollListener()
    }

    private fun attachPopularTVOnScrollListener() {
        popularTV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = popularTVLayoutMgr.itemCount
                val visibleItemCount = popularTVLayoutMgr.childCount
                val firstVisibleItem = popularTVLayoutMgr.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    popularTV.removeOnScrollListener(this)
                    popularTVPage++
                    getPopularTV()
                }
            }
        })
    }


    private fun onError() {
        Toast.makeText(activity, "error TVList", Toast.LENGTH_SHORT).show()
    }

    private fun showTVDetails(tv: TV) {
//        MovieDetailsActivity에 TV 요소 적용
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra(MainActivity.TV_ID, tv.id)
        intent.putExtra(MainActivity.MOVIE_BACKDROP, tv.backdrop_path)
        intent.putExtra(MainActivity.MOVIE_POSTER, tv.poster_path)
        intent.putExtra(MainActivity.MOVIE_TITLE, tv.name)
        intent.putExtra(MainActivity.MOVIE_RATING, tv.rating)
        intent.putExtra(MainActivity.MOVIE_RELEASE_DATE, tv.first_air_date)
        intent.putExtra(MainActivity.MOVIE_OVERVIEW, tv.overview)
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
        api.searchTV("beb95ad63fd33a8568136afbb01979a1", query, 1).enqueue(object :
            Callback<GetTVResponse> {
            override fun onResponse(call: Call<GetTVResponse>, response: Response<GetTVResponse>) {
                if (response.isSuccessful) {
                    // API 호출 성공 시 결과를 처리
                    val getTVResponse = response.body()
                    if (getTVResponse != null) {
                        val tv = getTVResponse.tvlist
                        updateUIWithSearchResults(tv)
                    } else {
                        // API 응답이 null인 경우 빈 목록으로 처리
                        updateUIWithSearchResults(emptyList())
                    }

                } else {
                    // API 호출 실패 시 오류 메시지 출력
                    onError()
                }
            }

            override fun onFailure(call: Call<GetTVResponse>, t: Throwable) {
                // API 호출 실패 시 오류 메시지 출력
                onError()
            }
        })

    }

    // 검색 결과를 처리하고 UI를 업데이트하는 메서드
    private fun updateUIWithSearchResults(tv: List<TV>) {
        // 검색된 영화 목록을 어댑터에 설정
        popularTVAdapter.clear()
        popularTVAdapter.appendTV(tv)
    }
}
