package com.example.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/**
 * Custom JsonAdapter for DailyStudyTasksResponseDto.
 * Supports both direct root payload `{ "date": "...", "items": [...], "summary": {...} }`
 * and wrapped payload `{ "body": { "date": "...", "items": [...], "summary": {...} } }`.
 */
class DailyStudyTasksResponseJsonAdapter(
    private val bodyAdapter: JsonAdapter<DailyStudyTasksBodyDto>,
) : JsonAdapter<DailyStudyTasksResponseDto>() {

    override fun fromJson(reader: JsonReader): DailyStudyTasksResponseDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return DailyStudyTasksResponseDto(null)
        }

        // Peek or read into a buffer / map
        val jsonValue = reader.readJsonValue()
        if (jsonValue !is Map<*, *>) {
            return DailyStudyTasksResponseDto(null)
        }

        // Check if wrapped in "body"
        if (jsonValue.containsKey("body") && jsonValue["body"] is Map<*, *>) {
            val body = bodyAdapter.fromJsonValue(jsonValue["body"])
            return DailyStudyTasksResponseDto(body)
        }

        // Otherwise parse directly from root map
        val body = bodyAdapter.fromJsonValue(jsonValue)
        return DailyStudyTasksResponseDto(body)
    }

    override fun toJson(writer: JsonWriter, value: DailyStudyTasksResponseDto?) {
        if (value?.body == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("body")
        bodyAdapter.toJson(writer, value.body)
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            if (type == DailyStudyTasksResponseDto::class.java && annotations.isEmpty()) {
                return DailyStudyTasksResponseJsonAdapter(moshi.adapter(DailyStudyTasksBodyDto::class.java))
            }
            return null
        }
    }
}

/**
 * Custom JsonAdapter for ManualStudyTaskResponseDto.
 * Supports both direct root payload `{ "id": "...", "title": "...", ... }`
 * and wrapped payload `{ "body": { "id": "...", ... } }`.
 */
class ManualStudyTaskResponseJsonAdapter(
    private val bodyAdapter: JsonAdapter<ManualStudyTaskBodyDto>,
) : JsonAdapter<ManualStudyTaskResponseDto>() {

    override fun fromJson(reader: JsonReader): ManualStudyTaskResponseDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return ManualStudyTaskResponseDto(null)
        }

        val jsonValue = reader.readJsonValue()
        if (jsonValue !is Map<*, *>) {
            return ManualStudyTaskResponseDto(null)
        }

        if (jsonValue.containsKey("body") && jsonValue["body"] is Map<*, *>) {
            val body = bodyAdapter.fromJsonValue(jsonValue["body"])
            return ManualStudyTaskResponseDto(body)
        }

        val body = bodyAdapter.fromJsonValue(jsonValue)
        return ManualStudyTaskResponseDto(body)
    }

    override fun toJson(writer: JsonWriter, value: ManualStudyTaskResponseDto?) {
        if (value?.body == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("body")
        bodyAdapter.toJson(writer, value.body)
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            if (type == ManualStudyTaskResponseDto::class.java && annotations.isEmpty()) {
                return ManualStudyTaskResponseJsonAdapter(moshi.adapter(ManualStudyTaskBodyDto::class.java))
            }
            return null
        }
    }
}

/**
 * Custom JsonAdapter for StudyExecutionResponseDto.
 * Supports both direct root payload `{ "id": "...", "status": "...", "eventSequence": 1, ... }`
 * and wrapped payload `{ "body": { "id": "...", ... } }`.
 */
class StudyExecutionResponseJsonAdapter(
    private val bodyAdapter: JsonAdapter<StudyExecutionBodyDto>,
) : JsonAdapter<StudyExecutionResponseDto>() {

    override fun fromJson(reader: JsonReader): StudyExecutionResponseDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return StudyExecutionResponseDto(null)
        }

        val jsonValue = reader.readJsonValue()
        if (jsonValue !is Map<*, *>) {
            return StudyExecutionResponseDto(null)
        }

        if (jsonValue.containsKey("body") && jsonValue["body"] is Map<*, *>) {
            val body = bodyAdapter.fromJsonValue(jsonValue["body"])
            return StudyExecutionResponseDto(body)
        }

        val body = bodyAdapter.fromJsonValue(jsonValue)
        return StudyExecutionResponseDto(body)
    }

    override fun toJson(writer: JsonWriter, value: StudyExecutionResponseDto?) {
        if (value?.body == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("body")
        bodyAdapter.toJson(writer, value.body)
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            if (type == StudyExecutionResponseDto::class.java && annotations.isEmpty()) {
                return StudyExecutionResponseJsonAdapter(moshi.adapter(StudyExecutionBodyDto::class.java))
            }
            return null
        }
    }
}

/**
 * Custom JsonAdapter for StudyTaskCatalogResponseDto.
 * Supports both direct root payload `{ "books": [...], "academicProfile": {...} }`
 * and wrapped payload `{ "body": { "books": [...] } }`.
 */
class StudyTaskCatalogResponseJsonAdapter(
    private val bodyAdapter: JsonAdapter<StudyTaskCatalogBodyDto>,
) : JsonAdapter<StudyTaskCatalogResponseDto>() {

    override fun fromJson(reader: JsonReader): StudyTaskCatalogResponseDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return StudyTaskCatalogResponseDto(null)
        }

        val jsonValue = reader.readJsonValue()
        if (jsonValue !is Map<*, *>) {
            return StudyTaskCatalogResponseDto(null)
        }

        if (jsonValue.containsKey("body") && jsonValue["body"] is Map<*, *>) {
            val body = bodyAdapter.fromJsonValue(jsonValue["body"])
            return StudyTaskCatalogResponseDto(body)
        }

        val body = bodyAdapter.fromJsonValue(jsonValue)
        return StudyTaskCatalogResponseDto(body)
    }

    override fun toJson(writer: JsonWriter, value: StudyTaskCatalogResponseDto?) {
        if (value?.body == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("body")
        bodyAdapter.toJson(writer, value.body)
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            if (type == StudyTaskCatalogResponseDto::class.java && annotations.isEmpty()) {
                return StudyTaskCatalogResponseJsonAdapter(moshi.adapter(StudyTaskCatalogBodyDto::class.java))
            }
            return null
        }
    }
}
