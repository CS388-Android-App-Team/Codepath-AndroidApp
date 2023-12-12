package com.example.completionist.Quests

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface QuestDao {
    @Transaction
    @Insert
    suspend fun insert(quest: Quest)

    @Transaction
    @Update
    suspend fun update(quest: Quest)

    @Transaction
    @Delete
    suspend fun delete(quest: Quest)

    @Transaction
    @Query("SELECT * FROM quests")
    suspend fun getAllQuests(): List<Quest>
}