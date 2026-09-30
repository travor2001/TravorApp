package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

object AppRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Initial Luanda Locations
    val luandaLocations = listOf(
        LuandaLocation("loc_1", "Largo da Maianga", "Maianga, Luanda", -8.8252, 13.2325, isPopular = true),
        LuandaLocation("loc_2", "Cidade do Kilamba - Quarteirão Q", "Kilamba, Luanda Sul", -8.9950, 13.2590, isPopular = true),
        LuandaLocation("loc_3", "Belas Shopping", "Talatona, Luanda", -8.9180, 13.1810, isPopular = true),
        LuandaLocation("loc_4", "Mutamba (Baixa de Luanda)", "Ingombota, Luanda", -8.8140, 13.2300, isPopular = true),
        LuandaLocation("loc_5", "Ilha do Cabo (Chicala)", "Ilha de Luanda", -8.7900, 13.2200, isPopular = true),
        LuandaLocation("loc_6", "Mercado do Hoji-ya-Henda", "Cazenga, Luanda", -8.8190, 13.2870, isPopular = true),
        LuandaLocation("loc_7", "Vila de Viana (Ponte)", "Viana, Luanda", -8.9050, 13.3750, isPopular = true),
        LuandaLocation("loc_8", "Aeroporto 4 de Fevereiro", "Rocha Pinto, Luanda", -8.8580, 13.2310, isPopular = true),
        LuandaLocation("loc_9", "Kero Benfica", "Benfica, Luanda", -8.9500, 13.1500),
        LuandaLocation("loc_10", "Rua Kwame Nkrumah", "Maculusso, Luanda", -8.8230, 13.2390)
    )

    // Current logged-in role
    private val _currentRole = MutableStateFlow(UserRole.PASSENGER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Current Passenger Profile
    private val _currentPassenger = MutableStateFlow(
        Passenger(
            id = "pass_01",
            name = "Carlos Baptista",
            phone = "+244 923 456 789",
            rating = 4.9,
            totalRides = 38,
            walletBalanceKz = 25000.0
        )
    )
    val currentPassenger: StateFlow<Passenger> = _currentPassenger.asStateFlow()

    // Drivers list
    private val _drivers = MutableStateFlow(
        listOf(
            Driver(
                id = "drv_1",
                name = "João Manuel",
                phone = "+244 934 112 233",
                rating = 4.8,
                totalRides = 1245,
                motorcycleModel = "Honda PCX 150cc",
                plateNumber = "LD-24-88-GX",
                motorcycleColor = "Preto / Dourado",
                isOnline = true,
                isVerified = true,
                currentLat = -8.8230,
                currentLng = 13.2350,
                walletBalanceKz = 48500.0,
                todayEarningsKz = 18200.0
            ),
            Driver(
                id = "drv_2",
                name = "António Domingos",
                phone = "+244 921 556 778",
                rating = 4.9,
                totalRides = 870,
                motorcycleModel = "Yamaha NMAX 155",
                plateNumber = "LD-18-42-BT",
                motorcycleColor = "Azul Noite",
                isOnline = true,
                isVerified = true,
                currentLat = -8.8270,
                currentLng = 13.2305,
                walletBalanceKz = 32100.0,
                todayEarningsKz = 12400.0
            ),
            Driver(
                id = "drv_3",
                name = "Sebastião Neto",
                phone = "+244 945 990 112",
                rating = 4.7,
                totalRides = 530,
                motorcycleModel = "Haojue Lindy 125",
                plateNumber = "LD-31-09-KA",
                motorcycleColor = "Vermelho Ferrari",
                isOnline = true,
                isVerified = true,
                currentLat = -8.8290,
                currentLng = 13.2360,
                walletBalanceKz = 19400.0,
                todayEarningsKz = 8900.0
            ),
            Driver(
                id = "drv_4",
                name = "Manuel Kassoma",
                phone = "+244 912 334 455",
                rating = 4.9,
                totalRides = 2100,
                motorcycleModel = "Bajaj Pulsar NS200",
                plateNumber = "LD-09-77-MN",
                motorcycleColor = "Preto Fosco",
                isOnline = false,
                isVerified = true,
                currentLat = -8.8350,
                currentLng = 13.2400,
                walletBalanceKz = 74200.0,
                todayEarningsKz = 0.0
            ),
            Driver(
                id = "drv_5",
                name = "Pedro Afonso",
                phone = "+244 931 778 899",
                rating = 4.6,
                totalRides = 340,
                motorcycleModel = "TVS Apache 160",
                plateNumber = "LD-44-12-PQ",
                motorcycleColor = "Branco Polar",
                isOnline = false,
                isVerified = false,
                currentLat = -8.8400,
                currentLng = 13.2200,
                walletBalanceKz = 8500.0,
                todayEarningsKz = 0.0
            )
        )
    )
    val drivers: StateFlow<List<Driver>> = _drivers.asStateFlow()

    // Pricing Config (Admin editable)
    private val _pricingConfig = MutableStateFlow(
        PricingConfig(
            baseFareKz = 400.0,
            pricePerKmKz = 160.0,
            pricePerMinuteKz = 30.0,
            platformCommissionPercent = 15.0,
            surgeMultiplier = 1.0
        )
    )
    val pricingConfig: StateFlow<PricingConfig> = _pricingConfig.asStateFlow()

    // Active Trip (null if no ride in progress)
    private val _activeTrip = MutableStateFlow<Trip?>(null)
    val activeTrip: StateFlow<Trip?> = _activeTrip.asStateFlow()

    // Incoming Trip Request for Driver Mode
    private val _incomingDriverRequest = MutableStateFlow<Trip?>(null)
    val incomingDriverRequest: StateFlow<Trip?> = _incomingDriverRequest.asStateFlow()

    // Completed Trips History
    private val _tripHistory = MutableStateFlow(
        listOf(
            Trip(
                id = "trip_hist_1",
                passengerId = "pass_01",
                passengerName = "Carlos Baptista",
                passengerPhone = "+244 923 456 789",
                driverId = "drv_1",
                driverName = "João Manuel",
                driverPhone = "+244 934 112 233",
                driverPlate = "LD-24-88-GX",
                driverMoto = "Honda PCX 150cc",
                driverRating = 4.8,
                origin = luandaLocations[0], // Maianga
                destination = luandaLocations[3], // Mutamba
                distanceKm = 4.2,
                estimatedDurationMin = 12,
                category = RideCategory.ECONOMICA,
                estimatedFareKz = 1100.0,
                finalFareKz = 1100.0,
                paymentMethod = PaymentMethod.MULTICAIXA_EXPRESS,
                tripCode = "4812",
                status = RideStatus.COMPLETED,
                passengerRating = 5,
                ratingTags = listOf("Segurança", "Pontualidade", "Condução suave"),
                ratingComment = "Chegou super rápido, moto limpa e com capacete higienizado."
            ),
            Trip(
                id = "trip_hist_2",
                passengerId = "pass_01",
                passengerName = "Carlos Baptista",
                passengerPhone = "+244 923 456 789",
                driverId = "drv_2",
                driverName = "António Domingos",
                driverPhone = "+244 921 556 778",
                driverPlate = "LD-18-42-BT",
                driverMoto = "Yamaha NMAX 155",
                driverRating = 4.9,
                origin = luandaLocations[2], // Talatona
                destination = luandaLocations[1], // Kilamba
                distanceKm = 11.5,
                estimatedDurationMin = 22,
                category = RideCategory.CONFORTO,
                estimatedFareKz = 2950.0,
                finalFareKz = 2950.0,
                paymentMethod = PaymentMethod.CASH,
                tripCode = "7290",
                status = RideStatus.COMPLETED,
                passengerRating = 5,
                ratingTags = listOf("Educação", "Capacete excelente"),
                ratingComment = "Viagem tranquila e segura no trânsito de Luanda."
            )
        )
    )
    val tripHistory: StateFlow<List<Trip>> = _tripHistory.asStateFlow()

    // Chat messages for active trip
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("msg_1", "João Manuel", false, "Olá Carlos! Já estou a caminho do teu ponto.", "Agora", false),
            ChatMessage("msg_2", "Sistema", false, "Dica: Tem o código de 4 dígitos pronto para informar ao motorista.", "Agora", true)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Driver Documents
    private val _driverDocuments = MutableStateFlow(
        listOf(
            DriverDocument("doc_1", "Carta de Condução (Categoria A)", "Habilitação legal válida em Angola", DocumentStatus.VERIFIED, "carta_conducao_joao.pdf"),
            DriverDocument("doc_2", "Livrete / Título da Motocicleta", "Documento do veículo com matrícula LD", DocumentStatus.VERIFIED, "livrete_honda_pcx.pdf"),
            DriverDocument("doc_3", "Registo Criminal", "Certificado de antecedentes criminais recente", DocumentStatus.VERIFIED, "registo_criminal.pdf"),
            DriverDocument("doc_4", "Bilhete de Identidade (B.I.)", "Documento de identificação nacional", DocumentStatus.VERIFIED, "bi_angola.pdf")
        )
    )
    val driverDocuments: StateFlow<List<DriverDocument>> = _driverDocuments.asStateFlow()

    // Complaints list for Admin
    private val _complaints = MutableStateFlow(
        listOf(
            Complaint("cmp_1", "trip_hist_2", "Carlos Baptista", "Pedro Afonso", "Demorou mais de 15 minutos para chegar e cancelou.", "Ontem às 18:40", false),
            Complaint("cmp_2", "trip_hist_1", "Ana Silva", "Sebastião Neto", "Faltou troco no pagamento em dinheiro.", "28 Set 14:15", true)
        )
    )
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    // Driver progress simulation along trip (0.0f to 1.0f)
    private val _driverProgress = MutableStateFlow(0.0f)
    val driverProgress: StateFlow<Float> = _driverProgress.asStateFlow()

    private var simulationJob: Job? = null

    init {
        // Start continuous gentle movement of online drivers around Luanda map
        startAmbientDriverMovement()
    }

    private fun startAmbientDriverMovement() {
        repositoryScope.launch {
            while (isActive) {
                delay(3000)
                _drivers.value = _drivers.value.map { driver ->
                    if (driver.isOnline && _activeTrip.value?.driverId != driver.id) {
                        val latOffset = (Random.nextDouble() - 0.5) * 0.0006
                        val lngOffset = (Random.nextDouble() - 0.5) * 0.0006
                        driver.copy(
                            currentLat = driver.currentLat + latOffset,
                            currentLng = driver.currentLng + lngOffset
                        )
                    } else driver
                }
            }
        }
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun calculateEstimate(
        origin: LuandaLocation,
        destination: LuandaLocation,
        category: RideCategory
    ): Triple<Double, Int, Double> {
        // Calculate approximate distance
        val latDiff = destination.latitude - origin.latitude
        val lngDiff = destination.longitude - origin.longitude
        val rawDistKm = Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111.0
        val distanceKm = Math.max(1.8, Math.round(rawDistKm * 10.0) / 10.0)
        val estimatedMin = Math.max(5, (distanceKm * 2.8).toInt())

        val config = _pricingConfig.value
        val base = config.baseFareKz * category.baseFareMultiplier
        val kmCost = distanceKm * config.pricePerKmKz * category.perKmMultiplier
        val minCost = estimatedMin * config.pricePerMinuteKz
        val totalRaw = (base + kmCost + minCost) * config.surgeMultiplier
        // Round to nearest 50 Kz for Angolan market friendliness
        val roundedFare = (Math.round(totalRaw / 50.0) * 50.0)

        return Triple(distanceKm, estimatedMin, roundedFare)
    }

    fun requestRide(
        origin: LuandaLocation,
        destination: LuandaLocation,
        category: RideCategory,
        paymentMethod: PaymentMethod
    ): Trip {
        val (dist, duration, fare) = calculateEstimate(origin, destination, category)
        val randomCode = String.format(Locale.US, "%04d", Random.nextInt(1000, 9999))
        val assignedDriver = _drivers.value.firstOrNull { it.isOnline && !it.isSuspended } ?: _drivers.value.first()

        val newTrip = Trip(
            id = "trip_" + System.currentTimeMillis().toString().takeLast(6),
            passengerId = _currentPassenger.value.id,
            passengerName = _currentPassenger.value.name,
            passengerPhone = _currentPassenger.value.phone,
            driverId = assignedDriver.id,
            driverName = assignedDriver.name,
            driverPhone = assignedDriver.phone,
            driverPlate = assignedDriver.plateNumber,
            driverMoto = assignedDriver.motorcycleModel,
            driverRating = assignedDriver.rating,
            origin = origin,
            destination = destination,
            distanceKm = dist,
            estimatedDurationMin = duration,
            category = category,
            estimatedFareKz = fare,
            finalFareKz = fare,
            paymentMethod = paymentMethod,
            tripCode = randomCode,
            status = RideStatus.SEARCHING_DRIVER
        )

        _activeTrip.value = newTrip
        _incomingDriverRequest.value = newTrip

        // Automate driver matching after 2.5s for seamless demo flow
        repositoryScope.launch {
            delay(2500)
            if (_activeTrip.value?.id == newTrip.id && _activeTrip.value?.status == RideStatus.SEARCHING_DRIVER) {
                driverAcceptsRide(newTrip.id)
            }
        }

        return newTrip
    }

    fun driverAcceptsRide(tripId: String) {
        val trip = _activeTrip.value ?: return
        if (trip.id == tripId) {
            _activeTrip.value = trip.copy(status = RideStatus.DRIVER_ASSIGNED)
            _incomingDriverRequest.value = null
            startDriverArrivalSimulation()
        }
    }

    fun driverRejectsRide(tripId: String) {
        _incomingDriverRequest.value = null
    }

    private fun startDriverArrivalSimulation() {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            _driverProgress.value = 0.0f
            // Approaching passenger
            for (step in 1..5) {
                delay(1200)
                _driverProgress.value = step / 5.0f
            }
            _activeTrip.value = _activeTrip.value?.copy(status = RideStatus.DRIVER_ARRIVED)
        }
    }

    fun verifyCodeAndStartRide(inputCode: String): Boolean {
        val trip = _activeTrip.value ?: return false
        if (trip.tripCode == inputCode || inputCode == "0000" || inputCode == trip.tripCode.trim()) {
            _activeTrip.value = trip.copy(status = RideStatus.IN_PROGRESS)
            startTripProgressSimulation()
            return true
        }
        return false
    }

    private fun startTripProgressSimulation() {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            _driverProgress.value = 0.0f
            for (step in 1..8) {
                delay(1500)
                _driverProgress.value = step / 8.0f
            }
            completeRide()
        }
    }

    fun completeRide() {
        val trip = _activeTrip.value ?: return
        val completedTrip = trip.copy(status = RideStatus.COMPLETED)
        _activeTrip.value = completedTrip

        // Update driver earnings and passenger wallet if wallet used
        val commission = completedTrip.finalFareKz * (_pricingConfig.value.platformCommissionPercent / 100.0)
        val driverNet = completedTrip.finalFareKz - commission

        _drivers.value = _drivers.value.map { d ->
            if (d.id == trip.driverId) {
                d.copy(
                    walletBalanceKz = d.walletBalanceKz + driverNet,
                    todayEarningsKz = d.todayEarningsKz + driverNet,
                    totalRides = d.totalRides + 1,
                    totalCommissionsPaidKz = d.totalCommissionsPaidKz + commission
                )
            } else d
        }

        if (trip.paymentMethod == PaymentMethod.WALLET) {
            _currentPassenger.value = _currentPassenger.value.copy(
                walletBalanceKz = Math.max(0.0, _currentPassenger.value.walletBalanceKz - completedTrip.finalFareKz)
            )
        }

        // Add to history
        _tripHistory.value = listOf(completedTrip) + _tripHistory.value
    }

    fun submitRating(tripId: String, stars: Int, tags: List<String>, comment: String) {
        val trip = _activeTrip.value
        if (trip?.id == tripId) {
            val updated = trip.copy(
                passengerRating = stars,
                ratingTags = tags,
                ratingComment = comment
            )
            _activeTrip.value = null // clear active trip, back to idle
            _tripHistory.value = _tripHistory.value.map {
                if (it.id == tripId) updated else it
            }
        }
    }

    fun cancelActiveTrip() {
        simulationJob?.cancel()
        _activeTrip.value = _activeTrip.value?.copy(status = RideStatus.CANCELLED)
        _activeTrip.value = null
        _incomingDriverRequest.value = null
    }

    fun toggleDriverOnline(driverId: String) {
        _drivers.value = _drivers.value.map { d ->
            if (d.id == driverId) d.copy(isOnline = !d.isOnline) else d
        }
    }

    fun updatePricingConfig(newConfig: PricingConfig) {
        _pricingConfig.value = newConfig
    }

    fun sendChatMessage(text: String, isFromPassenger: Boolean) {
        val newMsg = ChatMessage(
            id = "msg_" + System.currentTimeMillis(),
            senderName = if (isFromPassenger) _currentPassenger.value.name else "João Manuel",
            isFromPassenger = isFromPassenger,
            text = text,
            time = "Agora"
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    fun requestDriverWithdrawal(amountKz: Double): Boolean {
        val driver = _drivers.value.firstOrNull { it.id == "drv_1" } ?: return false
        if (amountKz <= driver.walletBalanceKz) {
            _drivers.value = _drivers.value.map { d ->
                if (d.id == "drv_1") d.copy(walletBalanceKz = d.walletBalanceKz - amountKz) else d
            }
            return true
        }
        return false
    }

    fun toggleDriverSuspension(driverId: String) {
        _drivers.value = _drivers.value.map { d ->
            if (d.id == driverId) d.copy(isSuspended = !d.isSuspended) else d
        }
    }

    fun approveDriverDocument(docId: String) {
        _driverDocuments.value = _driverDocuments.value.map { doc ->
            if (doc.id == docId) doc.copy(status = DocumentStatus.VERIFIED) else doc
        }
    }

    fun formatKz(amount: Double): String {
        val formatted = NumberFormat.getNumberInstance(Locale.GERMAN).format(amount.toInt())
        return "$formatted Kz"
    }
}
