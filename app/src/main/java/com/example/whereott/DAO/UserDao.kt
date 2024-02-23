package com.example.whereott.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.whereott.Entity.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE nick = :nick")
    fun getUserByNick(nick: String): User?

    @Query("SELECT * FROM users WHERE id = :id AND password = :password")
    fun getUserByIdAndPassword(id: String, password: String): User?

    @Query("SELECT profile_image_uri FROM users WHERE id = :id")
    fun getProfileImageUri(id: String): String?


    @Insert
    fun insertUser(user: User)

    @Query("UPDATE users SET nick = :newNickname WHERE id = :id")
    fun updateNickname(id: String, newNickname: String)

    @Query("UPDATE users SET profile_image_uri = :newProfileUri WHERE id = :id")
    fun updateProfileUri(id: String, newProfileUri: String)

    @Query("UPDATE users SET nick = :newNickname, profile_image_uri = :newProfileUri WHERE id = :id")
    fun updateNicknameAndProfileUri(id: String, newNickname: String, newProfileUri: String)

    @Query("SELECT nick FROM users WHERE id = :id")
    suspend fun getUserNickname(id: String): String

    @Query("SELECT id FROM users WHERE nick = :nick")
    suspend fun getUserIdByNick(nick: String): String?


}
