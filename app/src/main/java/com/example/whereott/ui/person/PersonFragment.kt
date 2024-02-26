package com.example.whereott.ui.person

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whereott.R
import com.example.whereott.common.Person
import com.example.whereott.common.PersonRepository

class PersonFragment : Fragment() {
    lateinit var root : View

    private lateinit var popularPerson: RecyclerView
    private lateinit var popularPersonAdapter: PersonAdapter
    private lateinit var popularPersonLayoutMgr: LinearLayoutManager
    private var popularPersonPage = 1

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

        startActivity(intent)
    }
}
