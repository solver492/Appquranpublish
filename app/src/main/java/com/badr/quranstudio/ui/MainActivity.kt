package com.badr.quranstudio.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.badr.quranstudio.data.Reel
import com.badr.quranstudio.data.ShareInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TabItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
    val stageColor: Color,
)

private val APP_TABS = listOf(
    TabItem("home", "Accueil", Icons.Default.Home, Color(0xFFF59E0B), Color(0x33F59E0B)),
    TabItem("studio", "Studio", Icons.Default.AutoAwesome, Color(0xFF10B981), Color(0x3310B981)),
    TabItem("schedule", "Planning", Icons.Default.CalendarToday, Color(0xFF3B82F6), Color(0x333B82F6)),
    TabItem("library", "Reels", Icons.Default.VideoLibrary, Color(0xFFF97316), Color(0x33F97316)),
    TabItem("settings", "Réglages", Icons.Default.Settings, Color(0xFF8B5CF6), Color(0x338B5CF6)),
)

@Composable
fun BadrQuranStudioApp(viewModel: AppViewModel = viewModel()) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("home") }
    var publishTarget by remember { mutableStateOf<Reel?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.message) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }

    val currentTab = APP_TABS.firstOrNull { it.id == selectedTab } ?: APP_TABS[0]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.Bg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(currentTab.stageColor, Color.Transparent),
                        radius = 1200f,
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AnimatedCurvedNavigationBar(
                    tabs = APP_TABS,
                    selectedTabId = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TopBar(state)
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        "home" -> HomeScreen(
                            state = state,
                            onNavigateToStudio = { selectedTab = "studio" },
                            onNavigateToSchedule = { selectedTab = "schedule" },
                            onNavigateToLibrary = { selectedTab = "library" },
                            onShare = viewModel::share,
                        )
                        "studio" -> StudioScreen(
                            state = state,
                            catalog = state.catalog,
                            onUpdate = viewModel::updateStudio,
                            onGenerate = viewModel::generate,
                            onClearJob = viewModel::clearJob,
                        )
                        "schedule" -> ScheduleScreen(
                            state = state,
                            onLoadDate = viewModel::loadDate,
                            onToggleSlot = viewModel::toggleSlot,
                            onSave = viewModel::saveSchedule,
                        )
                        "library" -> LibraryScreen(
                            reels = state.reels,
                            onShare = viewModel::share,
                            onPublish = { publishTarget = it },
                        )
                        "settings" -> SettingsScreen(
                            state = state,
                            onSaveConnection = viewModel::saveConnection,
                            onTestConnection = viewModel::testConnection,
                            onSaveSettings = viewModel::saveSettings,
                        )
                    }
                }
            }
        }

        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC06110D)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Palette.Gold)
                    Spacer(Modifier.height(12.dp))
                    Text("Connexion au serveur…", color = Palette.Muted, fontSize = 12.sp)
                }
            }
        }
    }

    state.share?.let { share ->
        ShareDialog(
            share = share,
            onNative = { shareNative(context, share) },
            onFacebook = { openUrl(context, share.facebookUrl) },
            onWhatsApp = { openUrl(context, share.whatsappUrl) },
            onCopy = { copyToClipboard(context, share) },
            onDismiss = viewModel::consumeShare,
        )
    }

    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = viewModel::consumeError,
            title = { Text("Erreur", color = Palette.Text) },
            text = { Text(error, color = Palette.Muted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.consumeError()
                    viewModel.refresh()
                }) { Text("Réessayer", color = Palette.Gold) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::consumeError) { Text("Fermer", color = Palette.Muted) }
            },
            containerColor = Palette.Surface,
        )
    }

    publishTarget?.let { reel ->
        AlertDialog(
            onDismissRequest = { publishTarget = null },
            title = { Text("Publier maintenant ?", color = Palette.Text) },
            text = {
                Text(
                    "Le Reel « ${reel.reference} » sera publié sur la page Facebook Badr Quran.",
                    color = Palette.Muted,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.publish(reel)
                    publishTarget = null
                }) { Text("Publier", color = Palette.Gold) }
            },
            dismissButton = {
                TextButton(onClick = { publishTarget = null }) { Text("Annuler", color = Palette.Muted) }
            },
            containerColor = Palette.Surface,
        )
    }
}

