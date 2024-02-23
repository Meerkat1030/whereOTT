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
import com.example.whereott.ui.user.UsereditActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        const val MOVIE_BACKDROP = "extra_movie_backdrop"
        const val MOVIE_POSTER = "extra_movie_poster"
        const val MOVIE_TITLE = "extra_movie_title"
        const val MOVIE_RATING = "extra_movie_rating"
        const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
        const val MOVIE_OVERVIEW = "extra_movie_overview"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
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

        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        val idView: TextView = findViewById(R.id.id_view)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // 이미 로그인되어 있을 경우 마이페이지 레이아웃 표시
            binding.loginLayout.visibility = View.GONE
            binding.mypageLayout.visibility = View.VISIBLE

            val userId = sharedPreferences.getString("userId", "")
            if (!userId.isNullOrEmpty()) {
                val userNick = sharedPreferences.getString("username", "")
                idView.text = userNick

                val profileUriString = sharedPreferences.getString("profileImageUri", "")
                if (!profileUriString.isNullOrEmpty()) {
                    val profileUri = Uri.parse(profileUriString)
                    imageViewProfile.setImageURI(profileUri)
                    imageViewProfile.visibility = ImageView.VISIBLE
                } else {
                    imageViewProfile.visibility = ImageView.GONE
                }
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_movie, R.id.navigation_tv, R.id.navigation_actor
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.loginLayout.setOnClickListener { }
        binding.mypageLayout.setOnClickListener { }

        binding.btnLogin.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this@MainActivity, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val checkUser = userRepository.getUserByIdAndPassword(user, pass)
                    if (checkUser != null) {
                        val userNick = userRepository.getUserNickname(user)
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        sharedPreferences.edit().putString("username", userNick).apply()
                        sharedPreferences.edit().putString("userId", user).apply()
                        Log.d("전송하는 id", "$user")

                        val username = sharedPreferences.getString("username", "")
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
                                        imageViewProfile.setImageURI(profileUri)
                                        imageViewProfile.visibility = ImageView.VISIBLE
                                    } else {
                                        imageViewProfile.visibility = ImageView.GONE
                                    }
                                    editTextId.text.clear()
                                    editTextPassword.text.clear()
                                } else {
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
            binding.loginLayout.visibility = View.VISIBLE
            binding.mypageLayout.visibility = View.GONE
            editTextId.text.clear()
            editTextPassword.text.clear()
        }

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, UsereditActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
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

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}


