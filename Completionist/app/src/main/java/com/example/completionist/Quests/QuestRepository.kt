package com.example.completionist.Quests

import androidx.lifecycle.LiveData

// QuestRepository.kt
class QuestRepository(private val questDao: QuestDao) {
    val allQuests: LiveData<List<Quest>> = questDao.getAllQuests()

    fun getQuestsByDate(date: String): LiveData<List<Quest>> {
        return questDao.getQuestsByDate(date)
    }

    fun getSortedQuests(): LiveData<List<Quest>> {
        return questDao.getSortedQuests()
    }

    fun getSortedQuestsForUser(userId: String): LiveData<List<Quest>> {
        return questDao.getSortedQuestsForUser(userId)
    }

    fun getQuestsByDateForUser(date: String, userId: String): LiveData<List<Quest>> {
        return questDao.getQuestsByDateForUser(date, userId)
    }
}
