// QuestDatabase.kt
package com.example.completionist.Quests

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.completionist.User
import com.example.completionist.UserDao

@Database(entities = [Quest::class, User::class], version = 4, exportSchema = false)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: QuestDatabase? = null

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Your migration logic here
                // For example, if you added a userId column, you can use the following SQL:
                database.execSQL("ALTER TABLE quests ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): QuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java,
                    "quest_database"
                )
                    .fallbackToDestructiveMigration() // Be careful with this during development
                    .addMigrations(MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
