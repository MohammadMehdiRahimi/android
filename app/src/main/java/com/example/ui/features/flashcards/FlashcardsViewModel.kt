package com.example.ui.features.flashcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DatabaseBuilder
import com.example.data.local.entity.FlashcardEntity
import com.example.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository

    init {
        val flashcardDao = DatabaseBuilder.getInstance(application).flashcardDao()
        repository = FlashcardRepository(flashcardDao)
    }

    val dueFlashcards: StateFlow<List<FlashcardEntity>> = repository.getDueFlashcards()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allFlashcards: StateFlow<List<FlashcardEntity>> = repository.allFlashcards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dueCount: StateFlow<Int> = repository.getDueFlashcardsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val categories: StateFlow<List<String>> = repository.allFlashcards
        .map { cards -> cards.map { it.category }.distinct() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun markAsAgain(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.markAsAgain(flashcard)
        }
    }

    fun markAsHard(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.markAsHard(flashcard)
        }
    }

    fun markAsGood(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.markAsGood(flashcard)
        }
    }

    fun markAsEasy(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.markAsEasy(flashcard)
        }
    }

    fun deleteCard(flashcardId: Int) {
        viewModelScope.launch {
            repository.deleteById(flashcardId)
        }
    }

    fun saveCard(flashcard: FlashcardEntity, onSaved: () -> Unit) {
        viewModelScope.launch {
            if (!repository.exists(flashcard.question)) {
                repository.insert(flashcard)
            }
            onSaved()
        }
    }
}
