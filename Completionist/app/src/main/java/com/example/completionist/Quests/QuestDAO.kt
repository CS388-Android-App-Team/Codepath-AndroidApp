package com.example.completionist.Quests

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface QuestDao {
    @Query("SELECT * FROM quests WHERE quest_date = :date")
    fun getQuestsByDate(date: String): LiveData<List<Quest>>

    @Query("SELECT * FROM quests ORDER BY quest_date ASC")
    fun getSortedQuests(): LiveData<List<Quest>>

    @Query("SELECT * FROM quests WHERE userId = :userId ORDER BY quest_date ASC")
    fun getSortedQuestsForUser(userId: String): LiveData<List<Quest>>

    @Query("SELECT * FROM quests WHERE quest_date = :date AND userId = :userId ORDER BY quest_date ASC")
    fun getQuestsByDateForUser(date: String, userId: String): LiveData<List<Quest>>

    @Query("SELECT * FROM quests")
    fun getAllQuests(): LiveData<List<Quest>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quest: Quest)

    @Delete
    suspend fun delete(quest: Quest)

    @Update
    suspend fun update(quest: Quest)
}