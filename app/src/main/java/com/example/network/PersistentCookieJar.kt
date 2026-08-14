package com.example.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs = context.getSharedPreferences("shetab_http_cookies", Context.MODE_PRIVATE)
    private val lock = Any()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) = synchronized(lock) {
        val current = readCookies(url).associateBy { key(it) }.toMutableMap()
        cookies.forEach { cookie ->
            if (cookie.expiresAt <= System.currentTimeMillis()) current.remove(key(cookie))
            else current[key(cookie)] = cookie
        }
        prefs.edit().putStringSet("cookies", current.values.map { it.toString() }.toSet()).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = synchronized(lock) {
        val all = readCookies(url)
        val valid = all.filter { it.expiresAt > System.currentTimeMillis() }
        if (valid.size != all.size) {
            prefs.edit().putStringSet("cookies", valid.map { it.toString() }.toSet()).apply()
        }
        valid.filter { it.matches(url) }
    }

    fun clear() = prefs.edit().clear().apply()

    private fun readCookies(url: HttpUrl): List<Cookie> =
        prefs.getStringSet("cookies", emptySet()).orEmpty().mapNotNull { Cookie.parse(url, it) }

    private fun key(cookie: Cookie) = "${cookie.name}|${cookie.domain}|${cookie.path}"
}
