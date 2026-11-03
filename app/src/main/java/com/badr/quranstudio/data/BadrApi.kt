package com.badr.quranstudio.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ApiException(message: String) : Exception(message)

class BadrApi(private val prefs: Prefs) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonType = "application/json; charset=utf-8".toMediaType()

    private suspend fun call(
        method: String,
        path: String,
        body: JSONObject? = null,
    ): JSONObject = withContext(Dispatchers.IO) {
        val url = prefs.baseUrl.trimEnd('/') + path
        val builder = Request.Builder().url(url)
        if (prefs.token.isNotBlank()) {
            builder.header("Authorization", "Bearer ${prefs.token}")
        }
        val payload = (body ?: JSONObject()).toString().toRequestBody(jsonType)
        when (method) {
            "GET" -> builder.get()
            "POST" -> builder.post(payload)
            "PUT" -> builder.put(payload)
            "PATCH" -> builder.patch(payload)
            "DELETE" -> builder.delete()
            else -> builder.get()
        }
        client.newCall(builder.build()).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val detail = runCatching { JSONObject(text).optString("detail") }.getOrNull()
                throw ApiException(
                    detail?.takeIf { it.isNotBlank() } ?: "Erreur HTTP ${response.code}"
                )
            }
            if (text.isBlank()) JSONObject() else JSONObject(text)
        }
    }

    suspend fun health(): Boolean = runCatching {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(prefs.baseUrl.trimEnd('/') + "/health")
                .get()
                .build()
            client.newCall(request).execute().use { it.isSuccessful }
        }
    }.getOrDefault(false)

    suspend fun config(): Catalog {
        val o = call("GET", "/config")
        val defaults = o.optJSONObject("defaults") ?: JSONObject()
        return Catalog(
            reciters = o.optJSONArray("reciters").objects().map {
                Reciter(
                    id = it.optString("id"),
                    name = it.optString("name"),
                    arabicName = it.stringOrNull("arabic_name"),
                )
            },
            backgrounds = o.optJSONArray("backgrounds").objects().map {
                Background(id = it.optString("id"), name = it.optString("name"))
            },
            styles = o.optJSONArray("subtitle_styles").objects().map {
                SubtitleStyle(
                    id = it.optString("id"),
                    name = it.optString("name"),
                    description = it.optString("description"),
                )
            },
            logos = o.optJSONArray("logos").objects().map {
                LogoOption(id = it.optString("id"), name = it.optString("name"))
            },
            defaults = Defaults(
                reciter = defaults.optString("reciter", "yasser_ad_dussary"),
                background = defaults.optString("background", "nature_forest"),
                subtitleStyle = defaults.optString("subtitle_style", "gold_word"),
                logo = defaults.optString("logo", "gif2"),
                duration = defaults.optInt("duration", 30),
                verseCount = defaults.optInt("verse_count", 2),
                translation = defaults.optBoolean("translation", true),
                watermark = defaults.optString("watermark", "بدر القرآنيات"),
            ),
        )
    }

    suspend fun state(): StateInfo {
        val o = call("GET", "/state")
        return StateInfo(
            surah = o.optInt("surah", 1),
            ayah = o.optInt("ayah", 1),
            totalGenerated = o.optInt("total_generated", 0),
            mode = o.optString("mode", "manual"),
        )
    }

    suspend fun reels(): List<Reel> {
        val o = call("GET", "/reels")
        return o.optJSONArray("items").objects().map { it.toReel() }
    }

    suspend fun schedule(date: String): Schedule {
        val o = call("GET", "/schedules/$date")
        return o.toSchedule()
    }

    suspend fun saveSchedule(
        date: String,
        offsetMin: Int,
        city: String,
        country: String,
        method: Int,
    ): Schedule {
        val body = JSONObject()
            .put("enabled", true)
            .put("offset_min", offsetMin)
            .put("city", city)
            .put("country", country)
            .put("method", method)
        return call("PUT", "/schedules/$date", body).toSchedule()
    }

    suspend fun toggleSlot(date: String, prayer: String, enabled: Boolean): Slot {
        val body = JSONObject().put("enabled", enabled)
        val o = call("PATCH", "/schedules/$date/slots/$prayer", body)
        return o.toSlot()
    }

    suspend fun render(spec: RenderSpec): String {
        val body = JSONObject()
            .put("surah", spec.surah)
            .put("ayah", spec.ayah)
            .put("verse_count", spec.verseCount)
            .put("duration", spec.duration)
            .put("reciter", spec.reciter)
            .put("background", spec.background)
            .put("subtitle_style", spec.subtitleStyle)
            .put("logo", spec.logo)
            .put("translation", spec.translation)
            .put("watermark", spec.watermark)
            .put("publish", false)
        return call("POST", "/reels/render", body).optString("job_id")
    }

    suspend fun job(id: String): Job {
        val o = call("GET", "/jobs/$id")
        return Job(
            id = o.optString("id", id),
            status = o.optString("status", "queued"),
            progress = o.optInt("progress", 0),
            error = o.stringOrNull("error"),
            log = o.optJSONArray("log").strings(),
            reelId = o.stringOrNull("reel_id"),
        )
    }

    suspend fun publish(reelId: String, whenIso: String? = null) {
        val body = JSONObject().put("when", whenIso ?: JSONObject.NULL)
        call("POST", "/reels/$reelId/publish", body)
    }

    suspend fun share(reelId: String, reference: String): ShareInfo {
        val o = call("GET", "/reels/$reelId/share")
        return ShareInfo(
            reelId = reelId,
            reference = reference,
            caption = o.optString("caption"),
            publicUrl = o.optString("public_url"),
            facebookUrl = o.optString("facebook_share_url"),
            whatsappUrl = o.optString("whatsapp_url"),
        )
    }

    suspend fun settings(): SettingsInfo {
        val o = call("GET", "/settings")
        return SettingsInfo(
            pageName = o.optString("page_name"),
            facebookPageId = o.optString("facebook_page_id"),
            city = o.optString("city", "Casablanca"),
            country = o.optString("country", "Morocco"),
            offsetMin = o.optInt("offset_min", 10),
        )
    }

    suspend fun saveSettings(offsetMin: Int, city: String, country: String, method: Int): SettingsInfo {
        val body = JSONObject()
            .put("offset_min", offsetMin)
            .put("city", city)
            .put("country", country)
            .put("method", method)
        call("PUT", "/settings", body)
        return settings()
    }
}

