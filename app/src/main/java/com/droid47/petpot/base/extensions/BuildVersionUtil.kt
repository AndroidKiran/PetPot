package com.droid47.petpot.base.extensions

import android.os.Build
import android.os.Build.VERSION_CODES
import java.lang.reflect.Field


object BuildVersionUtil {
    var OS_VERSION_NAME: String? = null

    init {
        OS_VERSION_NAME = osVersionName
    }

    val isOreoLower: Boolean
        get() = Build.VERSION.SDK_INT < VERSION_CODES.O

    val isOreoOrHigher: Boolean
        get() = Build.VERSION.SDK_INT >= VERSION_CODES.O

    val isNougatOrHigher: Boolean
        get() = Build.VERSION.SDK_INT >= VERSION_CODES.N

    val isAtLeastNMR1: Boolean
        get() = Build.VERSION.SDK_INT >= VERSION_CODES.N_MR1//Ignore//Ignore

    private val osVersionName: String
        get() {
            val builder = StringBuilder().apply {
                append(Build.VERSION.RELEASE)
            }
            val fields: Array<Field> = VERSION_CODES::class.java.fields
            for (fieldItem in fields) {
                val fieldName: String = fieldItem.name
                var fieldValue = -1
                try {
                    fieldValue = fieldItem.getInt(Any())
                } catch (e: IllegalArgumentException) {
                    //Ignore
                } catch (e: IllegalAccessException) {
                    //Ignore
                } catch (e: NullPointerException) {
                    //Ignore
                }
                if (fieldValue == Build.VERSION.SDK_INT) {
                    builder.append(' ').append(fieldName)
                    builder.append(" SDK ").append(fieldValue)
                }
            }
            return builder.toString()
        }
}