package com.example.whereott.ui.user

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.whereott.MainActivity
import com.example.whereott.R
import com.example.whereott.Repository.UserRepository
import com.example.whereott.databinding.ActivityJoinBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var editTextNick: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var imageViewProfile: ImageView

    private var profileImageUri: String? = null

    private val SELECT_IMAGE_REQUEST = 1
    private var checkId: Boolean = false
    private var checkNick: Boolean = false
    private val REQUEST_PERMISSION_CODE = 1001
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        userRepository = UserRepository(this)

        editTextId = findViewById(R.id.editTextId_Reg)
        editTextPassword = findViewById(R.id.editTextPass_Reg)
        editTextRePassword = findViewById(R.id.editTextRePass_Reg)
        editTextNick = findViewById(R.id.editTextNick_Reg)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        val btnRegister = findViewById<Button>(R.id.btnRegister_Reg)
        val btnCheckId = findViewById<Button>(R.id.btnCheckId_Reg)
        val btnCheckNick = findViewById<Button>(R.id.btnCheckNick_Reg)


        btnSelectImage.setOnClickListener {
            Log.d("으어어어어엉", "으어어어어어어어엉어ㅓㅇ")
            checkPermissionAndOpenImagePicker()
        }

        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$"

            if (user == "") {
                Toast.makeText(this@JoinActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (Pattern.matches(idPattern, user)) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val checkUsername = userRepository.getUserById(user)
                        if (checkUsername == null) {
                            checkId = true
                            runOnUiThread {
                                Toast.makeText(
                                    this@JoinActivity,
                                    "사용 가능한 아이디입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@JoinActivity,
                                    "이미 존재하는 아이디입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@JoinActivity, "아이디 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCheckNick.setOnClickListener {
            val nick = editTextNick.text.toString()
            val nickPattern = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]{2,20}$"

            if (nick == "") {
                Toast.makeText(this@JoinActivity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (Pattern.matches(nickPattern, nick)) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val checkNick = userRepository.getUserByNick(nick)
                        if (checkNick == null) {
                            this@JoinActivity.checkNick = true
                            runOnUiThread {
                                Toast.makeText(
                                    this@JoinActivity,
                                    "사용 가능한 닉네임입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@JoinActivity,
                                    "이미 존재하는 닉네임입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@JoinActivity, "닉네임 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        btnSelectImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
//        }

        btnRegister.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()
            val repass = editTextRePassword.text.toString()
            val nick = editTextNick.text.toString()

            if (user == "" || pass == "" || repass == "" || nick == "" || profileImageUri == "") {
                Toast.makeText(
                    this@JoinActivity,
                    "회원정보를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (checkId) {
                    val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,15}$"
                    if (Pattern.matches(pwPattern, pass)) {
                        if (pass == repass) {
                            if (checkNick) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    userRepository.insertUser(user, pass, nick, profileImageUri!!)
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@JoinActivity,
                                            "가입되었습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(applicationContext, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@JoinActivity,
                                    "닉네임 중복확인을 해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@JoinActivity,
                                "비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@JoinActivity,
                            "비밀번호 형식이 옳지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@JoinActivity,
                        "아이디 중복확인을 해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                // 이미지를 비트맵으로 변환하여 저장
                val inputStream = contentResolver.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                // 앱 데이터 내에 이미지를 저장하고, 해당 URI를 가져옴
                profileImageUri = saveImageToInternalStorage(this, bitmap)?.toString()
                // 이미지 뷰에 비트맵 설정
                imageViewProfile.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkPermissionAndOpenImagePicker() {


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                Log.d("dddjdfkjas;lkfjsadkl", "dsajkhksdjaghaslkdj")
                AlertDialog.Builder(this)
                    .setTitle("권한 요청")
                    .setMessage("사진을 선택하려면 외부 저장소 권한이 필요합니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            PERMISSION_REQUEST_CODE
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
                //requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGE)
            }

        } else {
            openImagePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                openImagePicker()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_IMAGE_REQUEST)
    }

    // 이미지를 앱 데이터 내부 저장소에 저장하는 함수
//    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Uri? {
//        // 앱 내부의 파일 경로 설정
//        val fileDir = File(context.filesDir, "images")
//        // 디렉토리가 존재하지 않으면 생성
//        if (!fileDir.exists()) {
//            fileDir.mkdirs()//media/picker/0/com.android.providers.media.photopicker/me
//        }
//        // 이미지 파일 이름 설정
//        val fileName = "profile_image.jpg"
//        // 파일 경로 설정
//        val filePath = File(fileDir, fileName)
//        try {
//            // 파일 스트림을 통해 비트맵을 JPEG 파일로 저장
//            val outputStream = FileOutputStream(filePath)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            return null
//        }
//        // 저장된 파일의 URI 반환
//        return Uri.fromFile(filePath)
//    }
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
        val baseFileName = "profile_image"
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

}
