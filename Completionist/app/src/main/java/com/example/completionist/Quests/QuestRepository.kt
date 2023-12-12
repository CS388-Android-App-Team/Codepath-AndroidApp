package com.example.completionist.Quests

import androidx.lifecycle.LiveData
import com.example.completionist.User

// QuestRepository.kt
class QuestRepository(private val questDao: QuestDao) {
    val allQuests: LiveData<List<Quest>> = questDao.getAllQuests()

    fun getQuestsByDate(date: String): LiveData<List<Quest>> {
        return questDao.getQuestsByDate(date)
    }

    suspend fun insert(quest: Quest) {
        questDao.insert(quest)
    }
}
