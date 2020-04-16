package com.droid47.petfriend.app

import android.util.Base64
import java.nio.charset.Charset

object AppKeyMaker {
    private external fun getClientId(): String
    fun getClientIdValue(): String = getClientId()

    private external fun getClientSecret(): String
    fun getClientSecretValue(): String = getClientSecret()

    fun encode(value: String): String =
        Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)
            .trim { it <= ' ' }

    private fun decode(value: String): String = String(
        Base64.decode(value, Base64.DEFAULT),
        Charset.defaultCharset()
    )
}