package com.example.whereott.ui.person

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
import com.example.whereott.common.GetPersonResponse
import com.example.whereott.common.Movie
import com.example.whereott.common.Person
import com.example.whereott.common.PersonRepository
import com.example.whereott.common.TVRepository.getPopularTV
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonFragment : Fragment() {
    lateinit var root : View

    private lateinit var popularPerson: RecyclerView
    private lateinit var popularPersonAdapter: PersonAdapter
    private lateinit var popularPersonLayoutMgr: LinearLayoutManager
    private var popularPersonPage = 1
    // SearchView 및 RecyclerView 변수 추가
    private lateinit var personSearchView: SearchView

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        root = inflater.inflate(R.layout.fragment_person, container, false)

        popularPerson = root.findViewById(R.id.popular_person)
        popularPersonLayoutMgr = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        popularPerson.layoutManager = popularPersonLayoutMgr
        popularPersonAdapter = PersonAdapter(mutableListOf()) { person -> showPersonDetails(person) }
        popularPerson.adapter = popularPersonAdapter


        getPopularPerson()
        // SearchView 초기화
        personSearchView = root.findViewById(R.id.person_search_view)

        // SearchView 리스너 설정
        personSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    reloadPopularPerson()
                    root.findViewById<TextView>(R.id.personPopular).text = getString(R.string.popular)
                } else {
                    // 입력된 텍스트가 있을 때는 '검색결과' 텍스트를 moviePopular에 설정
                    root.findViewById<TextView>(R.id.personPopular).text = getString(R.string.search)
                }

                return false
            }
        })
        return root
    }


    private fun getPopularPerson() {
        PersonRepository.getPopularPerson (
            popularPersonPage,
            ::onPopularPersonFetched,
            ::onError
        )
    }

    private fun onPopularPersonFetched(persons: List<Person>) {
        popularPersonAdapter.appendPerson(persons)
        attachPopularPersonOnScrollListener()
    }

    private fun attachPopularPersonOnScrollListener() {
        popularPerson.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = popularPersonLayoutMgr.itemCount
                val visibleItemCount = popularPersonLayoutMgr.childCount
                val firstVisibleItem = popularPersonLayoutMgr.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    popularPerson.removeOnScrollListener(this)
                    popularPersonPage++
                    getPopularPerson()
                }
            }
        })
    }


    private fun onError() {
        Toast.makeText(activity, "error Persons", Toast.LENGTH_SHORT).show()
    }

    private fun showPersonDetails(person: Person) {
        val intent = Intent(activity, PersonDetailsActivity::class.java)
        intent.putExtra(MainActivity.PERSON_BIRTHDAY, person.birthday)
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
        api.searchPerson("beb95ad63fd33a8568136afbb01979a1", query, 1).enqueue(object :
            Callback<GetPersonResponse> {
            override fun onResponse(call: Call<GetPersonResponse>, response: Response<GetPersonResponse>) {
                if (response.isSuccessful) {
                    // API 호출 성공 시 결과를 처리
                    val GetPersonResponse = response.body()
                    if (GetPersonResponse != null) {
                        val persons = GetPersonResponse.persons
                        updateUIWithSearchResults(persons)
                    } else {
                        // API 응답이 null인 경우 빈 목록으로 처리
                        updateUIWithSearchResults(emptyList())
                    }

                } else {
                    // API 호출 실패 시 오류 메시지 출력
                    onError()
                }
            }

            override fun onFailure(call: Call<GetPersonResponse>, t: Throwable) {
                // API 호출 실패 시 오류 메시지 출력
                onError()
            }
        })

    }

    // 검색 결과를 처리하고 UI를 업데이트하는 메서드
    private fun updateUIWithSearchResults(persons: List<Person>) {
        // 검색된 영화 목록을 어댑터에 설정
        popularPersonAdapter.clear()
        popularPersonAdapter.appendPerson(persons)
    }


    private fun reloadPopularPerson() {
        // 이전 페이지의 영화 목록을 비우고 새로운 페이지를 가져오기 위해 어댑터의 clear() 메서드 호출
        popularPersonAdapter.clear()
        popularPersonPage = 1 // 페이지를 초기화
        getPopularPerson()
    }
}