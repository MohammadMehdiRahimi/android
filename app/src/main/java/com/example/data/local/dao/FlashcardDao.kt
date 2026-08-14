package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY nextReviewDate ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE nextReviewDate <= :timestamp ORDER BY nextReviewDate ASC")
    fun getDueFlashcards(timestamp: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE category = :category ORDER BY nextReviewDate ASC")
    fun getFlashcardsByCategory(category: String): Flow<List<FlashcardEntity>>
    
    @Query("SELECT COUNT(*) FROM flashcards WHERE nextReviewDate <= :timestamp")
    fun getDueFlashcardsCount(timestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE question = :question LIMIT 1")
    suspend fun countByQuestion(question: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcardById(id: Int)
}
