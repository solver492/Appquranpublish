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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badr.quranstudio.data.Catalog

@Composable
fun StudioScreen(
    state: UiState,
    catalog: Catalog?,
    onUpdate: ((StudioState) -> StudioState) -> Unit,
    onGenerate: () -> Unit,
    onClearJob: () -> Unit,
) {
    val studio = state.studio
    val reciterName = catalog?.reciters?.firstOrNull { it.id == studio.reciterId }?.name
        ?: studio.reciterId
    val styleName = catalog?.styles?.firstOrNull { it.id == studio.styleId }?.name
        ?: studio.styleId

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            PreviewCard(
                studio = studio,
                reciterName = reciterName,
                styleName = styleName,
            )
        }

        item {
            SectionCard {
                Text("Versets", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
                StepperRow(
                    label = "Sourate",
                    value = studio.surah.toString(),
                    onMinus = { onUpdate { it.copy(surah = (it.surah - 1).coerceAtLeast(1)) } },
                    onPlus = { onUpdate { it.copy(surah = (it.surah + 1).coerceAtMost(114)) } },
                )
                StepperRow(
                    label = "Verset de départ",
                    value = studio.ayah.toString(),
                    onMinus = { onUpdate { it.copy(ayah = (it.ayah - 1).coerceAtLeast(1)) } },
                    onPlus = { onUpdate { it.copy(ayah = it.ayah + 1) } },
                )
                StepperRow(
                    label = "Nombre de versets",
                    value = studio.verseCount.toString(),
                    onMinus = { onUpdate { it.copy(verseCount = (it.verseCount - 1).coerceAtLeast(1)) } },
                    onPlus = { onUpdate { it.copy(verseCount = (it.verseCount + 1).coerceAtMost(10)) } },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60).forEach { duration ->
                        val selected = studio.duration == duration
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) Palette.Gold.copy(alpha = 0.2f)
                                    else Color(0x14FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (selected) Palette.Gold.copy(alpha = 0.5f) else Color.Transparent,
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { onUpdate { it.copy(duration = duration) } }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "${duration}s",
                                color = if (selected) Palette.Gold else Palette.Muted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionCard {
                Text("Récitateur", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
                if (catalog != null) {
                    ChipRow(
                        items = catalog.reciters.map { it.id to it.name },
                        selected = studio.reciterId,
                        onSelect = { id -> onUpdate { it.copy(reciterId = id) } },
                    )
                }
            }
        }

        item {
            SectionCard {
                Text("Arrière-plan", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
                if (catalog != null) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(catalog.backgrounds) { background ->
                            val selected = background.id == studio.backgroundId
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(84.dp)
                                    .clickable { onUpdate { it.copy(backgroundId = background.id) } },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(84.dp)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            if (selected) 2.dp else 1.dp,
                                            if (selected) Palette.Gold else Color(0x1FFFFFFF),
                                            RoundedCornerShape(12.dp),
                                        )
                                ) {
                                    BackgroundImage(background.id, Modifier.fillMaxSize())
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    background.name,
                                    fontSize = 10.sp,
                                    color = if (selected) Palette.Gold else Palette.Muted,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionCard {
                Text(
                    "Style des sous-titres",
                    fontWeight = FontWeight.Bold,
                    color = Palette.Text,
                    fontSize = 14.sp,
                )
                if (catalog != null) {
                    ChipRow(
                        items = catalog.styles.map { it.id to it.name },
                        selected = studio.styleId,
                        onSelect = { id -> onUpdate { it.copy(styleId = id) } },
                    )
                    catalog.styles.firstOrNull { it.id == studio.styleId }?.let {
                        Text(it.description, fontSize = 11.sp, color = Palette.Muted)
                    }
                }
            }
        }

        item {
            SectionCard {
                Text("Logo", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
                if (catalog != null) {
                    ChipRow(
                        items = catalog.logos.map { it.id to it.name },
                        selected = studio.logoId,
                        onSelect = { id -> onUpdate { it.copy(logoId = id) } },
                    )
                }
            }
        }

        item {
            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Traduction française", color = Palette.Text, fontSize = 13.sp)
                    Switch(
                        checked = studio.translation,
                        onCheckedChange = { checked -> onUpdate { it.copy(translation = checked) } },
                        colors = SwitchDefaults.colors(checkedTrackColor = Palette.Gold),
                    )
                }
                OutlinedTextField(
                    value = studio.watermark,
                    onValueChange = { value -> onUpdate { it.copy(watermark = value) } },
                    label = { Text("Watermark", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            if (state.generating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Palette.Success.copy(alpha = 0.15f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val progress = (state.job?.progress ?: 0) / 100f
                    Text(
                        "Génération en cours… ${state.job?.progress ?: 0}%",
                        color = Palette.Success,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Palette.Success,
                    )
                    state.job?.log?.lastOrNull()?.let {
                        Text(it, fontSize = 10.sp, color = Palette.Muted, maxLines = 2)
                    }
                }
            } else {
                Button(
                    onClick = onGenerate,
                    enabled = state.connected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Palette.Gold,
                        disabledContainerColor = Color(0x33E8C36A),
                    ),
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF1A1305),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.connected) "Générer le Reel" else "Serveur non connecté",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1305),
                        fontSize = 15.sp,
                    )
                }
                if (state.job?.status == "completed") {
                    TextButton(onClick = onClearJob) {
                        Text("Nouveau rendu", color = Palette.Gold, fontSize = 12.sp)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun PreviewCard(studio: StudioState, reciterName: String, styleName: String) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .border(1.dp, Color(0x33E8C36A), RoundedCornerShape(22.dp)),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage(studio.backgroundId, Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x99000000),
                                Color(0x66000000),
                                Color(0xE6000000),
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${studio.duration}s · 9:16",
                        color = Palette.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    if (studio.logoId != "none") {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xCCE8C36A)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("ب", color = Color(0xFF1A1305), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (studio.watermark.isNotBlank() && studio.styleId != "minimal") {
                        Text(
                            studio.watermark,
                            color = Color(0xCCFFFFFF),
                            fontSize = 12.sp,
                            style = androidx.compose.ui.text.TextStyle(textDirection = TextDirection.Rtl),
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    Text(
                        buildAnnotatedString {
                            append("وَمِنَ ٱلنَّاسِ مَن ")
                            withStyle(SpanStyle(color = Palette.Gold, fontWeight = FontWeight.Bold)) {
                                append("يَقُولُ")
                            }
                            append(" ءَامَنَّا بِٱللَّهِ")
                        },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = androidx.compose.ui.text.TextStyle(textDirection = TextDirection.Rtl),
                        lineHeight = 38.sp,
                    )
                    if (studio.translation && studio.styleId != "minimal") {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "« Il est des gens qui disent : Nous croyons en Allah »",
                            color = Color(0xCCFFFFFF),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🎙️ $reciterName",
                        color = Color(0x99FFFFFF),
                        fontSize = 11.sp,
                    )
                    Text(
                        styleName,
                        color = Color(0x66FFFFFF),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Palette.Muted, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMinus, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Diminuer", tint = Palette.Muted)
            }
            Text(
                value,
                color = Palette.Text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = onPlus, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Augmenter", tint = Palette.Gold)
            }
        }
    }
}
