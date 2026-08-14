package com.example.ui.features.personalgrowth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalShetabColors

data class GrowthContent(
    val id: String,
    val title: String,
    val category: String, // "نظم", "مدیریت زمان", "تست زنی", "انگیزشی"
    val type: String, // "Text", "Video", "Audio"
    val durationOrReadTime: String,
    val timeAvailableIn: String // e.g. "هم‌اکنون", "۵ ساعت دیگر"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalGrowthScreen() {
    val colors = LocalShetabColors.current
    var selectedCategory by remember { mutableStateOf("همه") }
    
    val categories = listOf("همه", "انگیزشی", "مدیریت زمان", "نظم", "تست زنی")
    
    val contents = listOf(
        GrowthContent("1", "چطور اراده پولادین بسازیم؟", "انگیزشی", "Video", "۵ دقیقه", "هم‌اکنون"),
        GrowthContent("2", "تکنیک پومودورو در سال دهم", "مدیریت زمان", "Audio", "۱۲ دقیقه", "هم‌اکنون"),
        GrowthContent("3", "هنر نه گفتن به حواس‌پرتی", "نظم", "Text", "۳ دقیقه مطالعه", "هم‌اکنون"),
        GrowthContent("4", "اسرار تست‌زنی سرعتی", "تست زنی", "Video", "۸ دقیقه", "۵ ساعت دیگر"),
        GrowthContent("5", "اهمال‌کاری را همین الان تمام کن", "انگیزشی", "Audio", "۱۵ دقیقه", "۱۰ ساعت دیگر")
    )

    val filteredContents = if (selectedCategory == "همه") contents else contents.filter { it.category == selectedCategory }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "رشد فردی 🪴",
                    color = colors.primaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Motivational Quote
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "«موفقیت مجموعه‌ای از تلاش‌های کوچک است که هر روز و هر روز تکرار می‌شوند.»",
                            color = colors.primaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "- رابرت کالیر",
                            color = colors.secondaryText,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Featured Video Placeholder
                Text(
                    text = "ویدیوی ویژه امروز",
                    color = colors.primaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2C2C2C)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Text(
                        text = "چگونه با استرس کنکور مقابله کنیم؟",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { 
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else colors.primaryText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accentMain,
                            containerColor = colors.cardBg
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(filteredContents) { content ->
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                ContentCard(content = content, colors = colors)
            }
        }
    }
}

@Composable
fun ContentCard(content: GrowthContent, colors: com.example.ui.theme.ShetabColorPalette) {
    val isLocked = content.timeAvailableIn != "هم‌اکنون"
    
    val icon: ImageVector = when(content.type) {
        "Video" -> Icons.Default.PlayArrow
        "Audio" -> Icons.Default.VolumeUp
        else -> Icons.Default.Article
    }

    val typeColor = when(content.type) {
        "Video" -> Color(0xFFE91E63)
        "Audio" -> Color(0xFF2196F3)
        else -> Color(0xFFFF9800)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x0E000000), RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isLocked) colors.bgMain else typeColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (isLocked) {
                Icon(Icons.Default.Timer, contentDescription = "Locked", tint = colors.secondaryText)
            } else {
                Icon(icon, contentDescription = content.type, tint = typeColor)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = content.title,
                color = if (isLocked) colors.secondaryText else colors.primaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = content.category,
                    color = colors.accentMain,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " • ",
                    color = colors.secondaryText,
                    fontSize = 10.sp
                )
                Text(
                    text = content.durationOrReadTime,
                    color = colors.secondaryText,
                    fontSize = 10.sp
                )
            }
        }
        
        if (isLocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "آزادسازی در",
                    color = colors.secondaryText,
                    fontSize = 9.sp
                )
                Text(
                    text = content.timeAvailableIn,
                    color = colors.accentMain,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
