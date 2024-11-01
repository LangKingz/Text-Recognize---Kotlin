package com.dicoding.asclepius.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: History)

    @Query("SELECT * FROM history ORDER BY id DESC")
    suspend fun getAll(): List<History>

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}