package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamQuestion
import com.example.ui.theme.ShetabColorPalette
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.core.components.LatexText
import com.example.ui.core.components.LatexSkeletonType

@Composable
fun ExamTipsSection(
    question: ExamQuestion,
    colors: ShetabColorPalette
) {
    if (question.tips.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "💡 نکات طلایی و تله‌های تستی:",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = colors.accentMain,
            fontFamily = IranSansFontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        question.tips.forEach { tip ->
            val tipBg = when (tip.type) {
                "تله تستی" -> Color(0xFFFFF3E0)
                "روش سریع‌تر" -> Color(0xFFE0F7FA)
                "فرمول طلایی" -> Color(0xFFF3E5F5)
                else -> colors.cardBg
            }
            val tipBorder = when (tip.type) {
                "تله تستی" -> Color(0xFFFF9800)
                "روش سریع‌تر" -> Color(0xFF00BCD4)
                "فرمول طلایی" -> Color(0xFF9C27B0)
                else -> colors.primaryText.copy(alpha = 0.08f)
            }
            val tipEmoji = when (tip.type) {
                "تله تستی" -> "⚠️"
                "روش سریع‌تر" -> "⚡"
                "فرمول طلایی" -> "⭐"
                else -> "💡"
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(tipBg, RoundedCornerShape(12.dp))
                    .border(1.dp, tipBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = tipEmoji, fontSize = 16.sp)
                Column {
                    Text(
                        text = tip.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = tipBorder,
                        fontFamily = IranSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LatexText(
                        latexString = tip.content,
                        textColor = colors.primaryText,
                        skeletonType = LatexSkeletonType.EXPLANATION,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
