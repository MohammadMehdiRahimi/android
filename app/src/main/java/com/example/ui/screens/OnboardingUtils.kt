package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import com.example.R
import com.example.data.AppDatabase
import com.example.data.StudyTaskEntity
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import com.example.ui.theme.ShetabColorPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun saveScheduleToDatabase(context: android.content.Context, schedule: List<DaySchedule>) {
    val db = AppDatabase.getDatabase(context)
    val dao = db.taskDao()

    schedule.forEach { daySchedule ->
        // Format day database string
        // Day 1: "۴ شهریور (امروز)" or "<day> شهریور"
        val dateStr = if (daySchedule.dayNumber == 1) {
            "۴ شهریور (امروز)"
        } else {
            val dayNumInMonth = 3 + daySchedule.dayNumber
            "${dayNumInMonth.toString().toPersianNumber()} شهریور"
        }

        daySchedule.tasks.forEach { task ->
            dao.insertTask(
                StudyTaskEntity(
                    title = task.title,
                    timeLimit = "۴۵ دقیقه",
                    isCompleted = false,
                    subject = task.subject,
                    dateStr = dateStr,
                    completedCycles = 0,
                    totalCycles = 1,
                    focusDuration = 45,
                    restDuration = 15
                )
            )
        }
    }
}
data class TaskPreview(val subject: String, val title: String)
data class DaySchedule(val dayNumber: Int, val dayName: String, val tasks: List<TaskPreview>)

fun generate14DaySchedule(
    name: String,
    grade: String,
    major: String,
    dailyHours: Int,
    energyPeak: String,
    breakTime: Int,
    restTime: Int,
    beverage: String,
    goesToSchool: Boolean,
    schoolShift: String,
    hasExams: Boolean,
    examSubjects: List<String>,
    examTopics: String,
    studyStyle: String
): List<DaySchedule> {
    val dayNames = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه")
    val subjects = when (major) {
        "ریاضی و فیزیک" -> listOf("ریاضیات", "فیزیک", "شیمی", "هندسه", "ادبیات فارسی")
        "تجربی" -> listOf("زیست‌شناسی", "شیمی", "فیزیک", "ریاضی", "ادبیات فارسی")
        "انسانی" -> listOf("ادبیات اختصاصی", "عربیه", "تاریخ و جغرافیا", "فلسفه و منطق", "زبان انگلیسی")
        else -> listOf("زیست‌شناسی", "فیزیک", "شیمی", "ریاضی", "زبان انگلیسی")
    }

    val schedule = mutableListOf<DaySchedule>()
    for (day in 1..14) {
        val dayName = dayNames[(day - 1) % 7]
        val taskList = mutableListOf<TaskPreview>()
        val sub1 = subjects[(day - 1) % subjects.size]
        val sub2 = subjects[day % subjects.size]

        taskList.add(TaskPreview(sub1, "مطالعه مبحثی و تست‌زنی آموزشی $sub1"))
        taskList.add(TaskPreview(sub2, "مرور نکات کلیدی و حل مسائل $sub2"))

        schedule.add(DaySchedule(day, dayName, taskList))
    }
    return schedule
}

