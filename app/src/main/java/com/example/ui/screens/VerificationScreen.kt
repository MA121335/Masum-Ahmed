package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.UrgentAmber
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.BloodViewModel
import com.example.util.EligibilityHelper
import java.util.concurrent.TimeUnit

@Composable
fun VerificationScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val eligibility by viewModel.userEligibility.collectAsStateWithLifecycle()

    var testAge by remember { mutableStateOf("${userProfile.age}") }
    var testWeight by remember { mutableStateOf("${userProfile.weightKg.toInt()}") }
    var testHb by remember { mutableStateOf("${userProfile.hemoglobin}") }
    var daysSinceLast by remember { mutableStateOf("98") }
    var documentUploaded by remember { mutableStateOf(userProfile.isIdVerified) }

    val daysSinceNum = daysSinceLast.toLongOrNull() ?: 98L
    val calculatedDiffMillis = TimeUnit.DAYS.toMillis(daysSinceNum)
    val simulatedLastDonation = System.currentTimeMillis() - calculatedDiffMillis

    val evaluatedResult = remember(testAge, testWeight, testHb, daysSinceNum, documentUploaded) {
        val age = testAge.toIntOrNull() ?: 25
        val weight = testWeight.toFloatOrNull() ?: 60f
        val hb = testHb.toFloatOrNull() ?: 13.5f
        EligibilityHelper.checkEligibility(
            lastDonationTimestamp = simulatedLastDonation,
            age = age,
            weightKg = weight,
            hemoglobin = hb,
            isIdVerified = documentUploaded,
            isMedicalClearance = true
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("verification_screen_scroll")
    ) {
        // Verification Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BloodRedDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Verification & Standards",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Sahiddirgonj Blood Safety Protocol",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current User Verification Status
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Your Profile: Verified Hero",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "ID & Blood Group Lab clearance verified",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Eligibility Score Banner
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (evaluatedResult.isEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (evaluatedResult.isEligible) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (evaluatedResult.isEligible) VerifiedGreen else BloodRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (evaluatedResult.isEligible) "CLEAR TO DONATE TODAY" else "RECOVERY / REST REQUIRED",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (evaluatedResult.isEligible) VerifiedGreen else BloodRed
                            )
                        }

                        Text(
                            text = "${evaluatedResult.scorePercentage}% Ready",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (evaluatedResult.isEligible) VerifiedGreen else BloodRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { evaluatedResult.scorePercentage / 100f },
                        color = if (evaluatedResult.isEligible) VerifiedGreen else BloodRed,
                        trackColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (evaluatedResult.isEligible) {
                        Text(
                            text = "You meet all international WHO and Bangladesh Blood Bank safety thresholds for whole blood donation.",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    } else {
                        Column {
                            evaluatedResult.reasons.forEach { reason ->
                                Text(
                                    text = "• $reason",
                                    fontSize = 12.sp,
                                    color = BloodRed,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Eligibility Self-Test Wizard
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("eligibility_calculator_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = BloodRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Donation Eligibility Calculator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Verify your medical parameters against donation standards",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = testAge,
                            onValueChange = { testAge = it },
                            label = { Text("Age (18-65)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_age_input")
                        )
                        OutlinedTextField(
                            value = testWeight,
                            onValueChange = { testWeight = it },
                            label = { Text("Weight (≥50kg)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_weight_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = testHb,
                            onValueChange = { testHb = it },
                            label = { Text("Hb (≥12.5 g/dL)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_hb_input")
                        )
                        OutlinedTextField(
                            value = daysSinceLast,
                            onValueChange = { daysSinceLast = it },
                            label = { Text("Days Since Last") },
                            placeholder = { Text("e.g. 98") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_cooldown_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 90-day cooldown status bar
                    Text(
                        text = "3-Month Cooldown Progress: ${daysSinceNum.coerceAtMost(90)} / 90 Days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val cooldownProgress = (daysSinceNum.toFloat() / 90f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { cooldownProgress },
                        color = if (daysSinceNum >= 90) VerifiedGreen else UrgentAmber,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val age = testAge.toIntOrNull() ?: 26
                            val weight = testWeight.toFloatOrNull() ?: 68f
                            val hb = testHb.toFloatOrNull() ?: 14f
                            viewModel.updateSelfProfile(
                                name = userProfile.name,
                                bloodGroup = userProfile.bloodGroup,
                                age = age,
                                weight = weight,
                                hemoglobin = hb,
                                lastDonationDateMillis = simulatedLastDonation
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Update My Verified Record")
                    }
                }
            }
        }

        // Verification Tiers & Badges Explanation
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sahiddirgonj Recognition Tiers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Honoring our regular volunteer donors",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BadgeTierRow(
                        title = "Life Saver 🏆",
                        description = "10+ donations across Sahiddirgonj hospitals",
                        badgeColor = BloodRed
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BadgeTierRow(
                        title = "Gold Donor ⭐",
                        description = "5+ verified donations with stellar reliability",
                        badgeColor = UrgentAmber
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BadgeTierRow(
                        title = "Verified Hero ✓",
                        description = "National ID & Blood Group Lab Test verified",
                        badgeColor = VerifiedGreen
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeTierRow(
    title: String,
    description: String,
    badgeColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = badgeColor.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier
                    .padding(6.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = badgeColor)
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
