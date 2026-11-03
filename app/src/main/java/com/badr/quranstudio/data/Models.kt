package com.badr.quranstudio.data

data class Reciter(
    val id: String,
    val name: String,
    val arabicName: String?,
)

data class Background(
    val id: String,
    val name: String,
)

data class SubtitleStyle(
    val id: String,
    val name: String,
    val description: String,
)

data class LogoOption(
    val id: String,
    val name: String,
)

data class Defaults(
    val reciter: String,
    val background: String,
    val subtitleStyle: String,
    val logo: String,
    val duration: Int,
    val verseCount: Int,
    val translation: Boolean,
    val watermark: String,
)

data class Catalog(
    val reciters: List<Reciter>,
    val backgrounds: List<Background>,
    val styles: List<SubtitleStyle>,
    val logos: List<LogoOption>,
    val defaults: Defaults,
)

data class StateInfo(
    val surah: Int,
    val ayah: Int,
    val totalGenerated: Int,
    val mode: String,
)

data class Reel(
    val id: String,
    val filename: String,
    val publicUrl: String,
    val thumbnail: String,
    val createdAt: String,
    val duration: Double,
    val surah: Int?,
    val ayahStart: Int?,
    val ayahEnd: Int?,
    val surahName: String?,
    val surahNameArabic: String?,
    val arabicText: String?,
    val translation: String?,
    val caption: String?,
    val status: String,
    val publishUrl: String?,
) {
    val reference: String
        get() {
            val name = surahName ?: "Sourate"
            val s = surah ?: 0
            val a1 = ayahStart ?: 0
            val a2 = ayahEnd ?: a1
            return if (a1 == a2) "$name $s:$a1" else "$name $s:$a1-$a2"
        }

    val isPublished: Boolean get() = status == "published"
}

data class Slot(
    val prayer: String,
    val prayerAr: String,
    val adhan: String,
    val publishAt: String,
    val publishAtLocal: String,
    val enabled: Boolean,
    val status: String,
    val reelId: String?,
    val platformUrl: String?,
)

data class Schedule(
    val date: String,
    val city: String,
    val country: String,
    val method: Int,
    val offsetMin: Int,
    val slots: List<Slot>,
)

data class Job(
    val id: String,
    val status: String,
    val progress: Int,
    val error: String?,
    val log: List<String>,
    val reelId: String?,
)

data class SettingsInfo(
    val pageName: String,
    val facebookPageId: String,
    val city: String,
    val country: String,
    val offsetMin: Int,
)

data class ShareInfo(
    val reelId: String,
    val reference: String,
    val caption: String,
    val publicUrl: String,
    val facebookUrl: String,
    val whatsappUrl: String,
)

data class RenderSpec(
    val surah: Int,
    val ayah: Int,
    val verseCount: Int,
    val duration: Int,
    val reciter: String,
    val background: String,
    val subtitleStyle: String,
    val logo: String,
    val translation: Boolean,
    val watermark: String,
)
