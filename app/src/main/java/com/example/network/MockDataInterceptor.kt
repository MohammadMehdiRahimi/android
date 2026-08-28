package com.example.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * MockDataInterceptor provides offline / mock data for all API requests.
 * Completely eliminates dependence on real backend connectivity while enabling
 * all onboarding, login, study plans, charts, and group features.
 */
class MockDataInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val jsonResponse = when {
            // 1. Auth: Request OTP
            path.contains("auth/send-otp") -> {
                """
                {
                    "statusCode": 200,
                    "status": "success",
                    "body": {
                        "message": "کد تأیید با موفقیت ارسال شد",
                        "expiresIn": 120
                    }
                }
                """.trimIndent()
            }

            // 2. Auth: Verify OTP / Register
            path.contains("auth/verify-otp") || path.contains("auth/register") -> {
                """
                {
                    "statusCode": 200,
                    "body": {
                        "isNew": false,
                        "accessToken": "mock_shetab_access_token_active_offline",
                        "accessExpiresAt": 4102444800000,
                        "refreshExpiresAt": 4102444800000,
                        "sessionId": "mock_sess_1001",
                        "user": {
                            "id": "usr_shetab_mock_1",
                            "fullName": "پوریا رحیمی",
                            "phone": "09123456789",
                            "role": "STUDENT",
                            "bio": "دانش‌آموز کوشا در شتاب",
                            "profileImageUrl": null,
                            "sessionExpiresAt": 4102444800000,
                            "progression": {
                                "userId": "usr_shetab_mock_1",
                                "points": 6420,
                                "streak": 7,
                                "title": {
                                    "id": "t1",
                                    "nameFa": "پژوهشگر برتر",
                                    "minimumPoints": 5000,
                                    "colorHex": "#7656F5",
                                    "iconKey": "crown"
                                },
                                "league": {
                                    "id": "l4",
                                    "nameFa": "لیگ طلایی (لول ۴)",
                                    "minimumPoints": 5000,
                                    "colorHex": "#21B982",
                                    "iconKey": "star"
                                }
                            }
                        },
                        "onboarding": {
                            "required": false,
                            "missingFields": []
                        }
                    }
                }
                """.trimIndent()
            }

            // 3. Auth: Logout
            path.contains("auth/logout") -> {
                """{"statusCode": 200, "body": {}}"""
            }

            // 4. Base info onboarding
            path.contains("base-info/onboarding") -> {
                """
                {
                    "statusCode": 200,
                    "body": {
                        "grades": [
                            { "code": "G10", "label": "دهم", "key": "G10", "value": "دهم", "sortOrder": 1, "requiresFieldOfStudy": true },
                            { "code": "G11", "label": "یازدهم", "key": "G11", "value": "یازدهم", "sortOrder": 2, "requiresFieldOfStudy": true },
                            { "code": "G12", "label": "دوازدهم", "key": "G12", "value": "دوازدهم", "sortOrder": 3, "requiresFieldOfStudy": true },
                            { "code": "KONKUR", "label": "کنکور / فارغ‌التحصیل", "key": "KONKUR", "value": "کنکور", "sortOrder": 4, "requiresFieldOfStudy": true }
                        ],
                        "fieldsOfStudy": [
                            { "code": "MATH", "label": "ریاضی و فیزیک", "key": "MATH", "value": "ریاضی و فیزیک", "sortOrder": 1 },
                            { "code": "EXP", "label": "علوم تجربی", "key": "EXP", "value": "علوم تجربی", "sortOrder": 2 },
                            { "code": "HUMAN", "label": "علوم انسانی", "key": "HUMAN", "value": "علوم انسانی", "sortOrder": 3 }
                        ]
                    }
                }
                """.trimIndent()
            }

            // 5. Complete Onboarding Profile
            path.contains("student-profile/me/onboarding") -> {
                """{"statusCode": 200, "body": {"message": "اطلاعات با موفقیت ذخیره شد"}}"""
            }

