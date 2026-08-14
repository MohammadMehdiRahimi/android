package com.example.data.repository

import com.example.data.local.dao.FlashcardDao
import com.example.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    val allFlashcards: Flow<List<FlashcardEntity>> = flashcardDao.getAllFlashcards()

    fun getDueFlashcards(): Flow<List<FlashcardEntity>> {
        val now = System.currentTimeMillis()
        return flashcardDao.getDueFlashcards(now)
    }

    fun getDueFlashcardsCount(): Flow<Int> {
        val now = System.currentTimeMillis()
        return flashcardDao.getDueFlashcardsCount(now)
    }

    suspend fun exists(question: String): Boolean {
        return flashcardDao.countByQuestion(question) > 0
    }

    suspend fun insert(flashcard: FlashcardEntity) {
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun update(flashcard: FlashcardEntity) {
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun markAsAgain(flashcard: FlashcardEntity) {
        val nextReview = calculateNextReviewDate(1)
        val updatedCard = flashcard.copy(boxNumber = 1, nextReviewDate = nextReview)
        update(updatedCard)
    }

    suspend fun markAsHard(flashcard: FlashcardEntity) {
        // Keeps the same box number, but updates review date based on it
        val nextReview = calculateNextReviewDate(flashcard.boxNumber)
        val updatedCard = flashcard.copy(nextReviewDate = nextReview)
        update(updatedCard)
    }

    suspend fun markAsGood(flashcard: FlashcardEntity) {
        val nextBox = if (flashcard.boxNumber < 5) flashcard.boxNumber + 1 else 5
        val nextReview = calculateNextReviewDate(nextBox)
        val updatedCard = flashcard.copy(boxNumber = nextBox, nextReviewDate = nextReview)
        update(updatedCard)
    }

    suspend fun markAsEasy(flashcard: FlashcardEntity) {
        val nextBox = if (flashcard.boxNumber < 4) flashcard.boxNumber + 2 else 5
        val nextReview = calculateNextReviewDate(nextBox)
        val updatedCard = flashcard.copy(boxNumber = nextBox, nextReviewDate = nextReview)
        update(updatedCard)
    }

    suspend fun deleteById(id: Int) {
        flashcardDao.deleteFlashcardById(id)
    }

    private fun calculateNextReviewDate(boxNumber: Int): Long {
        val calendar = Calendar.getInstance()
        val daysToAdd = when (boxNumber) {
            1 -> 1 // 1 day
            2 -> 2 // 2 days
            3 -> 4 // 4 days
            4 -> 8 // 8 days
            5 -> 15 // 15 days
            else -> 1
        }
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        return calendar.timeInMillis
    }
}
