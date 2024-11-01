package com.dicoding.asclepius.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1, exportSchema = false)

abstract class roomHistory: RoomDatabase() {

    abstract fun historyDao() : HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: roomHistory? = null

        fun getDatabase(context: Context): roomHistory {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    roomHistory::class.java,
                    "history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}