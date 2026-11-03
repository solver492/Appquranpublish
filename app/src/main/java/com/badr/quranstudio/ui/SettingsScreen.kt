package com.badr.quranstudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun SettingsScreen(
    state: UiState,
    onSaveConnection: (String, String) -> Unit,
    onTestConnection: (String, String, (Boolean, String) -> Unit) -> Unit,
    onSaveSettings: (Int, String, String) -> Unit,
) {
    var baseUrl by remember(state.baseUrl) { mutableStateOf(state.baseUrl) }
    var token by remember(state.token) { mutableStateOf(state.token) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testOk by remember { mutableStateOf(false) }
    var city by remember(state.settings?.city) { mutableStateOf(state.settings?.city ?: "Casablanca") }
    var country by remember(state.settings?.country) { mutableStateOf(state.settings?.country ?: "Morocco") }
    var offset by remember(state.settings?.offsetMin) { mutableStateOf(state.settings?.offsetMin ?: 10) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionCard {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(
                            if (state.connected) Palette.Success else Palette.Error,
                            CircleShape,
                        )
                )
                Text(
                    if (state.connected) "Connecté au serveur" else "Hors ligne",
                    fontWeight = FontWeight.Bold,
                    color = if (state.connected) Palette.Success else Palette.Error,
                    fontSize = 14.sp,
                )
            }
            Text(
                "L'application pilote le pipeline installé sur ton serveur. Le jeton est stocké uniquement sur ce téléphone.",
                fontSize = 12.sp,
                color = Palette.Muted,
            )
        }

        SectionCard {
            Text("Connexion serveur", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("URL de l'API", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Jeton d'accès", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        onTestConnection(baseUrl, token) { ok, message ->
                            testOk = ok
                            testResult = message
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Tester", color = Palette.Text)
                }
                Button(
                    onClick = { onSaveConnection(baseUrl, token) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.Gold),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Enregistrer", color = Color(0xFF1A1305), fontWeight = FontWeight.Bold)
                }
            }
            testResult?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    color = if (testOk) Palette.Success else Palette.Error,
                )
            }
        }

        SectionCard {
            Text("Page Facebook", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
            InfoRow("Nom", state.settings?.pageName?.ifBlank { "Badr Quran" } ?: "Badr Quran")
            InfoRow(
                "ID de page",
                state.settings?.facebookPageId?.ifBlank { "non configuré" } ?: "non configuré",
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ville", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Pays", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Marge avant l'adhan", fontSize = 12.sp, color = Palette.Muted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { offset = (offset - 5).coerceAtLeast(0) },
                        shape = RoundedCornerShape(8.dp),
                    ) { Text("−", color = Palette.Text) }
                    Text(
                        "$offset min",
                        color = Palette.Text,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    OutlinedButton(
                        onClick = { offset = (offset + 5).coerceAtMost(45) },
                        shape = RoundedCornerShape(8.dp),
                    ) { Text("+", color = Palette.Text) }
                }
            }
            Button(
                onClick = { onSaveSettings(offset, city, country) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Palette.Gold),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Enregistrer les réglages", color = Color(0xFF1A1305), fontWeight = FontWeight.Bold)
            }
        }

        SectionCard {
            Text("À propos", fontWeight = FontWeight.Bold, color = Palette.Text, fontSize = 14.sp)
            InfoRow("Application", "Badr Quran Studio 1.0")
            InfoRow("Pipeline", "quran_reels_v2 · 5 Reels / jour")
            InfoRow("Publication", "TryPost → Facebook")
            InfoRow("Prochain verset", "${state.state?.surah ?: 1}:${state.state?.ayah ?: 1}")
        }

        Spacer(Modifier.height(20.dp))
    }
}
