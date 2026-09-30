package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppRepository
import com.example.model.Trip
import com.example.ui.theme.*

@Composable
fun SecurityCodeBanner(
    tripCode: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("security_code_banner"),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceVariant,
        border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MotoGoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Código de Viagem",
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Diga este código ao moto-taxista antes de subir",
                    color = DarkTextSecondary,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MotoGoldPrimary,
                modifier = Modifier.testTag("trip_code_box")
            ) {
                Text(
                    text = tripCode,
                    color = OnMotoGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RatingReviewDialog(
    trip: Trip,
    onDismiss: () -> Unit,
    onSubmit: (Int, List<String>, String) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(5) }
    val tagsList = listOf("Segurança", "Pontualidade", "Educação", "Condução", "Estado da moto")
    val selectedTags = remember { mutableStateListOf<String>("Segurança", "Pontualidade") }
    var commentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rating_review_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MotoGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MotoGoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Como foi a sua viagem?",
                    color = DarkTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Com ${trip.driverName} • ${trip.driverMoto}",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        IconButton(
                            onClick = { selectedStars = starIndex },
                            modifier = Modifier.testTag("star_$starIndex")
                        ) {
                            Icon(
                                imageVector = if (starIndex <= selectedStars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$starIndex Estrelas",
                                tint = if (starIndex <= selectedStars) MotoGoldPrimary else DarkTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "O que correu melhor?",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tagsList.take(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MotoGoldPrimary,
                                selectedLabelColor = OnMotoGold,
                                containerColor = DarkSurfaceVariant,
                                labelColor = DarkTextSecondary
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tagsList.drop(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MotoGoldPrimary,
                                selectedLabelColor = OnMotoGold,
                                containerColor = DarkSurfaceVariant,
                                labelColor = DarkTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Comment input
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Deixe um comentário opcional...", color = DarkTextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rating_comment_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MotoGoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        onSubmit(selectedStars, selectedTags.toList(), commentText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_rating_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoGoldPrimary,
                        contentColor = OnMotoGold
                    )
                ) {
                    Text("AVALIAR EXPERIÊNCIA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SosSafetyDialog(
    trip: Trip?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sos_safety_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(StatusRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Central de Segurança SOS",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Apoio e emergência em Luanda",
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: Call Emergency 111 (Polícia Nacional de Angola)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:111")
                            }
                            context.startActivity(intent)
                        },
                    color = StatusRed.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, StatusRed)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalPolice, contentDescription = null, tint = StatusRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ligar 111 (Emergência Nacional)", color = StatusRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Polícia Nacional e Serviços de Socorro", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.Phone, contentDescription = null, tint = StatusRed)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Share Live Trip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Estou numa viagem Moto Go em Luanda com o motorista ${trip?.driverName ?: "João"} (${trip?.driverPlate ?: "LD"}). Acompanha a minha rota segura: https://motogo.ao/live/${trip?.id ?: "1234"}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partilhar viagem segura"))
                        },
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, DarkOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShareLocation, contentDescription = null, tint = MotoGoldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Partilhar Viagem em Direto", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Envie a localização a familiares por WhatsApp/SMS", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 3: Moto Go Support Angola
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            Toast.makeText(context, "A contactar apoio ao cliente Moto Go Luanda...", Toast.LENGTH_SHORT).show()
                        },
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, DarkOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = StatusGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Apoio ao Cliente Moto Go", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Suporte 24/7 para Luanda e Angola", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DarkOutline)
                ) {
                    Text("FECHAR", color = DarkTextPrimary, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ChatAndCallDialog(
    trip: Trip,
    isPassenger: Boolean,
    onDismiss: () -> Unit
) {
    val messages by AppRepository.chatMessages.collectAsState()
    var inputMsg by remember { mutableStateOf("") }
    var isCallActive by remember { mutableStateOf(false) }

    val quickReplies = if (isPassenger) {
        listOf("Estou no ponto indicado.", "Estou a atravessar a rua.", "Pode aguardar 2 minutos?", "De camisola preta.")
    } else {
        listOf("Estou chegando.", "Estou no ponto indicado.", "Já cheguei com capacete.", "Trânsito ligeiro.")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("chat_call_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Call button & Masked Phone info
                Surface(
                    color = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MotoGoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPassenger) Icons.Default.TwoWheeler else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = OnMotoGold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPassenger) trip.driverName else trip.passengerName,
                                    color = DarkTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "🔒 Número Protegido (Privacidade)",
                                    color = DarkTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row {
                            IconButton(
                                onClick = { isCallActive = !isCallActive },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isCallActive) StatusRed else StatusGreen)
                            ) {
                                Icon(
                                    imageVector = if (isCallActive) Icons.Default.CallEnd else Icons.Default.Phone,
                                    contentDescription = "Chamada",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = DarkTextSecondary)
                            }
                        }
                    }
                }

                // If Call is active banner
                if (isCallActive) {
                    Surface(
                        color = StatusGreen.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Chamada encriptada em curso • 00:18",
                                color = StatusGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = (isPassenger && msg.isFromPassenger) || (!isPassenger && !msg.isFromPassenger)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMe) 14.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 14.dp
                                ),
                                color = if (isMe) MotoGoldPrimary else DarkSurfaceVariant,
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.text,
                                        color = if (isMe) OnMotoGold else DarkTextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.time,
                                        color = if (isMe) OnMotoGold.copy(alpha = 0.7f) else DarkTextMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick replies bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickReplies) { reply ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.clickable {
                                AppRepository.sendChatMessage(reply, isPassenger)
                            }
                        ) {
                            Text(
                                text = reply,
                                color = DarkTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Input bar
                Surface(
                    color = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMsg,
                            onValueChange = { inputMsg = it },
                            placeholder = { Text("Escreva uma mensagem...", color = DarkTextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MotoGoldPrimary,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary
                            ),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputMsg.isNotBlank()) {
                                    AppRepository.sendChatMessage(inputMsg, isPassenger)
                                    inputMsg = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MotoGoldPrimary)
                                .testTag("chat_send_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = OnMotoGold)
                        }
                    }
                }
            }
        }
    }
}
