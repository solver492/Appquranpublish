package com.badr.quranstudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badr.quranstudio.data.Reel
import com.badr.quranstudio.data.Slot
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun HomeScreen(
    state: UiState,
    onNavigateToStudio: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onShare: (Reel) -> Unit,
) {
    val schedule = state.schedule
    val now = remember(state.schedule) { OffsetDateTime.now() }
    val nextSlot = remember(schedule, now) {
        schedule?.slots
            ?.filter { it.enabled }
            ?.mapNotNull { slot ->
                runCatching { OffsetDateTime.parse(slot.publishAt) to slot }.getOrNull()
            }
            ?.filter { (time, _) -> time.isAfter(now) }
            ?.minByOrNull { it.first }
    }
    val published = state.reels.count { it.isPublished }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Palette.SurfaceHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33E8C36A), RoundedCornerShape(24.dp)),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = Palette.Gold,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                "Prochaine publication",
                                color = Palette.Gold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Text("5 Reels / jour", color = Palette.Muted, fontSize = 11.sp)
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        nextSlot?.second?.let { "${it.prayerAr} · ${it.prayer}" } ?: "Aucun créneau",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Palette.Text,
                    )
                    Text(
                        nextSlot?.let { (time, _) -> countdown(time) } ?: "Planning à vérifier",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Palette.Gold,
                    )
                    if (nextSlot != null) {
                        val local = runCatching {
                            OffsetDateTime.parse(nextSlot.second.publishAt)
                                .atZoneSameInstant(ZoneId.systemDefault())
                                .format(timeFormat)
                        }.getOrDefault("--:--")
                        Text(
                            "Publication à $local · adhan ${nextSlot.second.adhanTime()}",
                            fontSize = 12.sp,
                            color = Palette.Muted,
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onNavigateToStudio,
                            colors = ButtonDefaults.buttonColors(containerColor = Palette.Gold),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF1A1305),
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Créer un Reel",
                                color = Color(0xFF1A1305),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        OutlinedButton(
                            onClick = onNavigateToSchedule,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                        ) {
                            Text("Planning", color = Palette.Text)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    title = "${state.state?.totalGenerated ?: 0}",
                    subtitle = "Reels générés",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    title = "$published",
                    subtitle = "Publiés",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    title = "${state.state?.surah ?: 1}:${state.state?.ayah ?: 1}",
                    subtitle = "Prochain verset",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Derniers Reels",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Palette.Text,
                )
                TextButton(onClick = onNavigateToLibrary) {
                    Text("Tout voir", color = Palette.Gold)
                }
            }
        }

        item {
            if (state.reels.isEmpty()) {
                SectionCard {
                    Text(
                        "Aucun Reel pour le moment. Génère le premier depuis le Studio.",
                        fontSize = 13.sp,
                        color = Palette.Muted,
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.reels.take(4)) { reel ->
                        ReelMiniCard(reel = reel, onShare = { onShare(reel) })
                    }
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun ReelMiniCard(reel: Reel, onShare: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage(
                idOrPath = reel.thumbnail.ifBlank { "nature_forest" },
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0x33000000), Color(0xE6000000))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${reel.duration.toInt()}s",
                        color = Palette.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        reel.reference,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatusBadge(reel.status)
                        IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Partager",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Slot.adhanTime(): String = runCatching {
    OffsetDateTime.parse(adhan).atZoneSameInstant(ZoneId.systemDefault()).format(timeFormat)
}.getOrDefault("--:--")

private fun countdown(target: OffsetDateTime): String {
    val duration = Duration.between(OffsetDateTime.now(), target)
    if (duration.isNegative) return "maintenant"
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    return when {
        hours > 0 -> "dans ${hours} h ${minutes.toString().padStart(2, '0')}"
        minutes > 0 -> "dans $minutes min"
        else -> "dans moins d'une minute"
    }
}
