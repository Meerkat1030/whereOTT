package com.example.whereott.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.whereott.Entity.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite WHERE userId = :userId")
    fun getAll(userId: String): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE userId = :userId AND category = :category")
    fun getByCategory(userId: String, category: String): LiveData<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Favorite)
}
