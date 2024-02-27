package com.example.whereott.Repository

import androidx.lifecycle.LiveData
import com.example.whereott.DAO.FavoriteDao
import com.example.whereott.Entity.Favorite

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    fun getAllFavoriteMovies(userId: String): LiveData<List<Favorite>> {
        return favoriteDao.getAll(userId)
    }

    fun getFavoriteMoviesByCategory(userId: String, category: String): LiveData<List<Favorite>> {
        return favoriteDao.getByCategory(userId, category)
    }

    suspend fun insert(movie: Favorite) {
        favoriteDao.insert(movie)
    }
}