package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Donor
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.DonorCard
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.UrgentAmber
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.BloodViewModel
import com.example.util.EligibilityHelper
import com.example.util.LocationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorsDirectoryScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val donors by viewModel.filteredDonors.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBg by viewModel.selectedBloodGroupFilter.collectAsStateWithLifecycle()

    var selectedDonorForDetails by remember { mutableStateOf<Donor?>(null) }
    var showRegisterSheet by remember { mutableStateOf(false) }

    val bloodGroups = listOf("ALL", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRegisterSheet = true },
                containerColor = BloodRed,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("fab_register_donor")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register as Donor", fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Search
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sahiddirgonj Blood Donors",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Verified volunteer donors in our community",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by donor name, area (e.g. Hospital Road)...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = BloodRed)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("donor_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Blood Groups filter row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(bloodGroups) { bg ->
                            FilterChip(
                                selected = selectedBg == bg,
                                onClick = { viewModel.setBloodGroupFilter(bg) },
                                label = { Text(bg, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BloodRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Results count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Showing ${donors.size} Donor(s)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "${donors.count { it.isAvailable }} Available Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerifiedGreen
                )
            }

            // Donors List
            if (donors.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Donors Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Try clearing filters or search by another area/group.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(donors) { donor ->
                        DonorCard(
                            donor = donor,
                            onTap = { selectedDonorForDetails = donor },
                            onCallClick = { phone ->
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(dialIntent)
                            }
                        )
                    }
                }
            }
        }

        // Full Donor Details Dialog
        selectedDonorForDetails?.let { donor ->
            DonorProfileDetailDialog(
                donor = donor,
                onDismiss = { selectedDonorForDetails = null },
                onToggleAvailability = {
                    viewModel.toggleDonorAvailability(donor.id, donor.isAvailable)
                    selectedDonorForDetails = donor.copy(isAvailable = !donor.isAvailable)
                }
            )
        }

        // Register Donor Sheet
        if (showRegisterSheet) {
            RegisterDonorBottomSheet(
                onDismiss = { showRegisterSheet = false },
                onRegister = { name, bg, phone, email, area, age, weight, hb, gender ->
                    viewModel.registerNewDonor(name, bg, phone, email, area, age, weight, hb, gender)
                    showRegisterSheet = false
                }
            )
        }
    }
}

@Composable
fun DonorProfileDetailDialog(
    donor: Donor,
    onDismiss: () -> Unit,
    onToggleAvailability: () -> Unit
) {
    val context = LocalContext.current
    val eligibility = EligibilityHelper.checkEligibility(
        lastDonationTimestamp = donor.lastDonationDate,
        age = donor.age,
        weightKg = donor.weightKg,
        hemoglobin = donor.hemoglobin,
        isIdVerified = donor.isIdVerified,
        isMedicalClearance = donor.isMedicalClearance
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
                    context.startActivity(dialIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call Donor")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 46.dp, fontSize = 16)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = donor.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(
                            text = donor.area,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                VerificationBadge(badgeType = donor.verificationBadge, isIdVerified = donor.isIdVerified)

                Spacer(modifier = Modifier.height(14.dp))

                // Vital Stats Grid
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        ProfileStatItem(title = "Age", value = "${donor.age} yrs")
                        ProfileStatItem(title = "Weight", value = "${donor.weightKg.toInt()} kg")
                        ProfileStatItem(title = "Hb Level", value = "${donor.hemoglobin} g/dL")
                        ProfileStatItem(title = "Donations", value = "${donor.totalDonations}")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cooldown and Eligibility Status
                Text("Recovery & Cooldown Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                if (eligibility.cooldownPassed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Eligible to donate today (3-month rest complete)",
                            fontSize = 12.sp,
                            color = VerifiedGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = UrgentAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Resting: ${eligibility.daysRemainingCooldown} days remaining until next donation",
                                fontSize = 12.sp,
                                color = UrgentAmber,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val progress = ((90 - eligibility.daysRemainingCooldown).toFloat() / 90f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            color = UrgentAmber,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification Badges & Credentials
                Text("Verification Record:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                VerificationCheckItem("National ID / Student ID Verified", donor.isIdVerified)
                VerificationCheckItem("Hospital Medical Clearance Test", donor.isMedicalClearance)
                VerificationCheckItem("Sahiddirgonj Blood Group Volunteer", donor.isEmergencyVolunteer)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Contact: ${donor.phone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ProfileStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun VerificationCheckItem(title: String, isVerified: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Shield,
            contentDescription = null,
            tint = if (isVerified) VerifiedGreen else Color.Gray,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = if (isVerified) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDonorBottomSheet(
    onDismiss: () -> Unit,
    onRegister: (String, String, String, String, String, Int, Float, Float, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("Hospital Road, Sahiddirgonj") }
    var ageStr by remember { mutableStateOf("25") }
    var weightStr by remember { mutableStateOf("65") }
    var hemoglobinStr by remember { mutableStateOf("13.5") }
    var gender by remember { mutableStateOf("Male") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Join as a Verified Donor",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = BloodRed
            )
            Text(
                text = "Become part of Sahiddirgonj Blood Donner's Group",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_donor_name")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Blood Group Selector
            Text("Select Blood Group:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(bloodGroups) { bg ->
                    FilterChip(
                        selected = bloodGroup == bg,
                        onClick = { bloodGroup = bg },
                        label = { Text(bg, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BloodRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Phone") },
                    placeholder = { Text("+8801...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area in Sahiddirgonj") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text("Age (yrs)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = weightStr,
                    onValueChange = { weightStr = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hemoglobinStr,
                    onValueChange = { hemoglobinStr = it },
                    label = { Text("Hb (g/dL)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        val age = ageStr.toIntOrNull() ?: 25
                        val weight = weightStr.toFloatOrNull() ?: 60f
                        val hb = hemoglobinStr.toFloatOrNull() ?: 13.5f
                        onRegister(name, bloodGroup, phone, email, area, age, weight, hb, gender)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_donor_register_button")
            ) {
                Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Complete Registration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
