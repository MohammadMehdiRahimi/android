package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamQuestion
import com.example.ui.theme.ShetabColorPalette
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.components.LatexText
import com.example.ui.core.components.LatexSkeletonType

@Composable
fun AnswerExplanationSection(
    question: ExamQuestion,
    colors: ShetabColorPalette
) {
    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))
    Spacer(modifier = Modifier.height(16.dp))

    // Section Title
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = colors.accentMain,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "تحلیل هوشمند و پاسخ‌نامه تشریحی رایا",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = colors.accentMain,
            fontFamily = IranSansFontFamily
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    val exp = question.explanationObj
    if (exp != null) {
        // 1. Understanding the Question
        if (exp.understandingTheQuestion.isNotBlank()) {
            ExplanationBlock(
                title = "درک و تحلیل سوال",
                content = exp.understandingTheQuestion,
                emoji = "🤔",
                color = colors.accentSecondary.copy(alpha = 0.08f),
                borderColor = colors.accentSecondary.copy(alpha = 0.2f),
                textColor = colors.primaryText
            )
        }

        // 2. Concept Review
        if (exp.conceptReview.isNotBlank()) {
            ExplanationBlock(
                title = "مرور درس‌نامه و مفهوم کلیدی",
                content = exp.conceptReview,
                emoji = "📚",
                color = colors.accentMain.copy(alpha = 0.05f),
                borderColor = colors.accentMain.copy(alpha = 0.15f),
                textColor = colors.primaryText
            )
        }

        // 3. Step-by-Step Solution
        if (exp.stepByStepSolution.isNotBlank()) {
            ExplanationBlock(
                title = "راه‌حل گام‌به‌گام و حل تشریحی",
                content = exp.stepByStepSolution,
                emoji = "✍️",
                color = Color(0xFFE8F5E9),
                borderColor = Color(0xFF2E7D32).copy(alpha = 0.3f),
                textColor = colors.primaryText
            )
        }

        // 4. Why Others Are Wrong
        if (exp.whyOthersAreWrong.isNotBlank()) {
            ExplanationBlock(
                title = "تحلیل و رد سایر گزینه‌ها",
                content = exp.whyOthersAreWrong,
                emoji = "❌",
                color = Color(0xFFFFEBEE),
                borderColor = Color(0xFFC62828).copy(alpha = 0.3f),
                textColor = colors.primaryText
            )
        }
    } else if (question.explanation.isNotBlank()) {
        // Legacy explanation fallback
        ExplanationBlock(
            title = "پاسخ تشریحی",
            content = question.explanation,
            emoji = "📝",
            color = colors.primaryText.copy(alpha = 0.03f),
            borderColor = colors.primaryText.copy(alpha = 0.1f),
            textColor = colors.primaryText
        )
    }

    // 5. Tips section
    ExamTipsSection(question = question, colors = colors)
}

@Composable
fun ExplanationBlock(
    title: String,
    content: String,
    emoji: String,
    color: Color,
    borderColor: Color,
    textColor: Color
) {
    val colors = LocalShetabColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(color, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = colors.primaryText,
                fontFamily = IranSansFontFamily
            )
        }
        LatexText(
            latexString = content,
            textColor = textColor,
            skeletonType = LatexSkeletonType.EXPLANATION,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
