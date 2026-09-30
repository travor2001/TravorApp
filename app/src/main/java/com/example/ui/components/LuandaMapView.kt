package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Driver
import com.example.model.LuandaLocation
import com.example.model.RideStatus
import com.example.model.Trip
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LuandaMapView(
    modifier: Modifier = Modifier,
    drivers: List<Driver> = emptyList(),
    activeTrip: Trip? = null,
    driverProgress: Float = 0f,
    selectedOrigin: LuandaLocation? = null,
    selectedDestination: LuandaLocation? = null,
    onLocationClick: ((LuandaLocation) -> Unit)? = null
) {
    // Pulse animation for online bikes & user location
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Map offset for drag/pan
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    panOffsetX += pan.x
                    panOffsetY += pan.y
                    zoomScale = (zoomScale * zoom).coerceIn(0.7f, 2.5f)
                }
            }
            .testTag("luanda_map_view")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = (width / 2f) + panOffsetX
            val centerY = (height / 2f) + panOffsetY

            // 1. Draw Map Base (Water, Coastline, City Grids)
            drawLuandaBaseMap(width, height, centerX, centerY, zoomScale)

            // Coordinate conversion helper: Luanda center approx (-8.835, 13.24)
            fun geoToCanvas(lat: Double, lng: Double): Offset {
                val scale = 2400f * zoomScale
                val x = centerX + ((lng - 13.240) * scale).toFloat()
                val y = centerY - ((lat - (-8.835)) * scale).toFloat()
                return Offset(x, y)
            }

            // 2. Draw Available Drivers (Motorcyclists)
            drivers.forEach { driver ->
                if (driver.isOnline) {
                    val pos = geoToCanvas(driver.currentLat, driver.currentLng)
                    if (pos.x in -100f..width + 100f && pos.y in -100f..height + 100f) {
                        // Pulse wave
                        drawCircle(
                            color = MotoGoldPrimary.copy(alpha = pulseAlpha),
                            radius = 24f * pulseScale * zoomScale,
                            center = pos
                        )
                        // Outer ring
                        drawCircle(
                            color = DarkBackground,
                            radius = 14f * zoomScale,
                            center = pos
                        )
                        // Solid core
                        drawCircle(
                            color = if (driver.isSuspended) StatusRed else MotoGoldPrimary,
                            radius = 11f * zoomScale,
                            center = pos
                        )
                        // Center dot
                        drawCircle(
                            color = OnMotoGold,
                            radius = 4f * zoomScale,
                            center = pos
                        )
                    }
                }
            }

            // 3. Draw Active Trip Route or Planned Route
            val originLoc = activeTrip?.origin ?: selectedOrigin
            val destLoc = activeTrip?.destination ?: selectedDestination

            if (originLoc != null && destLoc != null) {
                val pStart = geoToCanvas(originLoc.latitude, originLoc.longitude)
                val pEnd = geoToCanvas(destLoc.latitude, destLoc.longitude)

                // Route Path
                val routePath = Path().apply {
                    moveTo(pStart.x, pStart.y)
                    // Create realistic road segments with waypoints
                    val midX1 = pStart.x + (pEnd.x - pStart.x) * 0.4f + (if (pStart.y < pEnd.y) 30f else -30f)
                    val midY1 = pStart.y + (pEnd.y - pStart.y) * 0.2f
                    val midX2 = pStart.x + (pEnd.x - pStart.x) * 0.7f
                    val midY2 = pStart.y + (pEnd.y - pStart.y) * 0.75f + 25f

                    cubicTo(midX1, midY1, midX2, midY2, pEnd.x, pEnd.y)
                }

                // Route glow background
                drawPath(
                    path = routePath,
                    color = MotoGoldPrimary.copy(alpha = 0.25f),
                    style = Stroke(width = 14f * zoomScale, cap = StrokeCap.Round)
                )

                // Route core line
                drawPath(
                    path = routePath,
                    color = MotoGoldPrimary,
                    style = Stroke(
                        width = 6f * zoomScale,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f), 0f)
                    )
                )

                // Origin Marker (Pickup Point - Green Pulse)
                drawCircle(
                    color = StatusGreen.copy(alpha = pulseAlpha),
                    radius = 28f * pulseScale * zoomScale,
                    center = pStart
                )
                drawCircle(
                    color = DarkBackground,
                    radius = 15f * zoomScale,
                    center = pStart
                )
                drawCircle(
                    color = StatusGreen,
                    radius = 11f * zoomScale,
                    center = pStart
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f * zoomScale,
                    center = pStart
                )

                // Destination Marker (Red Flag)
                drawCircle(
                    color = StatusRed.copy(alpha = 0.25f),
                    radius = 24f * zoomScale,
                    center = pEnd
                )
                drawCircle(
                    color = DarkBackground,
                    radius = 15f * zoomScale,
                    center = pEnd
                )
                drawCircle(
                    color = StatusRed,
                    radius = 11f * zoomScale,
                    center = pEnd
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f * zoomScale,
                    center = pEnd
                )

                // Animated Active Driver Movement along route
                if (activeTrip != null && (activeTrip.status == RideStatus.DRIVER_ASSIGNED || activeTrip.status == RideStatus.IN_PROGRESS)) {
                    val driverPos = if (activeTrip.status == RideStatus.DRIVER_ASSIGNED) {
                        // Driver moving to pickup
                        val approachX = pStart.x - 70f + (70f * driverProgress)
                        val approachY = pStart.y + 60f - (60f * driverProgress)
                        Offset(approachX, approachY)
                    } else {
                        // Driver moving along route from pickup to destination
                        val currentX = pStart.x + (pEnd.x - pStart.x) * driverProgress
                        val currentY = pStart.y + (pEnd.y - pStart.y) * driverProgress
                        Offset(currentX, currentY)
                    }

                    // Driver helmet circle with glow
                    drawCircle(
                        color = MotoGoldLight,
                        radius = 22f * zoomScale,
                        center = driverPos
                    )
                    drawCircle(
                        color = OnMotoGold,
                        radius = 17f * zoomScale,
                        center = driverPos
                    )
                    drawCircle(
                        color = MotoGoldPrimary,
                        radius = 12f * zoomScale,
                        center = driverPos
                    )
                }
            } else if (originLoc != null) {
                // Just Origin selected
                val pStart = geoToCanvas(originLoc.latitude, originLoc.longitude)
                drawCircle(
                    color = StatusGreen.copy(alpha = pulseAlpha),
                    radius = 30f * pulseScale * zoomScale,
                    center = pStart
                )
                drawCircle(
                    color = StatusGreen,
                    radius = 12f * zoomScale,
                    center = pStart
                )
            }
        }

        // Map Control Floating Buttons (Compass, Recenter, Layer)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1.0f
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("recenter_map_button"),
                containerColor = DarkSurfaceElevated,
                contentColor = MotoGoldPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recentrar Mapa",
                    modifier = Modifier.size(20.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    zoomScale = (zoomScale * 1.25f).coerceAtMost(2.5f)
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("zoom_in_button"),
                containerColor = DarkSurfaceElevated,
                contentColor = DarkTextPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar Zoom",
                    modifier = Modifier.size(20.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.7f)
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("zoom_out_button"),
                containerColor = DarkSurfaceElevated,
                contentColor = DarkTextPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Diminuir Zoom",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Luanda City Badge in corner
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 70.dp, start = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusGreen)
                )
                Text(
                    text = "Luanda, AO • ${drivers.count { it.isOnline }} motos online",
                    color = DarkTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
    }
}

