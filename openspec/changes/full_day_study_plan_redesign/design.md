# Design: معماری و طراحی UI/State برای ثبت برنامه روزانه کامل

## ۱. معماری و مدل داده (Data Structures)
برای پشتیبانی از چند کتاب و چند فصل در طول یک روز:

```kotlin
data class BookStudyPlanBlock(
    val id: String = UUID.randomUUID().toString(),
    val selectedGrade: String = "GRADE_10",
    val selectedSubjectId: String? = null,
    val chapters: List<ChapterSelectionBlock> = listOf(ChapterSelectionBlock()),
    val studyDurationMinutes: Int = 45,
    val breakDurationMinutes: Int = 15,
    val periodCount: Int = 1
)

data class ChapterSelectionBlock(
    val id: String = UUID.randomUUID().toString(),
    val selectedChapterId: String? = null,
    val selectedTopicIds: Set<String> = emptySet()
)
```

## ۲. طراحی بخش تاریخ (Interactive Persian Date Picker)
- نوار افقی حاوی آیکون تقویم بنفش، تاریخ شمسی کامل و آیکون ادیت/فلش جهت‌دار.
- کلیک بر روی کل نوار، مستقیماً پنجره انتخاب تاریخ یا تقویم را باز می‌کند.
- حذف کامل چیپ‌های متنی «امروز» و «فردا».

## ۳. طراحی بخش کتاب‌ها، فصل‌ها و مباحث (Multi-Book & Multi-Chapter Planner)
- **کانتینر هر کتاب:**
  - یک کارت زیبا با حاشیه ملایم و پس‌زمینه تمیز.
  - هدر کارت: عنوان کتاب با دکمه حذف کتاب (در صورت وجود بیش از یک کتاب).
  - انتخاب پایه و کتاب درون دراپ‌داون/سلکتور یکپارچه.
  - لیست فصل‌ها و مباحث درون همان کارت کتاب با دکمه «+ افزودن فصل دیگر».
  - تنظیم زمان اختصاصی یا پیش‌فرض برای همان درس (دقیقه مطالعه و استراحت).
- **دکمه افزودن کتاب جدید:**
  - یک دکمه با خط‌دور بنفش با متن «+ افزودن کتاب و درس دیگر به برنامه امروز» در پایین لیست کتاب‌ها.

## ۴. طراحی زمان‌بندی (Direct Open Timing)
- حذف وضعیت toggle/switch مربوط به «دستی/پیشنهادی».
- فیلدهای تعداد دوره، زمان مطالعه و استراحت همواره باز، شفاف و با دکمه‌های افزایشی/کاهشی سریع (+ و -) و اسلایدر/ورودی مستقیم در دسترس هستند.

## ۵. جریان داده و State Management (UDF)
- `CreateStudyPlanViewModel` به روزرسانی شده تا لیست `bookBlocks` را مدیریت کند.
- اکشن‌های:
  - `onAddBookBlock()`
  - `onRemoveBookBlock(bookBlockId)`
  - `onSubjectChanged(bookBlockId, subjectId)`
  - `onAddChapterToBook(bookBlockId)`
  - `onRemoveChapterFromBook(bookBlockId, chapterBlockId)`
  - `onChapterChanged(bookBlockId, chapterBlockId, chapterId)`
  - `onTopicToggled(bookBlockId, chapterBlockId, topicId)`
  - `onDurationChanged(...)`
