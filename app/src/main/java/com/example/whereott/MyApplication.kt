package com.example.whereott

import android.app.Application
import android.content.Context
import com.example.whereott.Database.AppDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // 앱이 처음 실행되는 경우 데이터베이스를 삭제하고 다시 생성합니다.
            applicationContext.deleteDatabase("Login.db")

            // 데이터베이스가 삭제된 것을 표시하기 위해 isFirstRun 값을 업데이트합니다.
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        }

        // 데이터베이스 초기화
        AppDatabase.getDatabase(this)
    }
}
