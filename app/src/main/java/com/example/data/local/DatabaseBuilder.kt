package com.example.data.local

import android.content.Context
import com.example.data.AppDatabase

object DatabaseBuilder {
    fun getInstance(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
}
