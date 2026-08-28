# Proposal: Exam Builder 3-Step Wizard with Redesigned Step 1 (طراحی آزمون جدید ۳ مرحله‌ای)

## 1. Executive Summary & Intent
Transition the **Exam Builder (طراحی آزمون جدید)** from a 2-step process to a cohesive **3-step wizard workflow** and completely redesign **Step 1: انتخاب ساختار آزمون** based on the provided UI design (`exam-create-1.png`). Additionally, ensure the FAB button on the main exam screen is positioned at the bottom start (bottom-left in RTL), the card arrows are left-pointing, and navigation to the builder is fully connected.

## 2. Problem Statement & User Needs
- The current exam maker uses a 2-step setup. Users need a clear, modern 3-step wizard:
  1. **ساختار آزمون (Exam Structure)**: Exam type (تستی/تشریحی), grade & field (پایه و رشته), books and chapters/topics scope (کتاب‌ها و محدوده آزمون), question source (منبع سوالات), and summary card.
  2. **تنظیم سوالات (Question Configuration)**: Difficulty breakdown, question count, and sub-topic distribution.
  3. **ساخت آزمون (Exam Finalization)**: Exam title, duration, negative marking toggles, and start exam button.
- The Step 1 layout must precisely reproduce `exam-create-1.png` with:
  - Top app bar with back navigation, Persian title & subtitle ("طراحی آزمون جدید / مرحله ۱: انتخاب ساختار آزمون"), and help `?` button.
  - Horizontal 3-step progress stepper with active/inactive indicators.
  - Exam type selector pills ("آزمون تستی" / "آزمون تشریحی").
  - Grade & Field selectors ("پایه" e.g., دهم / "رشته" e.g., ریاضی فیزیک).
  - Multi-book selection list with cover thumbnails, chapter subtitles, topic tags, delete button, and a "+ افزودن کتاب دیگر" dashed action.
  - Question source toggles ("تألیفی", "سوالات کنکور", "سوالات نهایی").
  - Dynamic "خلاصه انتخاب‌های شما" (Summary) 4-column card.
  - Bottom action bar with "ادامه به مرحله بعد" and "انصراف".

## 3. Acceptance Criteria
- [ ] FAB button in `ExamsScreen` is floating at the bottom left with valid navigation to `build_exam`.
- [ ] Exam cards in `ExamsScreen` feature left navigation arrow indicators.
- [ ] Stepper supports 3 steps: `۱. ساختار آزمون`, `۲. تنظیم سوالات`, `۳. ساخت آزمون`.
- [ ] Step 1 UI faithfully adheres to `exam-create-1.png` in structure, Persian typography, badges, topic tags, and colors.
- [ ] Book addition, chapter/topic selection, book deletion, and live summary calculation work reactively.
- [ ] Clean build and passes all unit and Compose tests under Robolectric.
