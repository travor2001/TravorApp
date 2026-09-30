package com.example.ui.passenger

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerHomeScreen(
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val passenger by AppRepository.currentPassenger.collectAsState()
    val drivers by AppRepository.drivers.collectAsState()
    val activeTrip by AppRepository.activeTrip.collectAsState()
    val driverProgress by AppRepository.driverProgress.collectAsState()

    // Screen States
    var isDestinationPickerOpen by remember { mutableStateOf(false) }
    var selectedOrigin by remember { mutableStateOf(AppRepository.luandaLocations[0]) }
    var selectedDestination by remember { mutableStateOf(AppRepository.luandaLocations[2]) }
    var selectedCategory by remember { mutableStateOf(RideCategory.ECONOMICA) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.MULTICAIXA_EXPRESS) }

    // Dialog toggles
    var showChatDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Check if trip completed to show rating
    LaunchedEffect(activeTrip?.status) {
        if (activeTrip?.status == RideStatus.COMPLETED && activeTrip?.passengerRating == null) {
            showRatingDialog = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Full Screen Interactive Map
        LuandaMapView(
            modifier = Modifier.fillMaxSize(),
            drivers = drivers,
            activeTrip = activeTrip,
            driverProgress = driverProgress,
            selectedOrigin = selectedOrigin,
            selectedDestination = if (isDestinationPickerOpen || activeTrip != null) selectedDestination else null
        )

        // 2. Top Header Bar (Brand, Wallet, Role Switcher, History)
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
                // Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MotoGoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = "MOTO GO",
                            tint = OnMotoGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOTO GO",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MotoGoldPrimary
                            ) {
                                Text(
                                    text = "LUANDA",
                                    color = OnMotoGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Chegue rápido. Vá seguro.",
                            color = DarkTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Right Actions: Wallet Chip + History + Role Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // History Icon
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Histórico",
                            tint = DarkTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Role Switcher Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { onSwitchRole(UserRole.DRIVER) }
                            .testTag("role_switcher_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = MotoGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Motorista",
                                color = MotoGoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. Floating SOS Button (Quick access in passenger mode)
        FloatingActionButton(
            onClick = { showSosDialog = true },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(46.dp)
                .testTag("passenger_sos_fab"),
            containerColor = StatusRed,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Emergência SOS",
                modifier = Modifier.size(22.dp)
            )
        }

        // 4. Bottom Bottom-Sheet Panels (Depending on Ride State)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(14.dp)
        ) {
            when {
                // Trip Active: Searching Driver
                activeTrip?.status == RideStatus.SEARCHING_DRIVER -> {
                    SearchingDriverCard(
                        trip = activeTrip!!,
                        onCancel = { AppRepository.cancelActiveTrip() }
                    )
                }

                // Trip Active: Driver Assigned or Arrived
                activeTrip?.status == RideStatus.DRIVER_ASSIGNED || activeTrip?.status == RideStatus.DRIVER_ARRIVED -> {
                    DriverAssignedCard(
                        trip = activeTrip!!,
                        onOpenChat = { showChatDialog = true },
                        onOpenSos = { showSosDialog = true },
                        onCancel = { AppRepository.cancelActiveTrip() }
                    )
                }

                // Trip Active: In Progress
                activeTrip?.status == RideStatus.IN_PROGRESS -> {
                    TripInProgressCard(
                        trip = activeTrip!!,
                        progress = driverProgress,
                        onOpenChat = { showChatDialog = true },
                        onOpenSos = { showSosDialog = true }
                    )
                }

                // Destination Picker / Fare Estimation Mode
                isDestinationPickerOpen -> {
                    DestinationPickerCard(
                        origin = selectedOrigin,
                        destination = selectedDestination,
                        category = selectedCategory,
                        paymentMethod = selectedPayment,
                        onOriginChange = { selectedOrigin = it },
                        onDestinationChange = { selectedDestination = it },
                        onCategoryChange = { selectedCategory = it },
                        onPaymentChange = { selectedPayment = it },
                        onRequestRide = {
                            AppRepository.requestRide(
                                origin = selectedOrigin,
                                destination = selectedDestination,
                                category = selectedCategory,
                                paymentMethod = selectedPayment
                            )
                            isDestinationPickerOpen = false
                        },
                        onClose = { isDestinationPickerOpen = false }
                    )
                }

                // Default Idle Mode: "Para onde vamos?" Search bar
                else -> {
                    IdleWhereToCard(
                        onOpenPicker = { isDestinationPickerOpen = true },
                        onSelectQuickLocation = { loc ->
                            selectedDestination = loc
                            isDestinationPickerOpen = true
                        }
                    )
                }
            }
        }

        // Dialogs
        if (showRatingDialog && activeTrip != null) {
            RatingReviewDialog(
                trip = activeTrip!!,
                onDismiss = { showRatingDialog = false },
                onSubmit = { stars, tags, comment ->
                    AppRepository.submitRating(activeTrip!!.id, stars, tags, comment)
                    showRatingDialog = false
                }
            )
        }

        if (showSosDialog) {
            SosSafetyDialog(
                trip = activeTrip,
                onDismiss = { showSosDialog = false }
            )
        }

        if (showChatDialog && activeTrip != null) {
            ChatAndCallDialog(
                trip = activeTrip!!,
                isPassenger = true,
                onDismiss = { showChatDialog = false }
            )
        }

        if (showHistoryDialog) {
            PassengerHistoryDialog(
                onDismiss = { showHistoryDialog = false }
            )
        }
    }
}

