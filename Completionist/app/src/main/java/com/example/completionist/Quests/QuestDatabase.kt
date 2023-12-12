// QuestDatabase.kt
package com.example.completionist.Quests

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.completionist.User
import com.example.completionist.UserDao

@Database(entities = [Quest::class, User::class], version = 3, exportSchema = false)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: QuestDatabase? = null

        fun getDatabase(context: Context): QuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java,
                    "quest_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}