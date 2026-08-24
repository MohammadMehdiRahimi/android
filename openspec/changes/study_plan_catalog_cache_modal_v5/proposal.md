# Proposal: Study Plan Catalog Cache, Chapter Search & Summary Confirmation (v5)

## 1. Problem Statement & Motivation
In `CreateStudyPlanScreen` and the Study Plan architecture, users and testers identified key requirements to elevate performance, usability, and data review workflow:
1. **True Skeleton Loading for Books:** On entering or changing grade, show skeleton shimmer loaders for the book selector while data is loading, instead of immediately showing fallback/static mock data.
2. **Simplified Chapter Numbering:** When listing and selecting chapters, replace the verbose "فصل ۱" prefix with concise numerical notation (e.g. "۱: نام فصل" or "۱").
3. **Right-Aligned Chapter Text & In-Menu Search:** Ensure all chapter item titles strictly align to the right edge (Persian RTL) and provide a search filter input within the chapter selection dropdown to quickly find chapters.
4. **Pre-Submission Summary Modal:** When clicking "Save Study Plan", do not immediately dispatch the network request. Instead, display a modal sheet summarizing the planned session (Grade, Subject, Chapters/Topics, Study Periods, Manual Timings). The user can review and then tap "تایید نهایی و ثبت" to send to the backend.
5. **Smart Catalog Caching per Grade:** Cache catalog data (books, chapters, topics) on the client for each grade once fetched, so subsequent visits or switches use the cached data instantly without redundant network calls.

6. **Back Button Navigation to Tasks Screen:** Ensure the back button in `CreateStudyPlanScreen` navigates specifically to the Tasks/Study Plan screen (`study_plan` / pop back to study plan tab).

## 2. Scope & Acceptance Criteria
*   **Granular Skeleton vs Static Data:** `isLoadingCatalog` reflects the real loading/cache lookup state. When loading, render skeleton chips in the books row and chapter box without static fallback flash.
*   **Clean Chapter Numbering:** Chapters display as "۱", "۲", ... or "۱: [عنوان فصل]" instead of "فصل ۱".
*   **Chapter Right-Alignment & Search:** Dropdown items are strictly right-aligned with full RTL fidelity, and include a search field at the top of the chapter picker with debounce/instant filtering.
*   **Study Plan Summary Modal:** A dedicated bottom sheet or dialog modal that visually summarizes the entire configured plan matching the app theme (purple/navy gradients, chips, period badges) before final backend submission.
*   **Per-Grade Catalog Caching:** Catalog data is cached locally (in-memory singleton cache and/or persistent storage) keyed by grade/major. Subsequent opens or grade selections fetch from cache instantly.
*   **Back Button Action:** Pressing the top back button directly navigates back to the Tasks/Study Plan screen (`onBackClick` returns to `study_plan`).
