package com.example.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Generates ISO-8601 UTC timestamp with exact millisecond precision (e.g. 2026-08-25T11:00:00.000Z)
 * required by Shetab backend execution event endpoints.
 */
fun currentIsoUtcTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

/**
 * Custom JsonAdapter for StudyExecutionEventDto.
 * Omits null properties (completionOutcome, completionPercent, note) so that
 * backend validators (e.g. NestJS class-validator) do not reject requests with invalid null fields.
 */
class StudyExecutionEventJsonAdapter : JsonAdapter<StudyExecutionEventDto>() {

    override fun fromJson(reader: JsonReader): StudyExecutionEventDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return null
        }

        var clientEventId = ""
        var expectedSequence = 0
        var type = ""
        var occurredAt = ""
        var completionOutcome: String? = null
        var completionPercent: Int? = null
        var note: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "clientEventId" -> clientEventId = reader.nextString()
                "expectedSequence" -> expectedSequence = reader.nextInt()
                "type" -> type = reader.nextString()
                "occurredAt" -> occurredAt = reader.nextString()
                "completionOutcome" -> completionOutcome = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextString()
                "completionPercent" -> completionPercent = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextInt()
                "note" -> note = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return StudyExecutionEventDto(
            clientEventId = clientEventId,
            expectedSequence = expectedSequence,
            type = type,
            occurredAt = occurredAt,
            completionOutcome = completionOutcome,
            completionPercent = completionPercent,
            note = note,
        )
    }

    override fun toJson(writer: JsonWriter, value: StudyExecutionEventDto?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        writer.name("clientEventId").value(value.clientEventId)
        writer.name("expectedSequence").value(value.expectedSequence)
        writer.name("type").value(value.type)
        writer.name("occurredAt").value(value.occurredAt)

        if (value.completionOutcome != null) {
            writer.name("completionOutcome").value(value.completionOutcome)
        }
        if (value.completionPercent != null) {
            writer.name("completionPercent").value(value.completionPercent)
        }
        if (value.note != null) {
            writer.name("note").value(value.note)
        }
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: Set<Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            if (type == StudyExecutionEventDto::class.java && annotations.isEmpty()) {
                return StudyExecutionEventJsonAdapter()
            }
            return null
        }
    }
}

/**
 * Custom JsonAdapter for UpdateManualStudyTaskDto.
 * Only serializes fields that are explicitly provided (non-null), matching backend PATCH specification.
 */
class UpdateManualStudyTaskJsonAdapter : JsonAdapter<UpdateManualStudyTaskDto>() {

    override fun fromJson(reader: JsonReader): UpdateManualStudyTaskDto? {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return null
        }

        var topicId: String? = null
        var scheduledOn: String? = null
        var periodCount: Int? = null
        var minutesPerPeriod: Int? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "topicId" -> topicId = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextString()
                "scheduledOn" -> scheduledOn = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextString()
                "periodCount" -> periodCount = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextInt()
                "minutesPerPeriod" -> minutesPerPeriod = if (reader.peek() == JsonReader.Token.NULL) reader.nextNull() else reader.nextInt()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return UpdateManualStudyTaskDto(
            topicId = topicId,
            scheduledOn = scheduledOn,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )
    }

    override fun toJson(writer: JsonWriter, value: UpdateManualStudyTaskDto?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        if (value.topicId != null) {
            writer.name("topicId").value(value.topicId)
        }
        if (value.scheduledOn != null) {
            writer.name("scheduledOn").value(value.scheduledOn)
        }
        if (value.periodCount != null) {
            writer.name("periodCount").value(value.periodCount)
        }
        if (value.minutesPerPeriod != null) {
            writer.name("minutesPerPeriod").value(value.minutesPerPeriod)
        }
        writer.endObject()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: Set<Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            if (type == UpdateManualStudyTaskDto::class.java && annotations.isEmpty()) {
                return UpdateManualStudyTaskJsonAdapter()
            }
            return null
        }
    }
}
