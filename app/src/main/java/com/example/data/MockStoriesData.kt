package com.example.data

import androidx.compose.runtime.mutableStateListOf

data class ShetabStory(
    val id: String,
    val title: String, // Circle label under story, e.g., "خبر فوری", "آزمون آنلاین", "مشاوره رایا"
    val fullTitle: String, // Header inside the story view
    val content: String, // Body message
    val emoji: String, // Story circle emoji/sticker representation
    val gradientStart: String, // hex colors for story card background
    val gradientEnd: String,
    val date: String, // Date posted
    var isRead: Boolean = false
)

object MockStoriesData {
    val stories = mutableStateListOf(
        ShetabStory(
            id = "story_1",
            title = "خبر فوری",
            fullTitle = "🚨 خبر فوری: اعلام حذفیات کنکور دهم!",
            content = "بر اساس آخرین اطلاعیه سازمان سنجش، بخش‌های مشخص شده از کتاب جامعه‌شناسی و ریاضی پایه دهم از بودجه‌بندی کنکور سال آینده حذف شد. برای مشاهده لیست دقیق حذفیات به بخش برنامه‌ریزی مراجعه کنید.",
            emoji = "🚨",
            gradientStart = "#FF416C",
            gradientEnd = "#FF4B2B",
            date = "امروز"
        ),
        ShetabStory(
            id = "story_2",
            title = "رایا چت",
            fullTitle = "🤖 دستیار جدید رایا ۲.۰ فعال شد!",
            content = "از امروز می‌توانید مستقیماً با ارسال تصاویر از فرمول‌ها یا تمرین‌های دشوار خود، حل گام به گام و نکات آموزشی مرتبط را از مشاور هوشمند رایا دریافت کنید. حتماً رایا جدید رو امتحان کن!",
            emoji = "🤖",
            gradientStart = "#2196F3",
            gradientEnd = "#00BCD4",
            date = "دیروز"
        ),
        ShetabStory(
            id = "story_3",
            title = "انگیزشی",
            fullTitle = "✨ یک فنجان انگیزه برای قهرمانان دهم",
            content = "«موفقیت مجموعه‌ای از تلاش‌های کوچک است که روزبه‌روز تکرار می‌شوند.» امروز حتی اگر ۱۰ تست بیشتر بزنی یا ۱۵ دقیقه بیشتر بخوانی، به هدف بزرگت نزدیک‌تر شدی. تسلیم نشو!",
            emoji = "✨",
            gradientStart = "#FF9800",
            gradientEnd = "#F44336",
            date = "۲ روز پیش"
        ),
        ShetabStory(
            id = "story_4",
            title = "آزمون جدید",
            fullTitle = "📝 امتحان تشریحی انگلیسی فعال شد",
            content = "امتحان نهایی تشریحی زبان انگلیسی دهم هم‌اکنون در بخش آزمون‌ها در دسترس است. با قابلیت بارم‌بندی مصوب و پاسخ متنی/تصویری به همراه تصحیح هوشمند هوش‌مصنوعی شتاب.",
            emoji = "📝",
            gradientStart = "#9C27B0",
            gradientEnd = "#E91E63",
            date = "۳ روز پیش"
        )
    )
}
