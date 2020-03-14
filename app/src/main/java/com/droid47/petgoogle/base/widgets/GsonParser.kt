package com.droid47.petgoogle.base.widgets

import com.google.gson.JsonElement
import com.google.gson.JsonObject

object GsonParser {

    fun parse(json: JsonElement): Map<String, String> {
        if (json.isJsonObject) {
            return jsonToMap(json as JsonObject)
        }
        return emptyMap()
    }

    private fun jsonToMap(jsonObject: JsonObject): Map<String, String> {
        return if (jsonObject.isJsonNull) {
            HashMap()
        } else toMap(jsonObject)
    }

    private fun toMap(obj: JsonObject): Map<String, String> {
        val map = HashMap<String, String>()
        for ((key, value) in obj.entrySet()) {
            map[key] = value.asString
        }
        return map
    }

//    fun parse(json: JsonElement): Any? {
//        if (json.isJsonObject) {
//            return jsonToMap(json as JsonObject)
//        } else if (json.isJsonArray) {
//            return jsonToList(json as JsonArray)
//        }
//        return null
//    }
//
//    private fun jsonToList(jsonArray: JsonArray): List<Any> {
//        return if (jsonArray.isJsonNull) {
//            ArrayList()
//        } else toList(jsonArray)
//
//    }
//
//    private fun toMap(obj: JsonObject): Map<String, Any> {
//        val map = HashMap<String, Any>()
//        for ((key, value) in obj.entrySet()) {
//            val result = toValue(value) ?: continue
//            map[key] = result
//        }
//        return map
//    }
//
//    private fun toList(array: JsonArray): List<Any> {
//        val list = ArrayList<Any>()
//        for (element in array) {
//            val value = toValue(element) ?: continue
//            list.add(value)
//        }
//        return list
//    }
//
//    private fun toPrimitive(value: JsonPrimitive): Any? {
//        return when {
//            value.isBoolean -> value.asBoolean
//            value.isString -> value.asString
//            value.isNumber -> value.asNumber
//            else -> null
//        }
//
//    }
//
//    private fun toValue(value: JsonElement): Any? {
//        return when {
//            value.isJsonNull -> null
//            value.isJsonArray -> toList(value as JsonArray)
//            value.isJsonObject -> toMap(value as JsonObject)
//            value.isJsonPrimitive -> toPrimitive(value as JsonPrimitive)
//            else -> null
//        }
//
//    }
}