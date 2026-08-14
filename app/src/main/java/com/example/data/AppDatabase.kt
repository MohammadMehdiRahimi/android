package com.example.data

import androidx.room.*
import com.example.data.local.dao.FlashcardDao
import com.example.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timeLimit: String,
    val isCompleted: Boolean,
    val subject: String,
    val dateStr: String,
    val completedCycles: Int = 0,
    val totalCycles: Int = 1,
    val focusDuration: Int = 60,
    val restDuration: Int = 15
)

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM tasks WHERE dateStr = :date")
    fun getTasksByDate(date: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<StudyTaskEntity?>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTasksCount(): Flow<Int>

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    @Query("DELETE FROM tasks WHERE dateStr = :date")
    suspend fun deleteTasksByDate(date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)
}

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val dateStr: String,
    val status: String, // "در حال بررسی" (In progress), "پاسخ داده شده" (Answered), "بسته شده" (Closed)
    val conversation: String // format: "sender:message_text<split>sender:message_text"
)

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    fun getTicketById(ticketId: Int): Flow<TicketEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Delete
    suspend fun deleteTicket(ticket: TicketEntity)
}

@Database(entities = [StudyTaskEntity::class, TicketEntity::class, FlashcardEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): StudyTaskDao
    abstract fun ticketDao(): TicketDao
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study-db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

