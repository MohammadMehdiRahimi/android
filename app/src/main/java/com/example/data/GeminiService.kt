package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getRayaResponse(userPrompt: String, questionText: String, answerExplanation: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey.contains("MY_GEMINI_API_KEY")) {
            return@withContext "رایا جان، کلید اتصال به هوش مصنوعی تنظیم نشده است."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        
        val systemPrompt = """
            شما "رایا" هستید، یک دستیار و مشاور تحصیلی هوشمند دهمی‌ها. مهربان، دلگرم‌کننده و در عین حال بسیار دقیق و علمی صحبت می‌کنید.
            دانش‌آموز در حال دیدن پاسخ‌نامه تشریحی یک سوال است و از شما درباره این سوال توضیحات اضافی یا کمک خواسته است.
            مشخصات سوال فعلی:
            متن سوال: $questionText
            پاسخ تشریحی سوال: $answerExplanation

            لطفاً درخواست دانش‌آموز را به صورت کامل و جامع با زبان فارسی، همراه با لحن صمیمی رایا پاسخ دهید. اگر فرمول ریاضی یا کد استفاده می‌کنید، حتماً از فرمت استاندارد LaTeX با علامت $ برای درون‌خطی و $$ برای بلاک استفاده کنید تا زیباتر رندر شود.
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "خطا در ارتباط با رایا: ${response.code}"
                }
                val responseBodyStr = response.body?.string() ?: return@withContext "رایا در حال حاضر پاسخی ارسال نکرد."
                val jsonResponse = JSONObject(responseBodyStr)
                val candidates = jsonResponse.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.getJSONObject("content")
                    val parts = contentObj.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
                "رایا متوجه این پیام نشد. لطفاً دوباره تلاش کنید!"
            }
        } catch (e: Exception) {
            "خطایی رخ داد: ${e.message}"
        }
    }
}
