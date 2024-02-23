// UsereditActivity.kt
package com.example.whereott.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.whereott.MainActivity
import com.example.whereott.R
import com.example.whereott.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UsereditActivity : AppCompatActivity() {

    private lateinit var editTextNickname: EditText
    private lateinit var imageViewProfile: ImageView
    private lateinit var imageViewProfileBtn : Button
    private lateinit var buttonSave: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userRepository: UserRepository
    private lateinit var userId: String // 사용자 ID
    private var currentProfileUri: Uri? = null // 현재 프로필 사진 URI
    private var newProfileUri: Uri? = null // 변경된 프로필 사진 URI

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                imageViewProfile.setImageURI(selectedImageUri)
                newProfileUri = selectedImageUri
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useredit)

        editTextNickname = findViewById(R.id.editTextNickname)
        imageViewProfile = findViewById<ImageView>(R.id.imageViewProfile)
        imageViewProfileBtn = findViewById<Button>(R.id.imageViewProfileBtn)

        buttonSave = findViewById(R.id.buttonSave)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userRepository = UserRepository(this)

        // 사용자 ID 가져오기
        userId = sharedPreferences.getString("userId", "") ?: ""
        Log.d("유저 아이디 :: ", "$userId")

        // 저장된 닉네임 불러오기
        val savedNickname = sharedPreferences.getString("username", "")
        editTextNickname.setText(savedNickname)

        // 저장된 프로필 사진 불러오기
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
            } else {
                // 사용자 ID가 없는 경우 처리
                Log.e("HomeActivity", "User ID is null or empty")
            }
        }

        imageViewProfileBtn.setOnClickListener {
            openGallery()
        }

        buttonSave.setOnClickListener {
            val newNickname = editTextNickname.text.toString()
            val currentNickname = sharedPreferences.getString("username", "")
            val currentProfileUri = sharedPreferences.getString("profileUri", "")

            // 변경된 닉네임과 프로필 사진의 URI 가져오기
            val newProfileUri = getImageUriFromImageView(imageViewProfile)

            // 수정 내역에 따라 데이터베이스 업데이트 수행
            GlobalScope.launch(Dispatchers.IO) {
                if (newNickname != currentNickname && currentProfileUri != null) {
                    Log.d("수정된 닉네임1", newNickname)
                    // 닉네임과 프로필 사진 모두 변경된 경우
                    val bitmap = (imageViewProfile.drawable as BitmapDrawable).bitmap
                    val savedImageUri = saveImageToInternalStorage(this@UsereditActivity, bitmap)
                    savedImageUri?.let {
                        val savedImageUriString = savedImageUri.toString()
                        sharedPreferences.edit().putString("profileUri", savedImageUriString).apply()
                        sharedPreferences.edit().putString("username", newNickname).apply()
                        userRepository.updateNicknameAndProfileUri(userId, newNickname, savedImageUriString)
                    }
                } else if (newNickname != currentNickname) {
                    // 닉네임만 변경된 경우
                    sharedPreferences.edit().putString("username", newNickname).apply()
                    Log.d("수정된 닉네임2", newNickname)
                    userRepository.updateNickname(userId, newNickname)

                } else if (currentProfileUri != null) {
                    Log.d("수정된 닉네임3", newNickname)
                    // 프로필 사진만 변경된 경우
                    newProfileUri?.let {
                        val savedImageUriString = it.toString()
                        sharedPreferences.edit().putString("profileUri", savedImageUriString).apply()
                        userRepository.updateProfileUri(userId, savedImageUriString)

                    }
                }
                // 수정이 완료되면 로그인 상태 저장
                saveLoginState()

                // 수정이 완료되면 홈 화면으로 이동
                val intent = Intent(this@UsereditActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun getImageUriFromImageView(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val savedImageUri = saveImageToInternalStorage(this, bitmap)
            return savedImageUri
        }
        return null
    }


    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Uri? {
        // 앱 내부의 파일 디렉토리 경로 설정
        val fileDir = File(context.filesDir, "images")
        // 디렉토리가 존재하지 않으면 생성
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        // 디렉토리 내 기존 파일 목록 가져오기
        val existingFiles = fileDir.list()
        // 기본 파일 이름 설정
        val baseFileName = "re_profile_image"
        // 카운터 초기화
        var counter = 1
        var fileName = "$baseFileName.jpg"
        // 현재 카운터로 파일이 존재하는지 확인
        while (existingFiles != null && existingFiles.contains(fileName)) {
            // 파일이 존재하면 카운터를 증가시키고 새로운 파일 이름 생성
            fileName = "${baseFileName}_${counter++}.jpg"
        }
        // 생성된 파일 이름으로 파일 경로 설정
        val filePath = File(fileDir, fileName)
        try {
            // FileOutputStream를 사용하여 비트맵을 JPEG 파일로 저장
            val outputStream = FileOutputStream(filePath)
            // 비트맵을 압축하고 출력 스트림에 쓰기
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            // 파일 작업 중 오류가 발생하면 스택 추적을 출력하고 null 반환
            e.printStackTrace()
            return null
        }
        // 저장된 이미지 파일의 URI 반환
        return Uri.fromFile(filePath)
    }
    private fun saveLoginState() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }
}
