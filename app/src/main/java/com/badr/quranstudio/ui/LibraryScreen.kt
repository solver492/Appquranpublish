package com.badr.quranstudio.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badr.quranstudio.data.Reel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dayFormat = DateTimeFormatter.ofPattern("dd/MM")

@Composable
fun LibraryScreen(
    reels: List<Reel>,
    onShare: (Reel) -> Unit,
    onPublish: (Reel) -> Unit,
) {
    var filter by remember { mutableStateOf("all") }
    val filtered = when (filter) {
        "published" -> reels.filter { it.isPublished }
        "pending" -> reels.filter { !it.isPublished }
        else -> reels
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ChipRow(
            items = listOf(
                "all" to "Tous",
                "published" to "Publiés",
                "pending" to "À publier",
            ),
            selected = filter,
            onSelect = { filter = it },
        )

        if (filtered.isEmpty()) {
            SectionCard {
                Text(
                    "Aucun Reel ici. Génère ton premier Reel depuis le Studio.",
                    fontSize = 13.sp,
                    color = Palette.Muted,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(filtered) { reel ->
                    ReelCard(
                        reel = reel,
                        onShare = { onShare(reel) },
                        onPublish = { onPublish(reel) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReelCard(reel: Reel, onShare: () -> Unit, onPublish: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.Surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                BackgroundImage(
                    idOrPath = reel.thumbnail.ifBlank { "nature_forest" },
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x22000000), Color(0xDD000000))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${reel.duration.toInt()}s",
                        color = Palette.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    StatusBadge(reel.status)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        reel.reference,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        reel.createdAt.take(10).let { date ->
                            runCatching {
                                LocalDateTime.parse(reel.createdAt).format(dayFormat)
                            }.getOrElse { date }
                        },
                        color = Color(0xAAFFFFFF),
                        fontSize = 10.sp,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onShare) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Partager",
                        tint = Palette.Gold,
                        modifier = Modifier.size(18.dp),
                    )
                }
                if (!reel.isPublished) {
                    IconButton(onClick = onPublish) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = "Publier sur Facebook",
                            tint = Palette.Success,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                } else {
                    Spacer(Modifier.size(18.dp))
                }
            }
        }
    }
}