// ---------------------------------------------------------------------------
// Helpers JSON
// ---------------------------------------------------------------------------

private fun JSONArray?.objects(): List<JSONObject> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { optJSONObject(it) }
}

private fun JSONArray?.strings(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).map { optString(it) }.filter { it.isNotBlank() }
}

private fun JSONObject.stringOrNull(key: String): String? =
    if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

private fun JSONObject.intOrNull(key: String): Int? =
    if (isNull(key)) null else optInt(key)

private fun JSONObject.toReel(): Reel {
    val publish = optJSONObject("publish")
    return Reel(
        id = optString("id"),
        filename = optString("filename"),
        publicUrl = optString("public_url"),
        thumbnail = optString("thumbnail"),
        createdAt = optString("created_at"),
        duration = optDouble("duration", 0.0),
        surah = intOrNull("surah"),
        ayahStart = intOrNull("ayah_start"),
        ayahEnd = intOrNull("ayah_end"),
        surahName = stringOrNull("surah_name"),
        surahNameArabic = stringOrNull("surah_name_arabic"),
        arabicText = stringOrNull("arabic_text"),
        translation = stringOrNull("translation"),
        caption = stringOrNull("caption"),
        status = optString("status", "rendered"),
        publishUrl = publish?.stringOrNull("url"),
    )
}

private fun JSONObject.toSlot(): Slot = Slot(
    prayer = optString("prayer"),
    prayerAr = optString("prayer_ar"),
    adhan = optString("adhan"),
    publishAt = optString("publish_at"),
    publishAtLocal = optString("publish_at_local"),
    enabled = optBoolean("enabled", true),
    status = optString("status", "pending"),
    reelId = stringOrNull("reel_id"),
    platformUrl = stringOrNull("platform_url"),
)

private fun JSONObject.toSchedule(): Schedule = Schedule(
    date = optString("date"),
    city = optString("city", "Casablanca"),
    country = optString("country", "Morocco"),
    method = optInt("method", 21),
    offsetMin = optInt("offset_min", 10),
    slots = optJSONArray("slots").objects().map { it.toSlot() },
)
