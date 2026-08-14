package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val optionsJson: String,
    val answer: Int,
    val explanation: String,
    val category: String, // e.g. Book name
    val boxNumber: Int = 1, // Leitner box number (1 to 5)
    val nextReviewDate: Long = System.currentTimeMillis()
)
