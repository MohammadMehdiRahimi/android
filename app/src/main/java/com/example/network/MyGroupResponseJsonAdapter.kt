package com.example.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/**
 * The server's response transformer historically serialized a missing group as
 * `body: []`. The documented shape is `body: null`, while a membership is an
 * object. Accept both no-membership representations without weakening the
 * strongly typed membership model used by the rest of the group screen.
 */
class MyGroupResponseJsonAdapter(
    private val groupAdapter: JsonAdapter<MyGroupBody>,
) : JsonAdapter<MyGroupResponseDto>() {

    override fun fromJson(reader: JsonReader): MyGroupResponseDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return MyGroupResponseDto()
        }

        var body: MyGroupBody? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "body" -> body = readBody(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return MyGroupResponseDto(body)
    }

    private fun readBody(reader: JsonReader): MyGroupBody? = when (reader.peek()) {
        JsonReader.Token.NULL -> {
            reader.nextNull<Unit>()
            null
        }
        JsonReader.Token.BEGIN_ARRAY -> {
            reader.beginArray()
            while (reader.hasNext()) reader.skipValue()
            reader.endArray()
            null
        }
        JsonReader.Token.BEGIN_OBJECT -> groupAdapter.fromJson(reader)
        else -> {
            reader.skipValue()
            null
        }
    }

    override fun toJson(writer: JsonWriter, value: MyGroupResponseDto?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("body")
        groupAdapter.toJson(writer, value.body)
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: Set<Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            if (type != MyGroupResponseDto::class.java || annotations.isNotEmpty()) return null
            return MyGroupResponseJsonAdapter(moshi.adapter(MyGroupBody::class.java))
        }
    }
}
