package com.example.whereott.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val password: String,
    val nick: String,
    val profile_image_uri: String
)
