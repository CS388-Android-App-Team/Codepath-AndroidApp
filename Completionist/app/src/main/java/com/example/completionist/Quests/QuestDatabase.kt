package com.example.completionist.Quests

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Quest::class], version = 1, exportSchema = false)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: QuestDatabase? = null

        fun getDatabase(context: Context): QuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java,
                    "quest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
