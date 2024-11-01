package com.dicoding.asclepius.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val imageUri: String,
    val result: String
)