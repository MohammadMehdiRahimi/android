package com.example.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MyGroupResponseJsonAdapterTest {
    private val adapter = Moshi.Builder()
        .add(MyGroupResponseJsonAdapter.Factory)
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(MyGroupResponseDto::class.java)

    @Test
    fun emptyArrayBodyMeansNoMembership() {
        val response = adapter.fromJson("""{"body":[],"status":"success","statusCode":200}""")

        assertNull(response?.body)
    }

    @Test
    fun nullBodyMeansNoMembership() {
        val response = adapter.fromJson("""{"body":null,"status":"success","statusCode":200}""")

        assertNull(response?.body)
    }

    @Test
    fun objectBodyStillParsesMembership() {
        val response = adapter.fromJson(
            """
            {
              "body": {
                "group": {
                  "id": "group-1",
                  "name": "گروه تست",
                  "ownerId": "user-1",
                  "inviteCode": "TEST42",
                  "isPublic": true
                },
                "member": { "role": "MEMBER" }
              }
            }
            """.trimIndent(),
        )

        assertEquals("group-1", response?.body?.group?.id)
        assertEquals("گروه تست", response?.body?.group?.name)
    }
}
