# Tasks: Exam Entry Screen UI Redesign

## Phase 2: Implementation Checklist

- [x] **1. Presentation Layer - Exam Details Screen UI Overhaul:**
    - [x] Update `ExamDetailsScreen.kt` to match the exact visual hierarchy in the screenshot:
        - [x] Header with circular back button, title "ورود به آزمون" with clipboard icon, and subtitle.
        - [x] Section 1: "شناسه آزمون" card with "ID" chip tag, entered exam ID number, scanner button, and bottom helper note.
        - [x] Section 2: "مشخصات آزمون" card featuring the top illustration/banner, title, chapter badge, and 7 detailed rows (Organizer, Date, Time, Duration, Questions, Type, Total Score) each with light purple icon badges.
        - [x] Section 3: "توجه داشته باشید" warning/info banner card.
        - [x] Sticky bottom CTA button "ورود به آزمون" with forward icon.
    - [x] Add Persian typography, colors matching `#5B42F3`, `#F3F0FF`, and smooth RTL spacing.

- [x] **2. Navigation & Interactions:**
    - [x] Ensure back button pops backstack smoothly to `exams_screen`.
    - [x] Ensure "ورود به آزمون" navigates to `exam_taking`.

- [x] **3. Testing & Verification:**
    - [x] Update / run Robolectric UI tests in `ExamDetailsScreenTest.kt` ensuring all new components and tags render correctly.
    - [x] Compile and verify applet build.
