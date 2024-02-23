package com.example.whereott.Repository

import android.content.Context
import androidx.room.Room
import com.example.whereott.Database.AppDatabase
import com.example.whereott.Entity.User

class UserRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "Login.db"
    ).build()

    private val userDao = db.userDao()

    suspend fun insertUser(id: String, password: String, nick: String, profileImageUri: String) {
        val user = User(id, password, nick, profileImageUri)
        userDao.insertUser(user)
    }

    suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)
    }

    suspend fun getUserByNick(nick: String): User? {
        return userDao.getUserByNick(nick)
    }

    suspend fun getUserByIdAndPassword(id: String, password: String): User? {
        return userDao.getUserByIdAndPassword(id, password)
    }
    suspend fun getProfileImageUri(userId: String): String? {
        return userDao.getProfileImageUri(userId)
    }

//    suspend fun updateNickname(id: String, newNickname: String) {
//        userDao.updateNickname(id, newNickname)
//    }
suspend fun updateNickname(id: String, newNickname: String) {
    userDao.updateNickname(id, newNickname)
}

    suspend fun updateProfileUri(id: String, newProfileUri: String) {
        userDao.updateProfileUri(id, newProfileUri)
    }

    suspend fun updateNicknameAndProfileUri(id: String, newNickname: String, newProfileUri: String) {
        userDao.updateNicknameAndProfileUri(id, newNickname, newProfileUri)
    }

    suspend fun getUserNickname(id: String): String {
        return userDao.getUserNickname(id)
    }

    suspend fun getUserIdByNick(nick: String): String? {
        return userDao.getUserIdByNick(nick)
    }
}
