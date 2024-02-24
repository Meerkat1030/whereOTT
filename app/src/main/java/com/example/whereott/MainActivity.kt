package com.example.whereott

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.whereott.Repository.UserRepository
import com.example.whereott.databinding.ActivityMainBinding
import com.example.whereott.ui.user.JoinActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var btnLogin: Button
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
//    private lateinit var btnRegister: Button
    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imageViewProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        userRepository = UserRepository(this)
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

//        btnLogin = findViewById(R.id.btnLogin)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
//        btnRegister = findViewById(R.id.btnRegister)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        val idView: TextView = findViewById(R.id.id_view)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_movie, R.id.navigation_tv, R.id.navigation_actor
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // 툴바의 홈 버튼을 항상 표시
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // login_layout의 클릭 이벤트를 가로채기, 옆창 나온 상태에서 다른거 클릭 안되게
        binding.loginLayout.setOnClickListener { }
        binding.mypageLayout.setOnClickListener { }


//        binding.btnLogin.setOnClickListener {
//            // 로그인 버튼을 클릭했을 때 로그인 레이아웃을 숨기고, 대신 마이페이지 레이아웃을 표시
//            binding.loginLayout.visibility = View.GONE
//            binding.mypageLayout.visibility = View.VISIBLE
//        }
        binding.btnLogin.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this@MainActivity, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val checkUser = userRepository.getUserByIdAndPassword(user, pass)
                    // id와 password 일치시
                    if (checkUser != null) {
                        // 로그인 상태를 SharedPreferences에 저장
                        val userNick = userRepository.getUserNickname(user)
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        sharedPreferences.edit().putString("username", userNick).apply()
                        sharedPreferences.edit().putString("userId", user).apply()
                        Log.d("전송하는 id", "$user")


                        val username = sharedPreferences.getString("username", "")
                        // 프로필 사진을 데이터베이스에서 가져와서 URI로 저장
                        val profileImageUri = userRepository.getProfileImageUri(user)
                        sharedPreferences.edit().putString("profileImageUri", profileImageUri).apply()

                        launch(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                            hideKeyboard()
                            binding.loginLayout.visibility = View.GONE
                            binding.mypageLayout.visibility = View.VISIBLE
                            idView.text = userNick
                            val userId = sharedPreferences.getString("userId", "")
                            GlobalScope.launch(Dispatchers.IO) {
                                if (!userId.isNullOrEmpty()) {
                                    val profileUriString = userRepository.getProfileImageUri(userId)
                                    if (!profileUriString.isNullOrEmpty()) {
                                        val profileUri = Uri.parse(profileUriString)
                                        imageViewProfile.post {
                                            imageViewProfile.setImageURI(profileUri)
                                            imageViewProfile.visibility = ImageView.VISIBLE
                                        }
                                    } else {
                                        // 프로필 이미지 URI가 없는 경우 이미지 뷰를 숨깁니다.
                                        imageViewProfile.post {
                                            imageViewProfile.visibility = ImageView.GONE
                                        }
                                    }


                                    // Clear EditText fields after successful login
                                    editTextId.text.clear()
                                    editTextPassword.text.clear()
                                } else {
                                    // 사용자 ID가 없는 경우 처리
                                    Log.e("HomeActivity", "User ID is null or empty")
                                }
                            }
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        binding.logoutButton.setOnClickListener {
            // 로그인 버튼을 클릭했을 때 로그인 레이아웃을 숨기고, 대신 마이페이지 레이아웃을 표시
            binding.loginLayout.visibility = View.VISIBLE
            binding.mypageLayout.visibility = View.GONE
            editTextId.text.clear()
            editTextPassword.text.clear()
        }


        // 회원가입 버튼 클릭 시 JoinActivity 시작
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
            // 현재 액티비티를 종료하여 로그인 화면을 닫음
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 네비게이션 드로어 열기
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        const val MOVIE_BACKDROP = "extra_movie_backdrop"
        const val MOVIE_POSTER = "extra_movie_poster"
        const val MOVIE_TITLE = "extra_movie_title"
        const val MOVIE_RATING = "extra_movie_rating"
        const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
        const val MOVIE_OVERVIEW = "extra_movie_overview"
        const val MOVIE_ID = "extra_movie_id"
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
