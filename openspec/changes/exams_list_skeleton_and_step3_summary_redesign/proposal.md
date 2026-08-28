# Proposal: Exams List Granular Skeleton Loading, RTL Arrow Fix & Step 3 Summary Screen Redesign

## 1. Overview
This proposal covers two primary areas:
1. **Exams List Screen (`ExamsScreen.kt` & `ExamItemCard.kt`) Refinements**:
   - Granular skeleton loading: Preserve static structural UI elements (page title, filter chips, summary counter labels) immediately upon entry, and apply skeleton shimmer only to dynamic metric values and exam cards list.
   - Directional Arrow Fix: Replace right arrow with left arrow (`Icons.AutoMirrored.Filled.KeyboardArrowLeft` / pointing left) in exam cards for proper Persian RTL forward navigation.
2. **Step 3 Exam Creation Summary Redesign (`BuildExamScreen.kt` / Step 3)**:
   - Completely redesign Step 3 ("خلاصه و ساخت آزمون" / Summary and Finalization) to match the provided high-fidelity visual specification (`exam-create-3.png`) pixel-perfectly.

---

## 2. User Requirements & Motivation
1. **Granular Skeleton Loading in "My Exams" (`آزمون‌های من`)**:
   - Currently, entire sections may be masked during loading. The user wants static layout content ("آزمون‌های من", filter categories, "تعداد کل" label) to render instantly, showing skeleton shimmers only on changing values (total count number) and rendering a list of skeleton exam cards during data fetch.
2. **Left Arrow Navigation in Exam Cards**:
   - In Persian (RTL), forward navigation into details/start screen is represented by a left-pointing arrow (`<`), not a right-pointing arrow (`>`).
3. **Step 3 Summary Redesign based on `exam-create-3.png`**:
   - Top Header with Help (?) and Back Arrow.
   - 3-step active progress indicator with completed checkmarks and active step 3.
   - **General Summary Card (خلاصه کلی آزمون)**: 2x4 grid with icons and formatted Persian labels (نوع آزمون, پایه, رشته, تعداد کتاب‌ها, منبع سوال, نمره منفی, چینش سوالات, مدت زمان تقریبی).
   - **Question Statistics (آمار سوالات آزمون)**: Total question count badge + difficulty breakdown (آسان, متوسط, دشوار, خیلی دشوار) with color-coded dots and a multi-segmented colored progress bar.
   - **Exam Sections (بخش‌های آزمون)**: Detailed book cards showing book cover artwork, chapter, topic pill tags, total questions badge, and difficulty distribution row with colored dots.
   - **Exam Tips (نکات آزمون)**: Guidelines list with icons, dashed guides, and bullet points.
   - **Bottom Action**: Prominent "ساخت آزمون" button with sparkle icon + "مرحله قبل" text button.

---

## 3. Acceptance Criteria
1. **Exams Screen Loading**:
   - Header, search bar, and filter chips are always visible immediately.
   - Dynamic counter shows a shimmer placeholder when loading.
   - While loading, 3 skeleton exam cards with shimmering placeholders are displayed.
2. **Card Arrow**:
   - Exam item card shows a left-facing chevron/arrow icon for forward navigation in RTL.
3. **Step 3 Fidelity**:
   - Step 3 reproduces the layout, typography, colors, icon pairings, and segmented difficulty bar from `exam-create-3.png`.
   - Clicking "ساخت آزمون" completes exam creation and navigates to the exam taking / success screen.
   - Clicking "مرحله قبل" returns to Step 2.