private fun DrawScope.drawLuandaBaseMap(
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float,
    zoomScale: Float
) {
    // Background Dark Asphalt
    drawRect(color = Color(0xFF101217))

    // Subtle Grid representing city blocks
    val gridSize = 70f * zoomScale
    val startX = (centerX % gridSize) - gridSize
    val startY = (centerY % gridSize) - gridSize

    var x = startX
    while (x < width + gridSize) {
        drawLine(
            color = Color(0xFF181B22),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1.2f
        )
        x += gridSize
    }

    var y = startY
    while (y < height + gridSize) {
        drawLine(
            color = Color(0xFF181B22),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1.2f
        )
        y += gridSize
    }

    // Atlantic Ocean / Baía de Luanda Coastline (top-left arc)
    val waterPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(width * 0.45f, 0f)
        cubicTo(
            width * 0.35f, height * 0.15f,
            width * 0.15f, height * 0.30f,
            0f, height * 0.42f
        )
        close()
    }
    drawPath(
        path = waterPath,
        color = Color(0xFF0A1424) // Deep ocean blue
    )
    drawPath(
        path = waterPath,
        color = Color(0xFF1A3358),
        style = Stroke(width = 3f * zoomScale)
    )

    // Major Arterial Expressways of Luanda (Estrada de Catete, Via Expressa, Deolinda Rodrigues)
    // Expressway 1 (Diagonal East-West)
    val exp1 = Path().apply {
        moveTo(-100f, centerY + 80f * zoomScale)
        cubicTo(
            centerX - 60f * zoomScale, centerY + 30f * zoomScale,
            centerX + 120f * zoomScale, centerY - 40f * zoomScale,
            width + 100f, centerY - 120f * zoomScale
        )
    }
    drawPath(
        path = exp1,
        color = Color(0xFF262A36),
        style = Stroke(width = 16f * zoomScale, cap = StrokeCap.Round)
    )
    drawPath(
        path = exp1,
        color = Color(0xFF3B4153),
        style = Stroke(width = 6f * zoomScale, cap = StrokeCap.Round)
    )

    // Expressway 2 (North-South: Baixa -> Maianga -> Talatona -> Kilamba)
    val exp2 = Path().apply {
        moveTo(centerX - 40f * zoomScale, -100f)
        cubicTo(
            centerX - 20f * zoomScale, centerY - 60f * zoomScale,
            centerX + 30f * zoomScale, centerY + 100f * zoomScale,
            centerX + 150f * zoomScale, height + 100f
        )
    }
    drawPath(
        path = exp2,
        color = Color(0xFF262A36),
        style = Stroke(width = 14f * zoomScale, cap = StrokeCap.Round)
    )
    drawPath(
        path = exp2,
        color = Color(0xFF3B4153),
        style = Stroke(width = 5f * zoomScale, cap = StrokeCap.Round)
    )

    // Expressway 3 (Via Expressa connecting Viana and Cacuaco)
    val exp3 = Path().apply {
        moveTo(centerX + 80f * zoomScale, -50f)
        lineTo(centerX + 260f * zoomScale, height + 80f)
    }
    drawPath(
        path = exp3,
        color = Color(0xFF20232E),
        style = Stroke(width = 10f * zoomScale)
    )
}
