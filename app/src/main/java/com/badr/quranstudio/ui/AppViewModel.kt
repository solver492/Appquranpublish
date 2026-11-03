package com.badr.quranstudio.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.badr.quranstudio.data.BadrApi
import com.badr.quranstudio.data.Catalog
import com.badr.quranstudio.data.Job
import com.badr.quranstudio.data.Prefs
import com.badr.quranstudio.data.Reel
import com.badr.quranstudio.data.RenderSpec
import com.badr.quranstudio.data.Schedule
import com.badr.quranstudio.data.SettingsInfo
import com.badr.quranstudio.data.ShareInfo
import com.badr.quranstudio.data.StateInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StudioState(
    val surah: Int = 2,
    val ayah: Int = 1,
    val verseCount: Int = 2,
    val duration: Int = 30,
    val reciterId: String = "yasser_ad_dussary",
    val backgroundId: String = "nature_forest",
    val styleId: String = "gold_word",
    val logoId: String = "gif2",
    val translation: Boolean = true,
    val watermark: String = "بدر القرآنيات",
)

data class UiState(
    val loading: Boolean = true,
    val connected: Boolean = false,
    val error: String? = null,
    val baseUrl: String = "",
    val token: String = "",
    val catalog: Catalog? = null,
    val state: StateInfo? = null,
    val reels: List<Reel> = emptyList(),
    val schedule: Schedule? = null,
    val settings: SettingsInfo? = null,
    val date: String = LocalDate.now().toString(),
    val job: Job? = null,
    val generating: Boolean = false,
    val share: ShareInfo? = null,
    val message: String? = null,
    val studio: StudioState = StudioState(),
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = Prefs(application)
    private val api = BadrApi(prefs)

    private val _ui = MutableStateFlow(
        UiState(baseUrl = prefs.baseUrl, token = prefs.token)
    )
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh(silent: Boolean = false) {
        if (prefs.token.isBlank()) {
            _ui.update { it.copy(loading = false, connected = false, error = null) }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(loading = !silent && it.catalog == null, error = null) }
            try {
                val catalog = api.config()
                val state = api.state()
                val reels = api.reels()
                val schedule = api.schedule(_ui.value.date)
                val settings = api.settings()
                _ui.update { current ->
                    val studio = if (current.connected) current.studio else StudioState(
                        surah = state.surah,
                        ayah = state.ayah,
                        verseCount = catalog.defaults.verseCount,
                        duration = catalog.defaults.duration,
                        reciterId = catalog.defaults.reciter,
                        backgroundId = catalog.defaults.background,
                        styleId = catalog.defaults.subtitleStyle,
                        logoId = catalog.defaults.logo,
                        translation = catalog.defaults.translation,
                        watermark = catalog.defaults.watermark,
                    )
                    current.copy(
                        loading = false,
                        connected = true,
                        error = null,
                        catalog = catalog,
                        state = state,
                        reels = reels,
                        schedule = schedule,
                        settings = settings,
                        studio = studio,
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        loading = false,
                        connected = false,
                        error = e.message ?: "Serveur injoignable",
                    )
                }
            }
        }
    }

    fun saveConnection(baseUrl: String, token: String) {
        prefs.baseUrl = baseUrl
        prefs.token = token
        _ui.update { it.copy(baseUrl = prefs.baseUrl, token = prefs.token) }
        refresh()
    }

    fun testConnection(baseUrl: String, token: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val previousUrl = prefs.baseUrl
            val previousToken = prefs.token
            prefs.baseUrl = baseUrl
            prefs.token = token
            val ok = runCatching { api.config() }.isSuccess
            if (!ok) {
                prefs.baseUrl = previousUrl
                prefs.token = previousToken
            }
            onResult(ok, if (ok) "Connexion réussie" else "Connexion impossible")
        }
    }

    // --- Studio -------------------------------------------------------------

    fun updateStudio(transform: (StudioState) -> StudioState) {
        _ui.update { it.copy(studio = transform(it.studio)) }
    }

    fun generate() {
        val current = _ui.value
        if (current.generating) return
        val s = current.studio
        viewModelScope.launch {
            _ui.update { it.copy(generating = true, job = null, error = null) }
            try {
                val spec = RenderSpec(
                    surah = s.surah,
                    ayah = s.ayah,
                    verseCount = s.verseCount,
                    duration = s.duration,
                    reciter = s.reciterId,
                    background = s.backgroundId,
                    subtitleStyle = s.styleId,
                    logo = s.logoId,
                    translation = s.translation,
                    watermark = s.watermark,
                )
                val jobId = api.render(spec)
                var job = Job(jobId, "queued", 0, null, emptyList(), null)
                _ui.update { it.copy(job = job) }

                var attempts = 0
                while (attempts < 1200) {
                    delay(1500)
                    attempts += 1
                    job = api.job(jobId)
                    _ui.update { it.copy(job = job) }
                    if (job.status == "completed" || job.status == "failed") break
                }

                if (job.status == "completed") {
                    val reels = runCatching { api.reels() }.getOrDefault(emptyList())
                    val state = runCatching { api.state() }.getOrNull()
                    _ui.update {
                        it.copy(
                            generating = false,
                            reels = reels,
                            state = state ?: it.state,
                            message = "Reel généré et ajouté à la bibliothèque",
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            generating = false,
                            error = job.error ?: "Le rendu a échoué",
                        )
                    }
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(generating = false, error = e.message ?: "Échec du rendu")
                }
            }
        }
    }

    fun clearJob() {
        _ui.update { it.copy(job = null, error = null) }
    }

    // --- Bibliothèque -------------------------------------------------------

    fun publish(reel: Reel) {
        viewModelScope.launch {
            try {
                api.publish(reel.id)
                val reels = runCatching { api.reels() }.getOrDefault(_ui.value.reels)
                _ui.update {
                    it.copy(
                        reels = reels,
                        message = "Publication lancée sur Facebook",
                    )
                }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Publication impossible") }
            }
        }
    }

    fun share(reel: Reel) {
        viewModelScope.launch {
            try {
                val info = api.share(reel.id, reel.reference)
                _ui.update { it.copy(share = info) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Partage indisponible") }
            }
        }
    }

    fun consumeShare() {
        _ui.update { it.copy(share = null) }
    }

    fun consumeMessage() {
        _ui.update { it.copy(message = null) }
    }

    fun consumeError() {
        _ui.update { it.copy(error = null) }
    }

    // --- Planning -----------------------------------------------------------

    fun loadDate(date: String) {
        _ui.update { it.copy(date = date) }
        viewModelScope.launch {
            try {
                val schedule = api.schedule(date)
                _ui.update { it.copy(schedule = schedule) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Planning indisponible") }
            }
        }
    }

    fun toggleSlot(prayer: String, enabled: Boolean) {
        val date = _ui.value.date
        _ui.update { current ->
            current.copy(
                schedule = current.schedule?.copy(
                    slots = current.schedule.slots.map {
                        if (it.prayer == prayer) it.copy(enabled = enabled) else it
                    }
                )
            )
        }
        viewModelScope.launch {
            try {
                api.toggleSlot(date, prayer, enabled)
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Modification impossible") }
                loadDate(date)
            }
        }
    }

    fun saveSchedule(offsetMin: Int) {
        val current = _ui.value
        val date = current.date
        val settings = current.settings
        viewModelScope.launch {
            try {
                val schedule = api.saveSchedule(
                    date = date,
                    offsetMin = offsetMin,
                    city = settings?.city ?: "Casablanca",
                    country = settings?.country ?: "Morocco",
                    method = 21,
                )
                _ui.update {
                    it.copy(schedule = schedule, message = "Planning enregistré")
                }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Enregistrement impossible") }
            }
        }
    }

    fun saveSettings(offsetMin: Int, city: String, country: String) {
        viewModelScope.launch {
            try {
                val settings = api.saveSettings(offsetMin, city, country, 21)
                _ui.update {
                    it.copy(settings = settings, message = "Réglages enregistrés")
                }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Enregistrement impossible") }
            }
        }
    }
}