            // 6. User Profile (users/me)
            path.contains("users/me") -> {
                """
                {
                    "statusCode": 200,
                    "body": {
                        "id": "usr_shetab_mock_1",
                        "fullName": "پوریا رحیمی",
                        "phone": "09123456789",
                        "role": "STUDENT",
                        "bio": "دانش‌آموز کوشا در شتاب",
                        "profileImageUrl": null,
                        "sessionExpiresAt": 4102444800000,
                        "progression": {
                            "userId": "usr_shetab_mock_1",
                            "points": 6420,
                            "streak": 7,
                            "title": {
                                "id": "t1",
                                "nameFa": "پژوهشگر برتر",
                                "minimumPoints": 5000,
                                "colorHex": "#7656F5",
                                "iconKey": "crown"
                            },
                            "league": {
                                "id": "l4",
                                "nameFa": "لول ۴",
                                "minimumPoints": 5000,
                                "colorHex": "#21B982",
                                "iconKey": "star"
                            }
                        }
                    }
                }
                """.trimIndent()
            }

            // 7. Check-in
            path.contains("progression/me/check-in") -> {
                """
                {
                    "statusCode": 200,
                    "body": {
                        "userId": "usr_shetab_mock_1",
                        "points": 6450,
                        "streak": 8,
                        "league": {
                            "id": "l4",
                            "nameFa": "لول ۴",
                            "minimumPoints": 5000,
                            "colorHex": "#21B982",
                            "iconKey": "star"
                        }
                    }
                }
                """.trimIndent()
            }

            // 8. Progress Dashboard
            path.contains("progression/me/dashboard") -> {
                """
                {
                    "body": {
                        "points": 6420,
                        "rank": 15,
                        "leagueMemberCount": 50,
                        "totalStudySeconds": 532800,
                        "league": {
                            "id": "l4",
                            "nameFa": "لول ۴",
                            "minimumPoints": 5000,
                            "colorHex": "#21B982",
                            "iconKey": "star"
                        }
                    }
                }
                """.trimIndent()
            }

            // 9. Performance Chart Data
            path.contains("progression/me/performance") -> {
                val range = request.url.queryParameter("range") ?: "7d"
                when (range) {
                    "30d" -> generate30DayPerformanceJson()
                    "12m" -> generate12MonthPerformanceJson()
                    else -> generate7DayPerformanceJson()
                }
            }

            // 10. League Leaderboard
            path.contains("progression/leagues/me/leaderboard") -> {
                """
                {
                    "body": {
                        "league": {
                            "id": "l4",
                            "nameFa": "لیگ طلایی (لول ۴)",
                            "minimumPoints": 5000,
                            "colorHex": "#21B982",
                            "iconKey": "star"
                        },
                        "data": [
                            { "userId": "usr_1", "fullName": "سارا محمدی", "points": 8940, "rank": 1, "totalStudySeconds": 612000, "totalTestCount": 420, "isMe": false },
                            { "userId": "usr_2", "fullName": "امیررضا کریمی", "points": 8210, "rank": 2, "totalStudySeconds": 589000, "totalTestCount": 380, "isMe": false },
                            { "userId": "usr_3", "fullName": "مریم رضایی", "points": 7650, "rank": 3, "totalStudySeconds": 540000, "totalTestCount": 350, "isMe": false },
                            { "userId": "usr_shetab_mock_1", "fullName": "پوریا رحیمی", "points": 6420, "rank": 15, "totalStudySeconds": 532800, "totalTestCount": 290, "isMe": true },
                            { "userId": "usr_4", "fullName": "علی احمدی", "points": 6100, "rank": 16, "totalStudySeconds": 490000, "totalTestCount": 260, "isMe": false },
                            { "userId": "usr_5", "fullName": "زهرا حسینی", "points": 5800, "rank": 17, "totalStudySeconds": 460000, "totalTestCount": 240, "isMe": false }
                        ],
                        "me": {
                            "userId": "usr_shetab_mock_1", "fullName": "پوریا رحیمی", "points": 6420, "rank": 15, "totalStudySeconds": 532800, "totalTestCount": 290, "isMe": true
                        },
                        "total": 50,
                        "page": 1,
                        "limit": 50
                    }
                }
                """.trimIndent()
            }

