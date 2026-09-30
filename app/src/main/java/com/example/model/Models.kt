package com.example.model

enum class UserRole {
    PASSENGER,
    DRIVER,
    ADMIN
}

enum class RideCategory(
    val title: String,
    val subtitle: String,
    val baseFareMultiplier: Double,
    val perKmMultiplier: Double,
    val iconName: String
) {
    ECONOMICA(
        title = "Moto Económica",
        subtitle = "Deslocações rápidas e acessíveis",
        baseFareMultiplier = 1.0,
        perKmMultiplier = 1.0,
        iconName = "motorcycle"
    ),
    CONFORTO(
        title = "Moto Conforto",
        subtitle = "Capacete duplo, condutor top e moto premium",
        baseFareMultiplier = 1.5,
        perKmMultiplier = 1.35,
        iconName = "shield"
    ),
    ENTREGA(
        title = "Moto Entrega",
        subtitle = "Encomendas pequenas, documentos e caixas",
        baseFareMultiplier = 1.25,
        perKmMultiplier = 1.15,
        iconName = "package"
    )
}

enum class PaymentMethod(val label: String, val icon: String) {
    CASH("Dinheiro físico", "cash"),
    MULTICAIXA_EXPRESS("Multicaixa Express", "card"),
    WALLET("Carteira MotoPay", "wallet")
}

enum class RideStatus {
    IDLE,
    SELECTING_DESTINATION,
    CONFIRMING_REQUEST,
    SEARCHING_DRIVER,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class LuandaLocation(
    val id: String,
    val name: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val isPopular: Boolean = false
)

data class Driver(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Double,
    val totalRides: Int,
    val motorcycleModel: String,
    val plateNumber: String,
    val motorcycleColor: String,
    var isOnline: Boolean,
    var isVerified: Boolean,
    var isSuspended: Boolean = false,
    var currentLat: Double,
    var currentLng: Double,
    var walletBalanceKz: Double = 35400.0,
    var todayEarningsKz: Double = 14500.0,
    var weekEarningsKz: Double = 68200.0,
    var monthEarningsKz: Double = 245000.0,
    var totalCommissionsPaidKz: Double = 18500.0,
    val photoUrl: String = ""
)

data class Passenger(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Double = 4.9,
    val totalRides: Int = 42,
    var walletBalanceKz: Double = 12500.0,
    var isBlocked: Boolean = false
)

data class Trip(
    val id: String,
    val passengerId: String,
    val passengerName: String,
    val passengerPhone: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val driverPlate: String,
    val driverMoto: String,
    val driverRating: Double,
    val origin: LuandaLocation,
    val destination: LuandaLocation,
    val distanceKm: Double,
    val estimatedDurationMin: Int,
    val category: RideCategory,
    val estimatedFareKz: Double,
    var finalFareKz: Double,
    val paymentMethod: PaymentMethod,
    val tripCode: String, // 4-digit security code for passenger to show driver
    var status: RideStatus,
    val createdAt: Long = System.currentTimeMillis(),
    var passengerRating: Int? = null,
    var ratingTags: List<String> = emptyList(),
    var ratingComment: String = ""
)

data class PricingConfig(
    var baseFareKz: Double = 400.0,
    var pricePerKmKz: Double = 150.0,
    var pricePerMinuteKz: Double = 30.0,
    var platformCommissionPercent: Double = 15.0,
    var surgeMultiplier: Double = 1.0
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isFromPassenger: Boolean,
    val text: String,
    val time: String,
    val isQuickReply: Boolean = false
)

data class DriverDocument(
    val id: String,
    val title: String,
    val description: String,
    var status: DocumentStatus,
    val fileName: String
)

enum class DocumentStatus {
    VERIFIED,
    PENDING,
    REJECTED
}

data class Complaint(
    val id: String,
    val tripId: String,
    val authorName: String,
    val targetName: String,
    val reason: String,
    val date: String,
    var isResolved: Boolean = false
)
