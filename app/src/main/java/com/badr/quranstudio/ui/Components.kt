package com.badr.quranstudio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badr.quranstudio.R

object Palette {
    val Bg = Color(0xFF06110D)
    val Surface = Color(0xFF0C2018)
    val SurfaceHigh = Color(0xFF0F2B20)
    val Gold = Color(0xFFE8C36A)
    val GoldDark = Color(0xFFC79A3E)
    val Text = Color(0xFFF4F7F3)
    val Muted = Color(0xFF9BB0A4)
    val Success = Color(0xFF3F8B68)
    val Error = Color(0xFFD97B6C)
    val Info = Color(0xFF3B82F6)
}

fun backgroundRes(idOrPath: String): Int? {
    val key = idOrPath.substringAfterLast('/').substringBeforeLast('.')
    return when (key) {
        "nature_waves" -> R.drawable.nature_waves
        "nature_sea_rocks" -> R.drawable.nature_sea_rocks
        "nature_flowers" -> R.drawable.nature_flowers
        "nature_waterfall" -> R.drawable.nature_waterfall
        "nature_forest" -> R.drawable.nature_forest
        "nature_bay" -> R.drawable.nature_bay
        "nature_clouds" -> R.drawable.nature_clouds
        "nature_meadow" -> R.drawable.nature_meadow
        else -> null
    }
}

@Composable
fun BackgroundImage(idOrPath: String, modifier: Modifier = Modifier) {
    val res = backgroundRes(idOrPath)
    if (res != null) {
        Image(
            painter = painterResource(res),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(listOf(Color(0xFF123527), Palette.Bg))
            )
        )
    }
}

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x14FFFFFF)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

@Composable
fun StatCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Palette.Text)
            Text(subtitle, fontSize = 11.sp, color = Palette.Muted)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "published" -> "Publié" to Palette.Success
        "scheduled" -> "Programmé" to Palette.Gold
        "failed" -> "Échec" to Palette.Error
        "missed" -> "Manqué" to Palette.Error
        "pending" -> "En attente" to Palette.Muted
        else -> "Rendu" to Palette.Info
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ChipRow(
    items: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        items(items) { (id, label) ->
            FilterChip(
                selected = id == selected,
                onClick = { onSelect(id) },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Palette.Gold.copy(alpha = 0.2f),
                    selectedLabelColor = Palette.Gold,
                    labelColor = Palette.Muted,
                ),
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 12.sp, color = Palette.Muted)
        Text(value, fontSize = 12.sp, color = Palette.Text, fontWeight = FontWeight.SemiBold)
    }
}
