package com.example.c001apk.util

import android.content.Context
import java.nio.charset.StandardCharsets




object FirstLaunchPassword {
    private const val PASSWORD_FILE = "password.base64"

    fun verify(context: Context, input: String): Boolean {
        if (input.isEmpty()) return false

        val configuredPassword = runCatching {
            context.assets.open(PASSWORD_FILE).use { inputStream ->
                val encoded = inputStream.readBytes()
                    .toString(StandardCharsets.UTF_8)
                    .trim()
                String(Base64Utils.decode(encoded), StandardCharsets.UTF_8)
            }
        }.getOrNull() ?: return false

        return input == configuredPassword
    }
}











private fun ByteArray.toString(charset: java.nio.charset.Charset): String =
    String(this, charset)

