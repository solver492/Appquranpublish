package com.badr.quranstudio.data

import android.content.Context

class Prefs(context: Context) {

    private val store = context.getSharedPreferences("badr_studio", Context.MODE_PRIVATE)

    var baseUrl: String
        get() = store.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) {
            store.edit().putString(KEY_BASE_URL, value.trim().trimEnd('/')).apply()
        }

    var token: String
        get() = store.getString(KEY_TOKEN, "") ?: ""
        set(value) {
            store.edit().putString(KEY_TOKEN, value.trim()).apply()
        }

    companion object {
        const val DEFAULT_BASE_URL = "https://badr.72-62-210-165.sslip.io/api"
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_TOKEN = "api_token"
    }
}
