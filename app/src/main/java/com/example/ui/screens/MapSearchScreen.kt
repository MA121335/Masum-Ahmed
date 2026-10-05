package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Donor
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.UrgentAmber
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.BloodViewModel
import com.example.util.EligibilityHelper
import com.example.util.LocationUtils
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapSearchScreen(
    viewModel: BloodViewModel,
    onDonorDetailClick: (Donor) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val donors by viewModel.mapDonors.collectAsStateWithLifecycle()
    val radiusKm by viewModel.mapRadiusKm.collectAsStateWithLifecycle()
    val bloodFilter by viewModel.selectedBloodGroupFilter.collectAsStateWithLifecycle()
    val onlyAvailable by viewModel.onlyAvailableOnMap.collectAsStateWithLifecycle()
    val selectedDonor by viewModel.selectedMapDonor.collectAsStateWithLifecycle()

    val bloodGroups = listOf("ALL", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Radar scan rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_search_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Map Top Control Panel
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationSearching,
                                contentDescription = "Radar",
                                tint = BloodRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Sahiddirgonj Donor Radar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Found ${donors.size} donor(s) within ${String.format("%.1f", radiusKm)} km",
                                    fontSize = 12.sp,
                                    color = BloodRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Available Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (onlyAvailable) VerifiedGreen else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = onlyAvailable,
                                onCheckedChange = { viewModel.toggleOnlyAvailableOnMap(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VerifiedGreen
                                ),
                                modifier = Modifier.testTag("map_available_switch")
                            )
                        }
                    }

                    // Blood Filter Chips
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(bloodGroups) { bg ->
                            FilterChip(
                                selected = bloodFilter == bg,
                                onClick = { viewModel.setBloodGroupFilter(bg) },
                                label = { Text(bg, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BloodRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Radius Slider Control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Radius:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = radiusKm,
                            onValueChange = { viewModel.setMapRadius(it) },
                            valueRange = 1.0f..25.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = BloodRed,
                                activeTrackColor = BloodRed
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("radius_slider")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${radiusKm.toInt()} km",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodRed
                        )
                    }
                }
            }

            // Radar & Canvas Map Area
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF141923)) // Rich night-mode map surface
            ) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()
                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f
                val maxRadiusPx = (minOf(canvasWidth, canvasHeight) / 2f) * 0.88f

                // Store click zones for markers
                val markerTouchTargets = remember(donors, radiusKm, canvasWidth, canvasHeight) {
                    donors.map { donor ->
                        val (posX, posY) = computeScreenCoordinates(
                            donor.latitude,
                            donor.longitude,
                            centerX,
                            centerY,
                            radiusKm,
                            maxRadiusPx
                        )
                        Pair(donor, Offset(posX, posY))
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(markerTouchTargets) {
                            detectTapGestures { tapOffset ->
                                // Check if user tapped near any donor marker
                                val tapped = markerTouchTargets.find { (_, pos) ->
                                    (tapOffset - pos).getDistance() <= 60f
                                }
                                viewModel.selectMapDonor(tapped?.first)
                            }
                        }
                        .testTag("radar_map_canvas")
                ) {
                    // Draw map grid and streets
                    drawMapGridAndRoads(centerX, centerY, canvasWidth, canvasHeight)

                    // Draw concentric radar range circles
                    val rings = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                    rings.forEach { ratio ->
                        val r = maxRadiusPx * ratio
                        drawCircle(
                            color = Color(0xFF263238),
                            radius = r,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        )
                    }

                    // Pulsing animated outer ring
                    drawCircle(
                        color = BloodRed.copy(alpha = 0.25f * (1f - radarPulse)),
                        radius = maxRadiusPx * radarPulse,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 3f)
                    )

                    // Rotating radar sweep beam
                    val sweepRad = Math.toRadians(radarAngle.toDouble())
                    val sweepX = centerX + (maxRadiusPx * cos(sweepRad)).toFloat()
                    val sweepY = centerY + (maxRadiusPx * sin(sweepRad)).toFloat()
                    drawLine(
                        brush = Brush.radialGradient(
                            colors = listOf(BloodRed.copy(alpha = 0.5f), Color.Transparent),
                            center = Offset(centerX, centerY),
                            radius = maxRadiusPx
                        ),
                        start = Offset(centerX, centerY),
                        end = Offset(sweepX, sweepY),
                        strokeWidth = 3f
                    )

                    // Draw Sahiddirgonj Center Pin (Red Drop Icon)
                    drawCircle(
                        color = Color.White,
                        radius = 16f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = BloodRed,
                        radius = 12f,
                        center = Offset(centerX, centerY)
                    )

                    // Draw Donors
                    markerTouchTargets.forEach { (donor, pos) ->
                        val isSelected = selectedDonor?.id == donor.id
                        val pinColor = when {
                            donor.isAvailable -> VerifiedGreen
                            donor.lastDonationDate > 0L -> UrgentAmber
                            else -> Color.Gray
                        }

                        // Selected halo ring
                        if (isSelected) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.6f),
                                radius = 34f,
                                center = pos
                            )
                        }

                        // Outer marker circle
                        drawCircle(
                            color = Color.White,
                            radius = 24f,
                            center = pos
                        )
                        drawCircle(
                            color = pinColor,
                            radius = 20f,
                            center = pos
                        )

                        // Draw blood group text using native canvas
                        val textPaint = Paint().asFrameworkPaint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            donor.bloodGroup,
                            pos.x,
                            pos.y + 10f,
                            textPaint
                        )
                    }

                    // Draw Town Center Label
                    val labelPaint = Paint().asFrameworkPaint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        "Sahiddirgonj Central",
                        centerX,
                        centerY + 34f,
                        labelPaint
                    )
                }
            }
        }

        // Floating Selected Donor Bottom Card
        AnimatedVisibility(
            visible = selectedDonor != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 75.dp)
        ) {
            selectedDonor?.let { donor ->
                val distance = LocationUtils.calculateDistanceKm(
                    LocationUtils.SAHIDDIRGONJ_CENTER_LAT,
                    LocationUtils.SAHIDDIRGONJ_CENTER_LNG,
                    donor.latitude,
                    donor.longitude
                )

                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("selected_donor_bottom_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 48.dp, fontSize = 16)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = donor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${donor.area} • ${LocationUtils.formatDistance(distance)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.selectMapDonor(null) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VerificationBadge(badgeType = donor.verificationBadge, isIdVerified = donor.isIdVerified)

                            if (donor.isAvailable) {
                                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "Eligible to Donate",
                                        color = VerifiedGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "In Cooldown",
                                        color = UrgentAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Button(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
                                    context.startActivity(dialIntent)
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = BloodRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("call_selected_map_donor")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Donor", fontWeight = FontWeight.Bold)
                            }

                            androidx.compose.material3.OutlinedButton(
                                onClick = { onDonorDetailClick(donor) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_selected_map_donor_profile")
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Full Profile", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun computeScreenCoordinates(
    lat: Double,
    lng: Double,
    centerX: Float,
    centerY: Float,
    radiusKm: Float,
    maxRadiusPx: Float
): Pair<Float, Float> {
    // 1 degree latitude ~= 111 km
    val dLatKm = (lat - LocationUtils.SAHIDDIRGONJ_CENTER_LAT) * 111.0
    // 1 degree longitude ~= 111 * cos(lat) km
    val dLngKm = (lng - LocationUtils.SAHIDDIRGONJ_CENTER_LNG) * 111.0 * cos(Math.toRadians(LocationUtils.SAHIDDIRGONJ_CENTER_LAT))

    val scalePxPerKm = maxRadiusPx / radiusKm
    val screenX = (centerX + (dLngKm * scalePxPerKm)).toFloat()
    // Latitude increases upwards, so screen Y decreases
    val screenY = (centerY - (dLatKm * scalePxPerKm)).toFloat()

    return Pair(screenX, screenY)
}

private fun DrawScope.drawMapGridAndRoads(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
) {
    val roadColor = Color(0xFF2C394B)
    val riverColor = Color(0xFF1E3A8A).copy(alpha = 0.4f)

    // Riverview River curve (East of Sahiddirgonj)
    val riverPath = Path().apply {
        moveTo(centerX + 120f, 0f)
        cubicTo(
            centerX + 150f, height * 0.3f,
            centerX + 90f, height * 0.7f,
            centerX + 160f, height
        )
    }
    drawPath(path = riverPath, color = riverColor, style = Stroke(width = 24f))

    // Main Hospital Road (Horizontal)
    drawLine(
        color = roadColor,
        start = Offset(0f, centerY),
        end = Offset(width, centerY),
        strokeWidth = 6f
    )

    // Station & College Bypass (Vertical)
    drawLine(
        color = roadColor,
        start = Offset(centerX, 0f),
        end = Offset(centerX, height),
        strokeWidth = 6f
    )

    // Diagonal bypass roads
    drawLine(
        color = roadColor.copy(alpha = 0.6f),
        start = Offset(0f, 0f),
        end = Offset(width, height),
        strokeWidth = 3f
    )
    drawLine(
        color = roadColor.copy(alpha = 0.6f),
        start = Offset(width, 0f),
        end = Offset(0f, height),
        strokeWidth = 3f
    )
}