            // 11. Study Notifications
            path.contains("progression/notifications/unread-count") -> {
                """{"body": {"count": 2}}"""
            }
            path.contains("progression/notifications") -> {
                """
                {
                    "body": {
                        "total": 3,
                        "unreadCount": 2,
                        "data": [
                            {
                                "id": "notif_1",
                                "type": "STREAK",
                                "titleFa": "رکورد استریک ۷ روزه!",
                                "bodyFa": "آفرین! ۷ روز پیاپی به برنامه مطالعه خود پایبند بودی و ۵۰ امتیاز ویژه گرفتی.",
                                "createdAt": "2026-08-27T10:00:00Z"
                            },
                            {
                                "id": "notif_2",
                                "type": "CHALLENGE",
                                "titleFa": "چالش مطالعه هفتگی گروهی",
                                "bodyFa": "گروه شما به هدف ۴۰ ساعت مطالعه تیمی در هفته نزدیک شده است.",
                                "createdAt": "2026-08-26T18:30:00Z"
                            }
                        ]
                    }
                }
                """.trimIndent()
            }

            // 12. Feedback options
            path.contains("progression/feedback/options") -> {
                """
                {
                    "body": [
                        { "code": "MOTIVATION", "labelFa": "خسته نباشی قهرمان! 💪" },
                        { "code": "AWESOME", "labelFa": "عالی پیش رفتی! 🔥" },
                        { "code": "FOCUS", "labelFa": "ادامه بده، موفقیت نزدیکه 🎯" }
                    ]
                }
                """.trimIndent()
            }

            // 13. Study Tasks Catalog
            path.contains("study-tasks/me/catalog") -> {
                generateStudyCatalogJson()
            }

            // 14. Daily Study Tasks
            path.contains("study-tasks/me") && method == "GET" -> {
                val dateParam = request.url.queryParameter("date") ?: LocalDate.now().toString()
                generateDailyStudyTasksJson(dateParam)
            }

            // 15. Create / Update Manual Study Task
            path.contains("study-tasks/me/manual") -> {
                """
                {
                    "body": {
                        "id": "tsk_manual_${System.currentTimeMillis()}",
                        "sourceType": "MANUAL",
                        "title": "مطالعه و تست مبحث انتخابی",
                        "scheduledOn": "${LocalDate.now()}",
                        "book": { "id": "book_1", "name": "حسابان ۲" },
                        "chapter": { "id": "ch_1_1", "name": "فصل ۱: تابع و حد" },
                        "topic": { "id": "top_1_1_1", "name": "مفهوم حد و پیوستگی" },
                        "periodCount": 1,
                        "minutesPerPeriod": 60,
                        "plannedMinutes": 60,
                        "status": "NOT_STARTED",
                        "execution": null
                    }
                }
                """.trimIndent()
            }

            // 16. Study Execution Event (Start/Pause/Finish study task)
            path.contains("study-execution/me") -> {
                """
                {
                    "body": {
                        "id": "exec_event_${System.currentTimeMillis()}",
                        "status": "COMPLETED",
                        "eventSequence": 2,
                        "plannedMinutes": 60,
                        "actualSeconds": 3600,
                        "persistedActiveSeconds": 3600,
                        "timerElapsedSeconds": 3600,
                        "pausedSeconds": 0,
                        "completionPercent": 100
                    }
                }
                """.trimIndent()
            }

