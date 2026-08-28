# Technical Design: Mock Data & Offline Mode

## 1. Architectural Strategy
To satisfy the request to bypass real backend calls and provide rich mock data cleanly, we implement an **Offline Mock Interceptor** or Mock Dispatcher in the network pipeline (`OkHttpClient`), or provide fallback mock responses across API endpoints.

```
+-----------------------------------------------------------+
|                   Jetpack Compose UI                      |
| (Auth Screen -> Base Info -> Home Dashboard -> Plan...)   |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|               Repositories & ViewModels                   |
|   (AuthRepo, DashboardRepo, StudyPlanRepo, UserRepo...)   |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|              Mock Interceptor / ApiClient                 |
| - Intercepts HTTP requests or provides instant JSON mocks |
| - Returns HTTP 200 with realistic Persian dataset         |
+-----------------------------------------------------------+
```

---

## 2. Mock Data Sets Specification

### 2.1 Auth & User Profile
- **Send OTP (`/auth/send-otp`)**: Returns `{ "message": "کد تأیید ارسال شد", "expiresIn": 120 }`.
- **Verify OTP (`/auth/verify-otp`)**: Returns valid mock `accessToken`, `user` (`id: 1, phone: "09123456789", fullName: "پوریا رحیمی", role: "STUDENT", grade: "دوازدهم", field: "ریاضی و فیزیک"`).
- **Profile / Base Info (`/base-info/onboarding`)**: Returns Persian grades (دهم، یازدهم، دوازدهم، کنکور) and fields (ریاضی و فیزیک، علوم تجربی، علوم انسانی).

### 2.2 Home Performance Chart
- Returns 7-day, 30-day, and 12-month performance buckets:
  - Sat: 4.5h, Sun: 6.0h, Mon: 5.2h, Tue: 7.0h, Wed: 6.5h, Thu: 8.0h, Fri: 5.0h
- Points: 6,420, Rank: 15, League: "لیگ طلایی".

### 2.3 Smart Study Plan (برنامه درسی)
- 4 comprehensive daily study slots:
  1. حسابان ۲ - مبحث مشتق و کاربردها (۹۰ دقیقه - انجام شده)
  2. فیزیک ۳ - دینامیک و حرکت دایره‌ای (۷۵ دقیقه - در حال انجام)
  3. زیست‌شناسی / شیمی - اسیدها و بازها (۶۰ دقیقه - پیش‌رو)
  4. ادبیات فارسی - آرایه‌های ادبی و تست (۴۵ دقیقه - پیش‌رو)

---

## 3. RTL & Localization
- All mock labels, subjects, and numbers are natively in Persian and formatted with RTL alignment and Persian digits.
