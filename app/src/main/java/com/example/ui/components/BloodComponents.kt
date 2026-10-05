package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.BloodRedLight
import com.example.ui.theme.UrgentAmber
import com.example.ui.theme.VerifiedGreen
import com.example.util.EligibilityHelper
import com.example.util.LocationUtils

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    size: Dp = 48.dp,
    fontSize: Int = 16,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(BloodRedLight, BloodRed, BloodRedDark)
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
    ) {
        Text(
            text = bloodGroup,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = fontSize.sp,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun VerificationBadge(
    badgeType: String,
    isIdVerified: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (label, icon, bgCol, textCol) = when (badgeType) {
        "LIFE_SAVER" -> Quadruple("Life Saver 🏆", Icons.Default.Star, Color(0xFFFFEBEE), BloodRed)
        "GOLD_DONOR" -> Quadruple("Gold Donor ⭐", Icons.Default.Star, Color(0xFFFFF8E1), UrgentAmber)
        "VERIFIED_HERO" -> Quadruple("Verified Hero ✓", Icons.Default.Shield, Color(0xFFE8F5E9), VerifiedGreen)
        else -> Quadruple("Member", Icons.Default.CheckCircle, Color(0xFFF5F5F5), Color.DarkGray)
    }

    Surface(
        color = bgCol,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textCol,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textCol,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun DonorCard(
    donor: Donor,
    onTap: () -> Unit,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val distance = LocationUtils.calculateDistanceKm(
        LocationUtils.SAHIDDIRGONJ_CENTER_LAT,
        LocationUtils.SAHIDDIRGONJ_CENTER_LNG,
        donor.latitude,
        donor.longitude
    )

    val eligibility = EligibilityHelper.checkEligibility(
        lastDonationTimestamp = donor.lastDonationDate,
        age = donor.age,
        weightKg = donor.weightKg,
        hemoglobin = donor.hemoglobin,
        isIdVerified = donor.isIdVerified,
        isMedicalClearance = donor.isMedicalClearance
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .testTag("donor_card_${donor.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 46.dp, fontSize = 16)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = donor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${donor.area} • ${LocationUtils.formatDistance(distance)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Call Action Button
                IconButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BloodRed.copy(alpha = 0.1f))
                        .testTag("call_donor_${donor.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Donor",
                        tint = BloodRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row & Cooldown info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationBadge(badgeType = donor.verificationBadge, isIdVerified = donor.isIdVerified)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${donor.totalDonations} donations",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Eligibility / Cooldown Status
                if (donor.isAvailable && eligibility.isEligible) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "● Ready to Donate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerifiedGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (!eligibility.cooldownPassed) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "⏳ Cooldown: ${eligibility.daysRemainingCooldown}d left",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrgentAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Unavailable",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