            // 17. My Group (study-groups/me)
            path.contains("study-groups/me") -> {
                """
                {
                    "body": {
                        "group": {
                            "id": "grp_mock_1",
                            "name": "تیم کنکوری‌های برتر شتاب",
                            "description": "گروه مطالعه و رقابت درسی روزانه دانش‌آموزان رشته ریاضی و تجربی",
                            "profileImageUrl": null,
                            "ownerId": "usr_shetab_mock_1",
                            "inviteCode": "SHETAB2026",
                            "isPublic": true,
                            "capacity": 20,
                            "totalGroupPoints": 18450
                        },
                        "member": {
                            "role": "OWNER"
                        },
                        "weeklyStats": {
                            "points": 3450,
                            "studyMinutes": 2480,
                            "testCount": 320
                        },
                        "members": [
                            { "userId": "usr_shetab_mock_1", "fullName": "پوریا رحیمی", "role": "OWNER", "points": 6420, "studyMinutes": 890, "testCount": 110 },
                            { "userId": "usr_mock_2", "fullName": "سارا محمدی", "role": "ADMIN", "points": 5890, "studyMinutes": 760, "testCount": 95 },
                            { "userId": "usr_mock_3", "fullName": "علی احمدی", "role": "MEMBER", "points": 4320, "studyMinutes": 540, "testCount": 70 },
                            { "userId": "usr_mock_4", "fullName": "زهرا حسینی", "role": "MEMBER", "points": 3820, "studyMinutes": 480, "testCount": 60 }
                        ]
                    }
                }
                """.trimIndent()
            }

            // 18. Study Groups Search
            path.contains("study-groups/search") -> {
                """
                {
                    "body": {
                        "total": 2,
                        "data": [
                            {
                                "id": "grp_mock_1",
                                "name": "تیم کنکوری‌های برتر شتاب",
                                "description": "گروه مطالعه و رقابت درسی روزانه دانش‌آموزان رشته ریاضی و تجربی",
                                "ownerId": "usr_shetab_mock_1",
                                "inviteCode": "SHETAB2026",
                                "isPublic": true,
                                "capacity": 20,
                                "totalGroupPoints": 18450
                            },
                            {
                                "id": "grp_mock_2",
                                "name": "ماراتن تست‌زنی دوازدهم",
                                "description": "بررسی روزانه تست‌های کنکور سراسری با تایمر و تحلیل",
                                "ownerId": "usr_2",
                                "inviteCode": "TEST2026",
                                "isPublic": true,
                                "capacity": 15,
                                "totalGroupPoints": 14200
                            }
                        ]
                    }
                }
                """.trimIndent()
            }

            // Fallback default for any other requests
            else -> {
                """{"statusCode": 200, "body": {}}"""
            }
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonResponse.toResponseBody(mediaType)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    private fun generate7DayPerformanceJson(): String {
        return """
        {
            "body": {
                "range": "7d",
                "metric": "study_duration",
                "timezone": "Asia/Tehran",
                "buckets": [
                    { "key": "d1", "label": "شنبه", "startAt": "2026-08-22T00:00:00Z", "value": 16200, "isFuture": false },
                    { "key": "d2", "label": "یکشنبه", "startAt": "2026-08-23T00:00:00Z", "value": 21600, "isFuture": false },
                    { "key": "d3", "label": "دوشنبه", "startAt": "2026-08-24T00:00:00Z", "value": 18720, "isFuture": false },
                    { "key": "d4", "label": "سه‌شنبه", "startAt": "2026-08-25T00:00:00Z", "value": 25200, "isFuture": false },
                    { "key": "d5", "label": "چهارشنبه", "startAt": "2026-08-26T00:00:00Z", "value": 23400, "isFuture": false },
                    { "key": "d6", "label": "پنج‌شنبه", "startAt": "2026-08-27T00:00:00Z", "value": 28800, "isFuture": false },
                    { "key": "d7", "label": "جمعه", "startAt": "2026-08-28T00:00:00Z", "value": 18000, "isFuture": false }
                ]
            }
        }
        """.trimIndent()
    }

    private fun generate30DayPerformanceJson(): String {
        val buckets = (1..30).joinToString(",") { day ->
            val seconds = (12000..32000).random()
            """{"key": "d$day", "label": "$day", "startAt": "2026-08-${day.toString().padStart(2, '0')}T00:00:00Z", "value": $seconds, "isFuture": false}"""
        }
        return """
        {
            "body": {
                "range": "30d",
                "metric": "study_duration",
                "timezone": "Asia/Tehran",
                "buckets": [$buckets]
            }
        }
        """.trimIndent()
    }

