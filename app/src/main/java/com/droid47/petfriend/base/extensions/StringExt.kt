package com.droid47.petfriend.base.extensions

import android.annotation.TargetApi
import android.os.Build
import android.text.Html
import android.text.TextUtils
import java.util.*


private const val LT_ESCAPE_STRING = "&lt;"
private const val GT_ESCAPE_STRING = "&gt;"
const val EMPTY_CHAR_SEQUENCE = ""

fun CharSequence?.isEmpty(): Boolean {
    var inputValue = this
    if (inputValue == null || TextUtils.isEmpty(inputValue))
        return true
    inputValue = inputValue.toString().trim { it <= ' ' }
    return TextUtils.isEmpty(inputValue) || inputValue == ""
}

fun CharSequence?.isNotEmpty(): Boolean = !this.isEmpty()

fun CharSequence?.notHasNullStr(): Boolean = this != "null"

fun String.fromHtml(): CharSequence {
    if (isEmpty()) return ""
    val charSequence = wrappedFromHtml(this)
    if (isEmpty()) return ""
    return if (!(contains(LT_ESCAPE_STRING) && contains(GT_ESCAPE_STRING))) {
        charSequence
    } else wrappedFromHtml(
        charSequence.toString()
    )
}

@TargetApi(Build.VERSION_CODES.N)
private fun wrappedFromHtml(value: String): CharSequence {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
    else
        Html.fromHtml(value)
}

fun String?.wordCapitalize(vararg delimiters: Char): String? {
    if (this == null || isEmpty()) return this
    val delimLen = delimiters.size
    if (isEmpty() || delimLen == 0) {
        return this
    }
    val buffer = toCharArray()
    var capitalizeNext = true
    for (i in buffer.indices) {
        val ch = buffer[i]
        if (isDelimiter(
                ch,
                delimiters
            )
        ) {
            capitalizeNext = true
        } else if (capitalizeNext) {
            buffer[i] = Character.toTitleCase(ch)
            capitalizeNext = false
        }
    }
    return String(buffer)
}

private fun isDelimiter(ch: Char, delimiters: CharArray?): Boolean {
    if (delimiters == null) {
        return Character.isWhitespace(ch)
    }
    for (delimiter in delimiters) {
        if (ch == delimiter) {
            return true
        }
    }
    return false
}

fun String.stringSentenceCase(): String {
    return if (trim { it <= ' ' }.isEmpty()) this else Character.toUpperCase(this[0])
        .toString() + this.substring(
        1
    ).toLowerCase(Locale.US)
}