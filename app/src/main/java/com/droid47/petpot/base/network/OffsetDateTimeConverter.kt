package com.droid47.petpot.base.network

import com.google.gson.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalQuery
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject

class OffsetDateTimeConverter @Inject constructor() : JsonSerializer<OffsetDateTime>,
    JsonDeserializer<OffsetDateTime> {

    override fun serialize(
        src: OffsetDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(ISO_DATE_FORMATTER.format(src))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OffsetDateTime {
        return ISO_DATE_FORMATTER.parse(json?.asString, tq)
    }

    private val tq: TemporalQuery<OffsetDateTime> =
        TemporalQuery<OffsetDateTime> { temporal ->
            OffsetDateTime.from(temporal)
        }

    companion object {
        val ISO_DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
    }
}