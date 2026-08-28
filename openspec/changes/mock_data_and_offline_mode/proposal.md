# Proposal: Enable Mock/Offline Mode for Onboarding, Dashboard Charts, and Study Plan

## 1. Problem Statement & User Intent
The user requested to temporarily disconnect the real backend API communication, allowing seamless progression through onboarding, authentication, and application steps, with realistic mock data populated for:
- Authentication & Onboarding steps (allow OTP verification, registration, base info selection without network dependency).
- Home Screen Performance Charts & metrics.
- Smart Study Plan (برنامه درسی), daily tasks, and study sessions.
- Related core features (e.g. Leaderboard, Groups, Flashcards, Profile).

This provides an immediate, fully testable, and functional offline interactive prototype without blocking on remote server availability.

---

## 2. Scope of Changes
- **Mock / Offline Mode Architecture**:
  - Provide a toggleable or default mock response layer in `ApiService` / `ApiClient` or Repository layer / Interceptor so all API calls succeed with realistic Persian demo data.
  - **Auth Flows**: Allow any OTP code (e.g. `11111` or any 5-digit number) to succeed and log in directly as an active user or proceed through registration smoothly.
  - **Base Info**: Provide default Grade (پایه تحصیلی) and Field of Study (رشته تحصیلی) options if offline.
  - **Home Dashboard & Performance Chart**: Provide rich sample performance buckets (daily/weekly/monthly study hours) for the chart.
  - **Study Plan (برنامه درسی)**: Provide sample daily study schedule, study tasks, and Pomodoro focus sessions.
- **Data Persistence**:
  - Keep Session and local Room persistence active so user edits (tasks, profile, timer) remain interactive.

---

## 3. Acceptance Criteria
1. The app runs smoothly without requiring active internet or remote server response.
2. Login and OTP verification succeed directly into the main app.
3. Performance chart on the home screen renders rich, realistic study progression data.
4. Study Plan (برنامه درسی) displays active daily schedule and interactive tasks.
5. All tests and builds compile successfully.
