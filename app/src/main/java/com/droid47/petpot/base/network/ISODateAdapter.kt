package com.droid47.petpot.base.network

import android.R.attr
import com.google.gson.*
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ISODateAdapter: JsonSerializer<Date>, JsonDeserializer<Date> {

    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    }

    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val dateFormatAsString = iso8601Format.format(attr.src)
        return JsonPrimitive(dateFormatAsString)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        if (json !is JsonPrimitive) {
            throw JsonParseException("The date should be a string value")
        }
        val date = deserializeToDate(json)
        return when {
            typeOfT === Date::class.java -> date
            typeOfT === Timestamp::class.java -> Timestamp(date.time)
            typeOfT === java.sql.Date::class.java -> java.sql.Date(date.time)
            else -> throw IllegalArgumentException("$javaClass cannot deserialize to $typeOfT")
        }
    }

    private fun deserializeToDate(json: JsonElement): Date =
        try {
            iso8601Format.parse(json.asString)
        } catch (e: ParseException) {
            throw JsonSyntaxException(json.asString, e)
        }


}