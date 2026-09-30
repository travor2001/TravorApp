package com.example.ui.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.LuandaMapView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Map, 1: Drivers, 2: Pricing, 3: Complaints
    val tabs = listOf("Visão Geral & Mapa", "Motoristas", "Preços", "Reclamações")

    val drivers by AppRepository.drivers.collectAsState()
    val tripHistory by AppRepository.tripHistory.collectAsState()
    val activeTrip by AppRepository.activeTrip.collectAsState()
    val pricingConfig by AppRepository.pricingConfig.collectAsState()
    val complaints by AppRepository.complaints.collectAsState()

    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MotoGoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OnMotoGold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("MOTO GO ADMIN", color = DarkTextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Gestão Operacional de Luanda", color = DarkTextSecondary, fontSize = 11.sp)
                            }
                        }

                        // Switch back to Passenger
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable { onSwitchRole(UserRole.PASSENGER) }
                                .testTag("admin_exit_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sair Admin", color = MotoGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Bar
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurfaceElevated,
                        contentColor = MotoGoldPrimary,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MotoGoldPrimary
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (selectedTab == index) MotoGoldPrimary else DarkTextSecondary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AdminOverviewAndMapTab(drivers, activeTrip, tripHistory)
                1 -> AdminDriversTab(drivers, onToggleSuspend = { AppRepository.toggleDriverSuspension(it) })
                2 -> AdminPricingTab(pricingConfig, onSave = {
                    AppRepository.updatePricingConfig(it)
                    Toast.makeText(context, "Tarifas atualizadas no sistema!", Toast.LENGTH_SHORT).show()
                })
                3 -> AdminComplaintsTab(complaints)
            }
        }
    }
}

@Composable
fun AdminOverviewAndMapTab(
    drivers: List<Driver>,
    activeTrip: Trip?,
    history: List<Trip>
) {
    val totalRevenue = history.sumOf { it.finalFareKz } + (activeTrip?.finalFareKz ?: 0.0)
    val totalCommissions = totalRevenue * 0.15
    val onlineCount = drivers.count { it.isOnline && !it.isSuspended }
    val offlineCount = drivers.count { !it.isOnline }

    Column(modifier = Modifier.fillMaxSize()) {
        // KPI Metrics Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Receita Total", color = DarkTextMuted, fontSize = 10.sp)
                    Text(AppRepository.formatKz(totalRevenue), color = MotoGoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Comissões (15%)", color = DarkTextMuted, fontSize = 10.sp)
                    Text(AppRepository.formatKz(totalCommissions), color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Viagens", color = DarkTextMuted, fontSize = 10.sp)
                    Text("${history.size + (if (activeTrip != null) 1 else 0)}", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Fleet Status Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Online: $onlineCount", color = DarkTextSecondary, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusBlue))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Em Viagem: ${if (activeTrip != null) 1 else 0}", color = DarkTextSecondary, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Offline: $offlineCount", color = DarkTextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Map takes remaining space
        Box(modifier = Modifier.fillMaxSize()) {
            LuandaMapView(
                modifier = Modifier.fillMaxSize(),
                drivers = drivers,
                activeTrip = activeTrip
            )
        }
    }
}

@Composable
fun AdminDriversTab(
    drivers: List<Driver>,
    onToggleSuspend: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(drivers) { driver ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, if (driver.isSuspended) StatusRed else DarkOutline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (driver.isOnline) StatusGreen.copy(alpha = 0.2f) else DarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = if (driver.isOnline) StatusGreen else DarkTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(driver.name, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (driver.isVerified) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text("${driver.phone} • ${driver.motorcycleModel}", color = DarkTextSecondary, fontSize = 11.sp)
                            }
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                driver.isSuspended -> StatusRed.copy(alpha = 0.2f)
                                driver.isOnline -> StatusGreen.copy(alpha = 0.2f)
                                else -> DarkSurfaceVariant
                            }
                        ) {
                            Text(
                                text = when {
                                    driver.isSuspended -> "SUSPENSO"
                                    driver.isOnline -> "ONLINE"
                                    else -> "OFFLINE"
                                },
                                color = when {
                                    driver.isSuspended -> StatusRed
                                    driver.isOnline -> StatusGreen
                                    else -> DarkTextMuted
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Matrícula: ${driver.plateNumber}", color = DarkTextSecondary, fontSize = 11.sp)
                        Text("⭐ ${driver.rating} (${driver.totalRides} viagens)", color = MotoGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onToggleSuspend(driver.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (driver.isSuspended) StatusGreen else StatusRed)
                        ) {
                            Text(
                                text = if (driver.isSuspended) "REATIVAR" else "SUSPENDER",
                                color = if (driver.isSuspended) StatusGreen else StatusRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = DarkTextPrimary)
                        ) {
                            Text("DOCUMENTOS", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPricingTab(
    currentConfig: PricingConfig,
    onSave: (PricingConfig) -> Unit
) {
    var baseFare by remember { mutableStateOf(currentConfig.baseFareKz.toString()) }
    var pricePerKm by remember { mutableStateOf(currentConfig.pricePerKmKz.toString()) }
    var pricePerMin by remember { mutableStateOf(currentConfig.pricePerMinuteKz.toString()) }
    var commission by remember { mutableStateOf(currentConfig.platformCommissionPercent.toString()) }
    var surge by remember { mutableFloatStateOf(currentConfig.surgeMultiplier.toFloat()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Configuração de Tarifas em Luanda", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Altere os parâmetros do algoritmo de preços em tempo real.", color = DarkTextSecondary, fontSize = 12.sp)
        }

        item {
            OutlinedTextField(
                value = baseFare,
                onValueChange = { baseFare = it },
                label = { Text("Tarifa Base (Kz)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = pricePerKm,
                onValueChange = { pricePerKm = it },
                label = { Text("Preço por Quilómetro (Kz/km)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = pricePerMin,
                onValueChange = { pricePerMin = it },
                label = { Text("Preço por Minuto (Kz/min)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = commission,
                onValueChange = { commission = it },
                label = { Text("Comissão da Plataforma (%)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Column {
                Text("Multiplicador de Alta Procura (Surge): ${String.format("%.1fx", surge)}", color = DarkTextPrimary, fontSize = 13.sp)
                Slider(
                    value = surge,
                    onValueChange = { surge = it },
                    valueRange = 1.0f..2.5f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = MotoGoldPrimary, activeTrackColor = MotoGoldPrimary)
                )
            }
        }

        item {
            Button(
                onClick = {
                    val updated = PricingConfig(
                        baseFareKz = baseFare.toDoubleOrNull() ?: 400.0,
                        pricePerKmKz = pricePerKm.toDoubleOrNull() ?: 160.0,
                        pricePerMinuteKz = pricePerMin.toDoubleOrNull() ?: 30.0,
                        platformCommissionPercent = commission.toDoubleOrNull() ?: 15.0,
                        surgeMultiplier = surge.toDouble()
                    )
                    onSave(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_pricing_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SALVAR TARIFAS MOTO GO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminComplaintsTab(complaints: List<Complaint>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Reclamações & Ouvidoria", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Atendimento ao passageiro e condutor", color = DarkTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(complaints) { complaint ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, if (complaint.isResolved) DarkOutline else StatusOrange),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("De: ${complaint.authorName}", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = if (complaint.isResolved) "RESOLVIDO" else "PENDENTE",
                            color = if (complaint.isResolved) StatusGreen else StatusOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("Alvo: ${complaint.targetName} • ${complaint.date}", color = DarkTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(complaint.reason, color = DarkTextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}
