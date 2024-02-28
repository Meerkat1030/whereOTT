package com.example.whereott.ui.person

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.whereott.MainActivity.Companion.MOVIE_BACKDROP
import com.example.whereott.MainActivity.Companion.MOVIE_ID
import com.example.whereott.MainActivity.Companion.MOVIE_POSTER
import com.example.whereott.MainActivity.Companion.MOVIE_RATING
import com.example.whereott.MainActivity.Companion.MOVIE_TITLE
import com.example.whereott.MainActivity.Companion.TYPE
import com.example.whereott.R
import com.example.whereott.common.CombinedCastAdapter
import com.example.whereott.common.PersonDetail
import com.example.whereott.common.PersonRepository
import com.example.whereott.databinding.ActivityPersonDetailsBinding

class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonDetailsBinding

    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var actor_name: TextView
    private lateinit var rating: RatingBar
    private lateinit var birthDate: TextView
    private lateinit var birthPlace: TextView

    private lateinit var combinedCastAdapter: CombinedCastAdapter

    private var castPage = 1
    private var personId: Long = -1L
    private var type: String = ""
    private var tvId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.actorToolbar)

        backdrop = findViewById(R.id.actor_backdrop)
        poster = findViewById(R.id.actor_poster)
        actor_name = findViewById(R.id.actor_title)
        rating = findViewById(R.id.actor_rating)
        birthDate = findViewById(R.id.actor_birth_date)
        birthPlace = findViewById(R.id.actor_birth_place)


        // 출연작 정보를 위한 RecyclerView 초기화
        val combinedCastRecyclerView: RecyclerView = findViewById(R.id.actor_credits_recycler_view)
        val combinedCastLayoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        combinedCastRecyclerView.layoutManager = combinedCastLayoutManager
        combinedCastAdapter = CombinedCastAdapter(mutableListOf())
        combinedCastRecyclerView.adapter = combinedCastAdapter

        val extras = intent.extras

        if (extras != null) {
            personId = extras.getLong(MOVIE_ID)
            type = extras.getString(TYPE).toString()

            personDetails(extras)


        } else {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun personDetails(extras: Bundle) {
        extras.getString(MOVIE_BACKDROP)?.let { backdropPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280$backdropPath")
                .transform(CenterCrop())
                .into(backdrop)
        }

        extras.getString(MOVIE_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(poster)
        }

        actor_name.text = extras.getString(MOVIE_TITLE, "")
        rating.rating = extras.getFloat(MOVIE_RATING, 0f) / 2
        birthDate.text = "hajkhfdjklahsdlfa "//extras.getString(MOVIE_RELEASE_DATE, "")


        getPersonDetail(extras.getLong(MOVIE_ID))
        getCombinedCredits(extras.getLong(MOVIE_ID))
    }

    private fun getPersonDetail(personId: Long) {

        if (personId != -1L) {
            val personRepository = PersonRepository
            personRepository.getPersonDetail(personId,
                ::onPopularPersonFetched,
                ::onError
            )
        }
    }

    private fun getCombinedCredits(movieId: Long) {

        if (movieId != -1L) {
            val personRepository = PersonRepository
            personRepository.getPersonCombinedCredits(movieId,
                onSuccess = { cast ->
                    combinedCastAdapter.appendCombinedCast(cast)
                },
                onError = {
                    Toast.makeText(this, "getCombinedCredits", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


//    private fun getPersonCombinedCredits(cast: List<Combined_Cast>) {
//        Toast.makeText(this, " Combined_Cast", Toast.LENGTH_SHORT).show()
//    }

    private fun onPopularPersonFetched(persons: PersonDetail) {
        actor_name.text = persons.name
        birthPlace.text = persons.placeOfBirth
        if(persons.birthday != null) {
            birthDate.text = persons.birthday + " 출생"
        }else{
            birthDate.text = persons.deathday + " 사망"
        }
//        Toast.makeText(this, persons.placeOfBirth, Toast.LENGTH_SHORT).show()
    }

    private fun onError() {
        Toast.makeText(this, "error Persons", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 뒤로가기 버튼 클릭 시
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}