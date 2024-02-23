package com.example.whereott.ui.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whereott.MainActivity
import com.example.whereott.R
import com.example.whereott.databinding.ActivityJoinBinding
import com.example.whereott.databinding.ActivityUsereditBinding

class UsereditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsereditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useredit)


        binding.EditSummitButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            // 현재 액티비티를 종료하여 로그인 화면을 닫음
            finish()
        }
    }
}