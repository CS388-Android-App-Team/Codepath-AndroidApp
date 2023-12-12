package com.example.completionist.Quests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuestRepository
    val allQuests: LiveData<List<Quest>>

    init {
        val questDao = QuestDatabase.getDatabase(application).questDao()
        repository = QuestRepository(questDao)
        allQuests = repository.allQuests
    }

    fun getQuestsByDate(date: String): LiveData<List<Quest>> {
        return repository.getQuestsByDate(date)
    }

    fun getSortedQuests(): LiveData<List<Quest>> {
        return repository.getSortedQuests()
    }

    fun insert(quest: Quest) {
        viewModelScope.launch {
            repository.insert(quest)
        }
    }
}