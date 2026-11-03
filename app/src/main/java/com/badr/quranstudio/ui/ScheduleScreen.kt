package com.badr.quranstudio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badr.quranstudio.data.Slot
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val hhmm = DateTimeFormatter.ofPattern("HH:mm")
private val dateLabel = DateTimeFormatter.ofPattern("EEEE d MMMM")

@Composable
fun ScheduleScreen(
    state: UiState,
    onLoadDate: (String) -> Unit,
    onToggleSlot: (String, Boolean) -> Unit,
    onSave: (Int) -> Unit,
) {
    val schedule = state.schedule
    var offset by remember(schedule?.date, schedule?.offsetMin) {
        mutableStateOf(schedule?.offsetMin ?: 10)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        val date = LocalDate.parse(state.date).minusDays(1).toString()
                        onLoadDate(date)
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Jour précédent", tint = Palette.Muted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            runCatching {
                                LocalDate.parse(state.date).format(dateLabel)
                            }.getOrDefault(state.date),
                            color = Palette.Text,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                        Text(
                            schedule?.let { "${it.city} · ${it.country}" } ?: "Chargement…",
                            color = Palette.Muted,
                            fontSize = 11.sp,
                        )
                    }
                    IconButton(onClick = {
                        val date = LocalDate.parse(state.date).plusDays(1).toString()
                        onLoadDate(date)
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Jour suivant", tint = Palette.Muted)
                    }
                }
            }
        }

        item {
            SectionCard {
                Text(
                    "Marge avant l'adhan",
                    fontWeight = FontWeight.Bold,
                    color = Palette.Text,
                    fontSize = 14.sp,
                )
                Text(
                    "$offset minutes avant chaque adhan",
                    fontSize = 12.sp,
                    color = Palette.Muted,
                )
                Slider(
                    value = offset.toFloat(),
                    onValueChange = { offset = (it / 5).toInt() * 5 },
                    valueRange = 0f..45f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Palette.Gold,
                        activeTrackColor = Palette.Gold,
                    ),
                )
                Button(
                    onClick = { onSave(offset) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.Gold),
                ) {
                    Text(
                        "Enregistrer le planning",
                        color = Color(0xFF1A1305),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        item {
            Text(
                "Un Reel est publié avant chaque adhan. Les versets avancent sans saut, de 1:1 à 114:6.",
                fontSize = 11.sp,
                color = Palette.Muted,
            )
        }

        items(schedule?.slots ?: emptyList()) { slot ->
            SlotCard(slot = slot, onToggle = { enabled -> onToggleSlot(slot.prayer, enabled) })
        }

        if (schedule == null) {
            item {
                SectionCard {
                    Text("Planning indisponible.", color = Palette.Muted, fontSize = 13.sp)
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun SlotCard(slot: Slot, onToggle: (Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(slot.prayer, fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
                    Text(slot.prayerAr, color = Palette.Gold, fontSize = 13.sp)
                    StatusBadge(slot.status)
                }
                Text(
                    "Adhan ${localTime(slot.adhan)} · publication ${localTime(slot.publishAt)}",
                    fontSize = 11.sp,
                    color = Palette.Muted,
                )
            }
            Switch(
                checked = slot.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedTrackColor = Palette.Gold),
            )
        }
    }
}

private fun localTime(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.systemDefault()).format(hhmm)
}.getOrDefault("--:--")