    private fun generate12MonthPerformanceJson(): String {
        val monthNames = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
        val buckets = monthNames.mapIndexed { idx, name ->
            val hours = (80 + idx * 8) * 3600
            val isFuture = idx > 5
            val valSec = if (isFuture) 0 else hours
            """{"key": "m${idx+1}", "label": "$name", "startAt": "2026-${(idx+1).toString().padStart(2, '0')}-01T00:00:00Z", "value": $valSec, "isFuture": $isFuture}"""
        }.joinToString(",")

        return """
        {
            "body": {
                "range": "12m",
                "metric": "study_duration",
                "timezone": "Asia/Tehran",
                "buckets": [$buckets]
            }
        }
        """.trimIndent()
    }

    private fun generateStudyCatalogJson(): String {
        return """
        {
            "body": {
                "academicProfile": {
                    "grade": "دوازدهم",
                    "fieldOfStudy": "ریاضی و فیزیک"
                },
                "books": [
                    {
                        "id": "book_1",
                        "name": "حسابان ۲",
                        "grade": "دوازدهم",
                        "fieldOfStudy": ["ریاضی و فیزیک"],
                        "chapters": [
                            {
                                "id": "ch_1_1",
                                "name": "فصل ۱: تابع و حد",
                                "order": 1,
                                "topics": [
                                    { "id": "top_1_1_1", "name": "مفهوم حد و پیوستگی", "order": 1 },
                                    { "id": "top_1_1_2", "name": "حدهای نامتناهی و در بی‌نهایت", "order": 2 }
                                ]
                            },
                            {
                                "id": "ch_1_2",
                                "name": "فصل ۲: مشتق و کاربردها",
                                "order": 2,
                                "topics": [
                                    { "id": "top_1_2_1", "name": "تعریف مشتق و فرمول‌های پایه", "order": 1 },
                                    { "id": "top_1_2_2", "name": "آهنگ تغییر و مشتق‌پذیری", "order": 2 }
                                ]
                            }
                        ]
                    },
                    {
                        "id": "book_2",
                        "name": "فیزیک ۳",
                        "grade": "دوازدهم",
                        "fieldOfStudy": ["ریاضی و فیزیک", "علوم تجربی"],
                        "chapters": [
                            {
                                "id": "ch_2_1",
                                "name": "فصل ۱: حرکت بر خط راست",
                                "order": 1,
                                "topics": [
                                    { "id": "top_2_1_1", "name": "حرکت با سرعت ثابت و شتاب‌دار", "order": 1 }
                                ]
                            },
                            {
                                "id": "ch_2_2",
                                "name": "فصل ۲: دینامیک و قوانین نیوتون",
                                "order": 2,
                                "topics": [
                                    { "id": "top_2_2_1", "name": "اصطکاک، تکانه و نیروی گرانش", "order": 1 }
                                ]
                            }
                        ]
                    },
                    {
                        "id": "book_3",
                        "name": "شیمی ۳",
                        "grade": "دوازدهم",
                        "fieldOfStudy": ["ریاضی و فیزیک", "علوم تجربی"],
                        "chapters": [
                            {
                                "id": "ch_3_1",
                                "name": "فصل ۱: مولکول‌ها در خدمت تندرستی",
                                "order": 1,
                                "topics": [
                                    { "id": "top_3_1_1", "name": "پاک‌کننده‌ها و صابون‌ها", "order": 1 }
                                ]
                            },
                            {
                                "id": "ch_3_2",
                                "name": "فصل ۲: آسایش و رفاه در سایه شیمی",
                                "order": 2,
                                "topics": [
                                    { "id": "top_3_2_1", "name": "سلول‌های گالوانی و اکسایش-کاهش", "order": 1 }
                                ]
                            }
                        ]
                    },
                    {
                        "id": "book_4",
                        "name": "ادبیات فارسی عمومی",
                        "grade": "دوازدهم",
                        "fieldOfStudy": ["ریاضی و فیزیک", "علوم تجربی", "علوم انسانی"],
                        "chapters": [
                            {
                                "id": "ch_4_1",
                                "name": "بخش ادبی و قرابت معنایی",
                                "order": 1,
                                "topics": [
                                    { "id": "top_4_1_1", "name": "آرایه‌های ادبی و تست‌های سراسری", "order": 1 }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
        """.trimIndent()
    }