@Composable
private fun TopBar(state: UiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(Palette.Gold, Palette.GoldDark))),
                contentAlignment = Alignment.Center,
            ) {
                Text("ب", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1305))
            }
            Column {
                Text("Badr Quran", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Text)
                Text(
                    "STUDIO REELS",
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFF71877B),
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        val (label, color) = when {
            state.connected -> "Connecté" to Palette.Success
            state.loading -> "Connexion…" to Palette.Muted
            else -> "Hors ligne" to Palette.Error
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(color.copy(alpha = 0.18f))
                .border(1.dp, color.copy(alpha = 0.35f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShareDialog(
    share: ShareInfo,
    onNative: () -> Unit,
    onFacebook: () -> Unit,
    onWhatsApp: () -> Unit,
    onCopy: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Palette.Surface,
        title = { Text("Partager · ${share.reference}", color = Palette.Text, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    share.caption.ifBlank { "Légende indisponible" },
                    color = Palette.Muted,
                    fontSize = 12.sp,
                    maxLines = 6,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ShareAction("Partager", Modifier.weight(1f), onNative)
                    ShareAction("Facebook", Modifier.weight(1f), onFacebook)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ShareAction("WhatsApp", Modifier.weight(1f), onWhatsApp)
                    ShareAction("Copier", Modifier.weight(1f), onCopy)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer", color = Palette.Gold) }
        },
    )
}

@Composable
private fun ShareAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x1FFFFFFF)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Text(label, fontSize = 11.sp, color = Palette.Text, textAlign = TextAlign.Center)
    }
}

@Composable
fun AnimatedCurvedNavigationBar(
    tabs: List<TabItem>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
) {
    val activeIndex = tabs.indexOfFirst { it.id == selectedTabId }.coerceAtLeast(0)
    val barHeight = 64.dp
    val bumpHeight = 16.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight + bumpHeight)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val bumpHeightPx = bumpHeight.toPx()
            val itemWidth = canvasWidth / tabs.size
            val centerX = itemWidth * (activeIndex + 0.5f)
            val bumpWidthPx = itemWidth * 0.9f
            val barTopPx = bumpHeightPx

            val path = Path().apply {
                moveTo(0f, barTopPx)
                val bumpLeft = (centerX - bumpWidthPx / 2f).coerceAtLeast(0f)
                val bumpRight = (centerX + bumpWidthPx / 2f).coerceAtMost(canvasWidth)
                lineTo(bumpLeft, barTopPx)
                cubicTo(
                    bumpLeft + bumpWidthPx * 0.22f, barTopPx,
                    centerX - bumpWidthPx * 0.24f, 0f,
                    centerX, 0f,
                )
                cubicTo(
                    centerX + bumpWidthPx * 0.24f, 0f,
                    bumpRight - bumpWidthPx * 0.22f, barTopPx,
                    bumpRight, barTopPx,
                )
                lineTo(canvasWidth, barTopPx)
                lineTo(canvasWidth, canvasHeight)
                lineTo(0f, canvasHeight)
                close()
            }
            drawPath(path = path, color = Color(0xFF0C2018))
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = bumpHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                val isActive = index == activeIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { onTabSelected(tab.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.offset(y = if (isActive) (-22).dp else 0.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(tab.accentColor)
                            )
                        }
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isActive) Color.White else Color(0x99FFFFFF),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun shareText(share: ShareInfo): String {
    val text = buildString {
        append(share.caption)
        if (share.publicUrl.isNotBlank()) {
            append("\n\n")
            append(share.publicUrl)
        }
    }
    return text.trim()
}

private fun shareNative(context: Context, share: ShareInfo) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText(share))
    }
    context.startActivity(Intent.createChooser(intent, "Partager le Reel"))
}

private fun openUrl(context: Context, url: String) {
    if (url.isBlank()) return
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }.onFailure {
        Toast.makeText(context, "Lien indisponible", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, share: ShareInfo) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Reel Badr Quran", shareText(share)))
    Toast.makeText(context, "Légende copiée", Toast.LENGTH_SHORT).show()
}
