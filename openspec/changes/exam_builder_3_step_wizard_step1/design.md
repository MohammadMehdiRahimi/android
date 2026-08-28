# Design: Exam Builder 3-Step Wizard with Redesigned Step 1 (طراحی مرحله اول ساخت آزمون)

## 1. Architecture & Component Structure

```
com.example.ui.features.exams/
├── BuildExamScreen.kt               // Top container hosting 3-step wizard & navigation
├── components/
│   ├── ExamWizardStepper.kt         // 3-step progress bar (۱. ساختار آزمون, ۲. تنظیم سوالات, ۳. ساخت آزمون)
│   ├── Step1ExamStructureScreen.kt  // Step 1 UI matching exam-create-1.png
│   ├── Step2QuestionSettingsScreen.kt // Step 2 UI (Question counts, difficulty distribution)
│   ├── Step3ExamFinalizeScreen.kt   // Step 3 UI (Title, time, negative marking, launch exam)
│   ├── SelectedBookCard.kt          // Book card with cover, title, chapter subtitle, topic chips, remove button
│   ├── AddBookModalSheet.kt         // Interactive sheet to add new books and choose chapters/topics
│   └── ExamSelectionSummaryCard.kt  // "خلاصه انتخاب‌های شما" 4-column metric card
└── ...
```

---

## 2. UI Specifications for Step 1 (`exam-create-1.png`)

### 2.1 Top App Bar
- **RTL Orientation**: Right side has back arrow (`Icons.AutoMirrored.Filled.ArrowBack`), center has Title `"طراحی آزمون جدید"` (18.sp, bold) and Subtitle `"مرحله ۱: انتخاب ساختار آزمون"` (12.sp, secondary text), and left side has circular help button (`?`).

### 2.2 3-Step Wizard Indicator
- 3 equidistant step circles connected by dashed lines:
  - **Step 1** (Right in RTL): Filled purple circle with "۱", bold label `"۱. ساختار آزمون"`.
  - **Step 2** (Center): Neutral circle with "۲", label `"۲. تنظیم سوالات"`.
  - **Step 3** (Left): Neutral circle with "۳", label `"۳. ساخت آزمون"`.

### 2.3 Section 1: "نوع آزمون" (Exam Type)
- Header with document icon (`Icons.Outlined.Description` or checklist).
- Two choice cards:
  - **"آزمون تستی"** (with list icon): Selected state has purple border (`#7C3AED`), soft purple background (`#F3E8FF`), and bold purple text.
  - **"آزمون تشریحی"** (with edit/pencil icon): Unselected state has clean white card and subtle border.
- Helper text: `"می‌توانید نوع آزمون را در هر زمان تغییر دهید."`

### 2.4 Section 2: "پایه و رشته" (Grade & Major)
- Header with graduation cap icon (`Icons.Outlined.School`).
- Two dropdown select boxes in a row:
  - **پایه**: "دهم" / "یازدهم" / "دوازدهم"
  - **رشته**: "ریاضی فیزیک" / "علوم تجربی" / "علوم انسانی"

### 2.5 Section 3: "کتاب‌ها و محدوده آزمون" (Books & Scope)
- Header with book icon (`Icons.Outlined.MenuBook`).
- List of selected books:
  - **Card Item**: White card with border (`1.dp`), book thumbnail image/gradient, title (e.g. "ریاضی دهم"), chapter subtitle (e.g. "فصل ۲: تابع"), topic tag chips (e.g. "تابع و نمودار", "دامنه و برد", "ترکیب توابع"), and close/delete button (`x`).
  - **Add Book Button**: Dashed purple border button `"+ افزودن کتاب دیگر"`.
  - Helper note with info circle icon: `"می‌توانید از یک یا چند کتاب برای ساخت آزمون استفاده کنید."`

### 2.6 Section 4: "منبع سوالات" (Question Source)
- Header with layers/database icon (`Icons.Outlined.Layers`).
- 3 selector buttons:
  - **تألیفی** (Selected with edit pencil icon).
  - **سوالات کنکور** (with university/bank icon).
  - **سوالات نهایی** (Disabled when Multiple Choice is selected with tooltip `"منبع نهایی فقط برای آزمون تشریحی فعال است."`).

### 2.7 Section 5: "خلاصه انتخاب‌های شما" (Selection Summary Card)
- Header with checklist icon (`Icons.Outlined.FactCheck`).
- 4-column summary row with vertical dividers:
  - **نوع آزمون**: "تستی" + list icon.
  - **پایه و رشته**: "دهم" / "ریاضی فیزیک" + icons.
  - **کتاب‌ها**: "۲ کتاب" + book icon.
  - **فصل‌ها و موضوعات**: "۵ مورد" + tag/pin icon.

### 2.8 Bottom Navigation Actions
- Full-width primary button: **"ادامه به مرحله بعد"** with left chevron (`Icons.AutoMirrored.Filled.KeyboardArrowLeft`).
- Text button below: **"انصراف"** (Cancel) that navigates back.

---

## 3. State Management & Data Models

```kotlin
data class SelectedBookItem(
    val bookId: String,
    val bookName: String,
    val grade: String,
    val field: String,
    val chapter: String,
    val topics: List<String>,
    val coverRes: Int? = null
)

data class Step1State(
    val examType: String = "تستی",
    val grade: String = "دهم",
    val field: String = "ریاضی فیزیک",
    val selectedBooks: List<SelectedBookItem> = emptyList(),
    val questionSource: String = "تألیفی"
)
```

---

## 4. Test Strategy
- Test Step 1 state mutations (toggling exam type, changing grade/major, adding/removing books, updating topics).
- Compose UI test checking presence of 3-step wizard headers, book cards, summary calculations, and step navigation.