    private fun generateDailyStudyTasksJson(dateStr: String): String {
        return """
        {
            "body": {
                "date": "$dateStr",
                "summary": {
                    "total": 4,
                    "completed": 1,
                    "pending": 2,
                    "completionPercent": 25
                },
                "items": [
                    {
                        "id": "tsk_mock_1",
                        "sourceType": "GENERATED",
                        "title": "حسابان ۲ - تمرین و تست مشتق",
                        "book": { "id": "book_1", "name": "حسابان ۲" },
                        "chapter": { "id": "ch_1_2", "name": "فصل ۲: مشتق و کاربردها" },
                        "topic": { "id": "top_1_2_1", "name": "تعریف مشتق و فرمول‌های پایه" },
                        "scheduledOn": "$dateStr",
                        "periodCount": 1,
                        "minutesPerPeriod": 90,
                        "plannedMinutes": 90,
                        "activityType": "STUDY",
                        "sequence": 1,
                        "execution": {
                            "id": "exec_1",
                            "status": "COMPLETED",
                            "eventSequence": 3,
                            "activeSeconds": 5400,
                            "completionPercent": 100,
                            "startedAt": "${dateStr}T08:00:00Z",
                            "finishedAt": "${dateStr}T09:30:00Z"
                        }
                    },
                    {
                        "id": "tsk_mock_2",
                        "sourceType": "GENERATED",
                        "title": "فیزیک ۳ - دینامیک و قوانین حرکت",
                        "book": { "id": "book_2", "name": "فیزیک ۳" },
                        "chapter": { "id": "ch_2_2", "name": "فصل ۲: دینامیک و قوانین نیوتون" },
                        "topic": { "id": "top_2_2_1", "name": "اصطکاک، تکانه و نیروی گرانش" },
                        "scheduledOn": "$dateStr",
                        "periodCount": 1,
                        "minutesPerPeriod": 75,
                        "plannedMinutes": 75,
                        "activityType": "TEST",
                        "sequence": 2,
                        "execution": {
                            "id": "exec_2",
                            "status": "ACTIVE",
                            "eventSequence": 1,
                            "activeSeconds": 1800,
                            "completionPercent": 40,
                            "startedAt": "${dateStr}T10:00:00Z"
                        }
                    },
                    {
                        "id": "tsk_mock_3",
                        "sourceType": "GENERATED",
                        "title": "شیمی ۳ - سلول‌های گالوانی و اکسایش-کاهش",
                        "book": { "id": "book_3", "name": "شیمی ۳" },
                        "chapter": { "id": "ch_3_2", "name": "فصل ۲: آسایش و رفاه در سایه شیمی" },
                        "topic": { "id": "top_3_2_1", "name": "سلول‌های گالوانی و اکسایش-کاهش" },
                        "scheduledOn": "$dateStr",
                        "periodCount": 1,
                        "minutesPerPeriod": 60,
                        "plannedMinutes": 60,
                        "activityType": "STUDY",
                        "sequence": 3,
                        "execution": null
                    },
                    {
                        "id": "tsk_mock_4",
                        "sourceType": "MANUAL",
                        "title": "ادبیات فارسی - حل آزمون جامع آرایه‌های ادبی",
                        "book": { "id": "book_4", "name": "ادبیات فارسی عمومی" },
                        "chapter": { "id": "ch_4_1", "name": "بخش ادبی و قرابت معنایی" },
                        "topic": { "id": "top_4_1_1", "name": "آرایه‌های ادبی و تست‌های سراسری" },
                        "scheduledOn": "$dateStr",
                        "periodCount": 1,
                        "minutesPerPeriod": 45,
                        "plannedMinutes": 45,
                        "activityType": "TEST",
                        "sequence": 4,
                        "execution": null
                    }
                ]
            }
        }
        """.trimIndent()
    }
}