@Composable
private fun IdleWhereToCard(
    onOpenPicker: () -> Unit,
    onSelectQuickLocation: (LuandaLocation) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("idle_where_to_card"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // "Para onde vamos?" Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenPicker() }
                    .testTag("where_to_input_button"),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkOutlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MotoGoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Para onde vamos?",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Escolha o seu destino em Luanda",
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = DarkTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick popular places in Luanda
            Text(
                text = "Destinos Populares em Luanda",
                color = DarkTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AppRepository.luandaLocations.take(5)) { loc ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier.clickable { onSelectQuickLocation(loc) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MotoGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = loc.name,
                                    color = DarkTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = loc.district,
                                    color = DarkTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationPickerCard(
    origin: LuandaLocation,
    destination: LuandaLocation,
    category: RideCategory,
    paymentMethod: PaymentMethod,
    onOriginChange: (LuandaLocation) -> Unit,
    onDestinationChange: (LuandaLocation) -> Unit,
    onCategoryChange: (RideCategory) -> Unit,
    onPaymentChange: (PaymentMethod) -> Unit,
    onRequestRide: () -> Unit,
    onClose: () -> Unit
) {
    val (dist, time, fare) = AppRepository.calculateEstimate(origin, destination, category)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("destination_picker_card"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Confirmar Solicitação",
                    color = DarkTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = DarkTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Origin & Destination Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Origin Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("De onde? (Ponto de recolha)", color = DarkTextMuted, fontSize = 10.sp)
                            Text(origin.name, color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 22.dp, top = 8.dp, bottom = 8.dp),
                        color = DarkOutline
                    )

                    // Destination Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(StatusRed)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Para onde? (Destino)", color = DarkTextMuted, fontSize = 10.sp)
                            Text(destination.name, color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trip Estimate Metrics Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Distância", color = DarkTextMuted, fontSize = 10.sp)
                        Text("$dist km", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Tempo Est.", color = DarkTextMuted, fontSize = 10.sp)
                        Text("~$time min", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MotoGoldContainer,
                    border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Preço Est.", color = MotoGoldPrimary, fontSize = 10.sp)
                        Text(AppRepository.formatKz(fare), color = MotoGoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Categories
            Text("Categoria de Moto", color = DarkTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RideCategory.values().forEach { cat ->
                    val isSelected = cat == category
                    val (_, _, catFare) = AppRepository.calculateEstimate(origin, destination, cat)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onCategoryChange(cat) }
                            .testTag("cat_${cat.name}"),
                        color = if (isSelected) MotoGoldContainer else DarkSurfaceVariant,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) MotoGoldPrimary else DarkOutline)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (cat) {
                                    RideCategory.ECONOMICA -> Icons.Default.TwoWheeler
                                    RideCategory.CONFORTO -> Icons.Default.Security
                                    RideCategory.ENTREGA -> Icons.Default.LocalShipping
                                },
                                contentDescription = null,
                                tint = if (isSelected) MotoGoldPrimary else DarkTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.title.replace("Moto ", ""),
                                color = if (isSelected) MotoGoldPrimary else DarkTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = AppRepository.formatKz(catFare),
                                color = if (isSelected) DarkTextPrimary else DarkTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment method selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Método de Pagamento", color = DarkTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = method == paymentMethod
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPaymentChange(method) },
                        color = if (isSelected) DarkSurfaceVariant else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) MotoGoldPrimary else DarkOutline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = when (method) {
                                    PaymentMethod.CASH -> Icons.Default.Payments
                                    PaymentMethod.MULTICAIXA_EXPRESS -> Icons.Default.CreditCard
                                    PaymentMethod.WALLET -> Icons.Default.AccountBalanceWallet
                                },
                                contentDescription = null,
                                tint = if (isSelected) MotoGoldPrimary else DarkTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = method.label.replace(" físico", "").replace("Digital ", ""),
                                color = if (isSelected) DarkTextPrimary else DarkTextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big CTA: SOLICITAR MOTO-TÁXI
            Button(
                onClick = onRequestRide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("request_moto_taxi_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoGoldPrimary,
                    contentColor = OnMotoGold
                )
            ) {
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOLICITAR MOTO-TÁXI • ${AppRepository.formatKz(fare)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SearchingDriverCard(
    trip: Trip,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("searching_driver_card"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MotoGoldPrimary,
                trackColor = DarkSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "A procurar moto-taxista mais próximo...",
                color = DarkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contactando motociclistas disponíveis em Luanda",
                color = DarkTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(trip.category.title, color = MotoGoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(AppRepository.formatKz(trip.estimatedFareKz), color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cancel_search_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Text("CANCELAR SOLICITAÇÃO", color = StatusRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DriverAssignedCard(
    trip: Trip,
    onOpenChat: () -> Unit,
    onOpenSos: () -> Unit,
    onCancel: () -> Unit
) {
    val isArrived = trip.status == RideStatus.DRIVER_ARRIVED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("driver_assigned_card"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, if (isArrived) StatusGreen else DarkOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Status Tag
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isArrived) StatusGreen.copy(alpha = 0.2f) else MotoGoldContainer,
                border = BorderStroke(1.dp, if (isArrived) StatusGreen else MotoGoldPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isArrived) StatusGreen else MotoGoldPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArrived) "O MOTO-TAXISTA CHEGOU!" else "MOTORISTA A CAMINHO • ~3 MIN",
                        color = if (isArrived) StatusGreen else MotoGoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Driver Profile Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MotoGoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = trip.driverName,
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verificado",
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "⭐ ${trip.driverRating} (1.245 viagens)",
                        color = DarkTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${trip.driverMoto} • ${trip.origin.district}",
                        color = DarkTextMuted,
                        fontSize = 11.sp
                    )
                }

                // Plate Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(
                        text = trip.driverPlate,
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Digit Security Code Banner
            SecurityCodeBanner(tripCode = trip.tripCode)

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Chat, Call, SOS, Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_chat_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = DarkTextPrimary
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_driver_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = DarkTextPrimary
                    )
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ligar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onOpenSos,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusRed.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "SOS", tint = StatusRed)
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = DarkTextMuted)
                }
            }
        }
    }
}

@Composable
private fun TripInProgressCard(
    trip: Trip,
    progress: Float,
    onOpenChat: () -> Unit,
    onOpenSos: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_in_progress_card"),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, MotoGoldPrimary.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(StatusBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIAGEM EM CURSO",
                        color = StatusBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}% percorrido",
                    color = DarkTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            Text(
                text = "Destino: ${trip.destination.name}",
                color = DarkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Condutor: ${trip.driverName} • ${trip.driverPlate}",
                color = DarkTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = DarkTextPrimary
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat / Suporte", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenSos,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BOTÃO SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PassengerHistoryDialog(onDismiss: () -> Unit) {
    val history by AppRepository.tripHistory.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("FECHAR", color = MotoGoldPrimary)
            }
        },
        title = {
            Text(
                text = "Histórico de Viagens",
                color = DarkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history) { trip ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkOutline)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = trip.destination.name,
                                    color = DarkTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = AppRepository.formatKz(trip.finalFareKz),
                                    color = MotoGoldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "De: ${trip.origin.name}",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Motorista: ${trip.driverName} (${trip.driverMoto})",
                                color = DarkTextMuted,
                                fontSize = 11.sp
                            )
                            if (trip.passengerRating != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Avaliação: ", color = DarkTextMuted, fontSize = 10.sp)
                                    (1..(trip.passengerRating ?: 5)).forEach {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = MotoGoldPrimary, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkSurfaceElevated
    )
}
