package com.example.completionist.Quests

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add any migration logic if needed
    }
}

// Add additional migrations as needed
