package com.example.ui.driver

import android.widget.Toast
import androidx.compose.animation.*
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
import com.example.model.*
import com.example.ui.components.ChatAndCallDialog
import com.example.ui.components.LuandaMapView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHomeScreen(
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val drivers by AppRepository.drivers.collectAsState()
    val activeTrip by AppRepository.activeTrip.collectAsState()
    val driverProgress by AppRepository.driverProgress.collectAsState()
    val incomingRequest by AppRepository.incomingDriverRequest.collectAsState()
    val documents by AppRepository.driverDocuments.collectAsState()

    // Primary driver profile (João Manuel)
    val driver = drivers.firstOrNull { it.id == "drv_1" } ?: drivers.first()

    // Dialog toggles
    var showWalletDialog by remember { mutableStateOf(false) }
    var showDocsDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showCodeInputDialog by remember { mutableStateOf(false) }
    var showRatePassengerDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Map View in Driver Mode
        LuandaMapView(
            modifier = Modifier.fillMaxSize(),
            drivers = drivers,
            activeTrip = activeTrip,
            driverProgress = driverProgress,
            selectedOrigin = activeTrip?.origin,
            selectedDestination = activeTrip?.destination
        )

        // 2. Top Driver Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Driver info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MotoGoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = OnMotoGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = driver.name,
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = "${driver.motorcycleModel} • ${driver.plateNumber}",
                            color = DarkTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Actions: Documents, Wallet, Role switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showDocsDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("driver_docs_button")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Documentos", tint = DarkTextSecondary)
                    }

                    IconButton(
                        onClick = { showWalletDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("driver_wallet_button")
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Carteira", tint = MotoGoldPrimary)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { onSwitchRole(UserRole.PASSENGER) }
                            .testTag("switch_to_passenger_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Passageiro", color = MotoGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Driver Stats Floating Bar
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceElevated.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ganhos Hoje", color = DarkTextMuted, fontSize = 10.sp)
                    Text(AppRepository.formatKz(driver.todayEarningsKz), color = MotoGoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkOutline))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Viagens", color = DarkTextMuted, fontSize = 10.sp)
                    Text("${driver.totalRides}", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkOutline))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Avaliação", color = DarkTextMuted, fontSize = 10.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${driver.rating}", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 4. Bottom Control Panel: ONLINE/OFFLINE Toggle OR Active Trip Workflow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(14.dp)
        ) {
            when {
                // Trip Active in Driver View:
                activeTrip != null && activeTrip?.driverId == driver.id -> {
                    DriverActiveTripControl(
                        trip = activeTrip!!,
                        progress = driverProgress,
                        onOpenChat = { showChatDialog = true },
                        onInputCode = { showCodeInputDialog = true },
                        onFinishTrip = {
                            AppRepository.completeRide()
                            showRatePassengerDialog = true
                        }
                    )
                }

                // Default: Online/Offline Toggle
                else -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (driver.isOnline) StatusGreen else DarkOutline)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (driver.isOnline) StatusGreen else StatusRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (driver.isOnline) "VOCÊ ESTÁ ONLINE E A RECEBER PEDIDOS" else "VOCÊ ESTÁ OFFLINE",
                                    color = if (driver.isOnline) StatusGreen else DarkTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Large ONLINE/OFFLINE Button
                            Button(
                                onClick = { AppRepository.toggleDriverOnline(driver.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("toggle_online_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (driver.isOnline) DarkSurfaceVariant else MotoGoldPrimary,
                                    contentColor = if (driver.isOnline) StatusRed else OnMotoGold
                                ),
                                border = if (driver.isOnline) BorderStroke(1.5.dp, StatusRed) else null
                            ) {
                                Icon(
                                    imageVector = if (driver.isOnline) Icons.Default.PowerSettingsNew else Icons.Default.FlashOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (driver.isOnline) "FICAR OFFLINE" else "FICAR ONLINE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Incoming Ride Request Alert (When a passenger requests a ride)
        if (incomingRequest != null && driver.isOnline) {
            IncomingRequestDialog(
                trip = incomingRequest!!,
                onAccept = { AppRepository.driverAcceptsRide(incomingRequest!!.id) },
                onReject = { AppRepository.driverRejectsRide(incomingRequest!!.id) }
            )
        }

        // Dialogs: Wallet
        if (showWalletDialog) {
            DriverWalletDialog(
                driver = driver,
                onDismiss = { showWalletDialog = false },
                onWithdraw = { amount ->
                    val success = AppRepository.requestDriverWithdrawal(amount)
                    if (success) {
                        Toast.makeText(context, "Levantamento de ${AppRepository.formatKz(amount)} enviado para Multicaixa Express!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Saldo insuficiente!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Dialogs: Documents Verification
        if (showDocsDialog) {
            DriverDocumentsDialog(
                documents = documents,
                onDismiss = { showDocsDialog = false }
            )
        }

        // Dialogs: Chat with passenger
        if (showChatDialog && activeTrip != null) {
            ChatAndCallDialog(
                trip = activeTrip!!,
                isPassenger = false,
                onDismiss = { showChatDialog = false }
            )
        }

        // Dialogs: Code input dialog to unlock trip
        if (showCodeInputDialog && activeTrip != null) {
            EnterTripCodeDialog(
                expectedCode = activeTrip!!.tripCode,
                onDismiss = { showCodeInputDialog = false },
                onCodeConfirmed = { code ->
                    val ok = AppRepository.verifyCodeAndStartRide(code)
                    if (ok) {
                        showCodeInputDialog = false
                        Toast.makeText(context, "Código confirmado! Viagem iniciada.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Código incorreto! Peça o código ao passageiro.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Dialogs: Rate Passenger
        if (showRatePassengerDialog) {
            AlertDialog(
                onDismissRequest = { showRatePassengerDialog = false },
                title = { Text("Avaliar Passageiro", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Como foi a viagem com Carlos Baptista?", color = DarkTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            (1..5).forEach {
                                Icon(Icons.Default.Star, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRatePassengerDialog = false
                            Toast.makeText(context, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                    ) {
                        Text("ENVIAR", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }
    }
}

@Composable
private fun DriverActiveTripControl(
    trip: Trip,
    progress: Float,
    onOpenChat: () -> Unit,
    onInputCode: () -> Unit,
    onFinishTrip: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("driver_active_control"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (trip.status) {
                        RideStatus.DRIVER_ASSIGNED -> "A CAMINHO DO PASSAGEIRO"
                        RideStatus.DRIVER_ARRIVED -> "AGUARDANDO EMBARQUE"
                        RideStatus.IN_PROGRESS -> "EM ROTA PARA DESTINO"
                        else -> "VIAGEM"
                    },
                    color = when (trip.status) {
                        RideStatus.DRIVER_ARRIVED -> StatusGreen
                        RideStatus.IN_PROGRESS -> StatusBlue
                        else -> MotoGoldPrimary
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )

                Text(
                    text = AppRepository.formatKz(trip.finalFareKz),
                    color = MotoGoldPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Passenger detail
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MotoGoldPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(trip.passengerName, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Ponto: ${trip.origin.name}", color = DarkTextSecondary, fontSize = 11.sp)
                    Text("Destino: ${trip.destination.name}", color = DarkTextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action based on status
            when (trip.status) {
                RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenChat,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = DarkTextPrimary)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Contactar", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onInputCode,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("validate_trip_code_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("INSERIR CÓDIGO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                RideStatus.IN_PROGRESS -> {
                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MotoGoldPrimary,
                            trackColor = DarkSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onFinishTrip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("finish_trip_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("FINALIZAR VIAGEM E COBRAR", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun IncomingRequestDialog(
    trip: Trip,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("incoming_request_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(2.dp, MotoGoldPrimary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header alert
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MotoGoldContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NOVA VIAGEM DISPONÍVEL!", color = MotoGoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = AppRepository.formatKz(trip.finalFareKz),
                    color = DarkTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = trip.category.title,
                    color = MotoGoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Passageiro: ${trip.passengerName}", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ponto: ${trip.origin.name} (~850m)", color = DarkTextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Destino: ${trip.destination.name} (${trip.distanceKm} km)", color = DarkTextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("driver_reject_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StatusRed)
                    ) {
                        Text("RECUSAR", color = StatusRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("driver_accept_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                    ) {
                        Text("ACEITAR", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EnterTripCodeDialog(
    expectedCode: String,
    onDismiss: () -> Unit,
    onCodeConfirmed: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("enter_trip_code_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Código de Segurança", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = "Peça o código de 4 dígitos ao passageiro para iniciar a viagem.",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { if (it.length <= 4) codeInput = it },
                    placeholder = { Text("Ex: $expectedCode", color = DarkTextMuted) },
                    modifier = Modifier.testTag("code_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MotoGoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("CANCELAR", color = DarkTextSecondary)
                    }
                    Button(
                        onClick = { onCodeConfirmed(codeInput) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_code_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                    ) {
                        Text("INICIAR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverWalletDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    onWithdraw: (Double) -> Unit
) {
    var withdrawInput by remember { mutableStateOf("10000") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("driver_wallet_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minha Carteira", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = DarkTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Available Balance Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MotoGoldContainer,
                    border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Saldo Disponível", color = MotoGoldPrimary, fontSize = 11.sp)
                        Text(AppRepository.formatKz(driver.walletBalanceKz), color = MotoGoldPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pronto para levantamento Multicaixa Express", color = DarkTextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Period Earnings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Hoje", color = DarkTextMuted, fontSize = 10.sp)
                            Text(AppRepository.formatKz(driver.todayEarningsKz), color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Semana", color = DarkTextMuted, fontSize = 10.sp)
                            Text(AppRepository.formatKz(driver.weekEarningsKz), color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Mês", color = DarkTextMuted, fontSize = 10.sp)
                            Text(AppRepository.formatKz(driver.monthEarningsKz), color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Withdrawal Form
                Text("Solicitar Levantamento", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = withdrawInput,
                    onValueChange = { withdrawInput = it },
                    label = { Text("Valor a levantar (Kz)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MotoGoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val amount = withdrawInput.toDoubleOrNull() ?: 0.0
                        onWithdraw(amount)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("withdraw_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LEVANTAR VIA MULTICAIXA EXPRESS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Commissions info
                Text(
                    text = "Comissão da plataforma: 15% por viagem finalizada. Pagamentos transferidos diretamente para o seu IBAN ou Multicaixa Express.",
                    color = DarkTextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun DriverDocumentsDialog(
    documents: List<DriverDocument>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("FECHAR", color = MotoGoldPrimary)
            }
        },
        title = { Text("Documentos & Verificação", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documents) { doc ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.title, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(doc.description, color = DarkTextSecondary, fontSize = 10.sp)
                                Text("Arquivo: ${doc.fileName}", color = DarkTextMuted, fontSize = 9.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "VERIFICADO",
                                    color = StatusGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkSurfaceElevated
    )
}
