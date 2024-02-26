package com.example.whereott.ui.person

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.whereott.R
import com.example.whereott.databinding.ActivityMovieDetailsBinding
import com.example.whereott.databinding.ActivityPersonDetailsBinding

class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonDetailsBinding

    private lateinit var poster: ImageView
    private lateinit var name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        poster = findViewById(R.id.person_img)
        name = findViewById(R.id.person_name)


    }
}