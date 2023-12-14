package com.example.completionist.Quests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class QuestViewModel(application: Application, private val userId: String) : AndroidViewModel(application) {

    private val repository: QuestRepository
    val allQuests: LiveData<List<Quest>>

    init {
        val questDao = QuestDatabase.getDatabase(application).questDao()
        repository = QuestRepository(questDao)
        allQuests = repository.allQuests
    }

    fun getSortedQuestsForCurrentUser(): LiveData<List<Quest>> {
        val currentUserId = getCurrentUserId()
        return repository.getSortedQuestsForUser(currentUserId)
    }

    fun getQuestsByDateForCurrentUser(date: String): LiveData<List<Quest>> {
        val currentUserId = getCurrentUserId()
        return repository.getQuestsByDateForUser(date, currentUserId)
    }

    private fun getCurrentUserId(): String {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.uid ?: ""
    }

    fun getQuestsByDate(date: String): LiveData<List<Quest>> {
        return repository.getQuestsByDate(date)
    }

    fun getSortedQuests(): LiveData<List<Quest>> {
        return repository.getSortedQuests()
    }
}