package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BloodRequest
import com.example.data.model.Donor
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.UrgentAmber
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.BloodViewModel
import com.example.util.EligibilityHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: BloodViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val donors by viewModel.donors.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()

    if (!isAdminLoggedIn) {
        AdminLoginView(
            onLogin = { email, pass -> viewModel.adminLogin(email, pass) },
            onBackClick = onBackClick,
            modifier = modifier
        )
    } else {
        AdminDashboardView(
            viewModel = viewModel,
            donors = donors,
            requests = requests,
            onLogout = { viewModel.adminLogout() },
            modifier = modifier
        )
    }
}

@Composable
fun AdminLoginView(
    onLogin: (String, String) -> Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("admin@sahiddirgonj.org") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("admin_login_screen")
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Surface(
                    color = BloodRed.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Security",
                        tint = BloodRed,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Administrator Portal",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sahiddirgonj Blood Donner's Group",
                    fontSize = 13.sp,
                    color = BloodRed,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Admin Email / Username") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = BloodRed) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password or Secret PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BloodRed) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = BloodRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // One-tap quick demo login chip
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "Demo Credentials: admin@sahiddirgonj.org / admin123",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Button(
                    onClick = {
                        val success = onLogin(email, password)
                        if (!success) {
                            errorMessage = "Invalid admin credentials. Please try again."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Admin Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Public App")
                }
            }
        }
    }
}

@Composable
fun AdminDashboardView(
    viewModel: BloodViewModel,
    donors: List<Donor>,
    requests: List<BloodRequest>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Verify Donors, 1: Verify Blood Requests

    val pendingDonors = remember(donors) {
        donors.filter { it.verificationBadge == "PENDING_VERIFICATION" || !it.isIdVerified }
    }
    val pendingRequests = remember(requests) {
        requests.filter { !it.isAdminVerified }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_view")
    ) {
        // Admin Header
        Surface(
            color = BloodRedDark,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Control Console",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Sahiddirgonj Blood Donner's Group",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Logout Button
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Stats Overview
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AdminStatBadge(
                        title = "Pending Donors",
                        count = "${pendingDonors.size}",
                        isAlert = pendingDonors.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatBadge(
                        title = "Pending Requests",
                        count = "${pendingRequests.size}",
                        isAlert = pendingRequests.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatBadge(
                        title = "Total Donors",
                        count = "${donors.size}",
                        isAlert = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BloodRed
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Verify Donors", fontWeight = FontWeight.Bold)
                        if (pendingDonors.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = BloodRed,
                                shape = CircleShape,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${pendingDonors.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("admin_tab_donors")
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Verify Requests", fontWeight = FontWeight.Bold)
                        if (pendingRequests.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = UrgentAmber,
                                shape = CircleShape,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${pendingRequests.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("admin_tab_requests")
            )
        }

        // Tab Content
        if (selectedTab == 0) {
            AdminVerifyDonorsList(
                donors = donors,
                onVerifyHero = { id -> viewModel.adminVerifyDonor(id, "VERIFIED_HERO") },
                onUpgradeGold = { id -> viewModel.adminVerifyDonor(id, "GOLD_DONOR") },
                onReject = { id -> viewModel.adminRejectDonor(id) },
                onDelete = { id -> viewModel.adminDeleteDonor(id) }
            )
        } else {
            AdminVerifyRequestsList(
                requests = requests,
                onVerify = { id, verified -> viewModel.adminVerifyBloodRequest(id, verified) },
                onBroadcast = { req ->
                    viewModel.triggerTestEmergencyNotification()
                },
                onDelete = { id -> viewModel.adminDeleteRequest(id) }
            )
        }
    }
}

@Composable
fun AdminStatBadge(
    title: String,
    count: String,
    isAlert: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isAlert) Color.White else Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = count,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = if (isAlert) BloodRedDark else Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAlert) BloodRedDark else Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AdminVerifyDonorsList(
    donors: List<Donor>,
    onVerifyHero: (String) -> Unit,
    onUpgradeGold: (String) -> Unit,
    onReject: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val context = LocalContext.current
    var filterType by remember { mutableStateOf("PENDING") } // "PENDING", "ALL", "VERIFIED"

    val displayDonors = remember(donors, filterType) {
        when (filterType) {
            "PENDING" -> donors.filter { it.verificationBadge == "PENDING_VERIFICATION" || !it.isIdVerified }
            "VERIFIED" -> donors.filter { it.isIdVerified }
            else -> donors
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == "PENDING",
                onClick = { filterType = "PENDING" },
                label = { Text("Pending Review (${donors.count { it.verificationBadge == "PENDING_VERIFICATION" || !it.isIdVerified }})", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = filterType == "ALL",
                onClick = { filterType = "ALL" },
                label = { Text("All (${donors.size})", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = filterType == "VERIFIED",
                onClick = { filterType = "VERIFIED" },
                label = { Text("Verified", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
        }

        if (displayDonors.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = "No donors found under '$filterType'. All donor profiles are verified and in order!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayDonors) { donor ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_donor_item_${donor.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 44.dp, fontSize = 15)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = donor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(
                                            text = "${donor.area} • Phone: ${donor.phone}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                VerificationBadge(badgeType = donor.verificationBadge, isIdVerified = donor.isIdVerified)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Medical verification criteria summary
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text("Age: ${donor.age} yrs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Weight: ${donor.weightKg.toInt()} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Hb: ${donor.hemoglobin} g/dL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Total: ${donor.totalDonations}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons for Admin
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onVerifyHero(donor.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_verify_hero_${donor.id}")
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verify Hero ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onUpgradeGold(donor.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = UrgentAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gold Donor ⭐", fontSize = 12.sp, color = UrgentAmber, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { onDelete(donor.id) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BloodRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminVerifyRequestsList(
    requests: List<BloodRequest>,
    onVerify: (String, Boolean) -> Unit,
    onBroadcast: (BloodRequest) -> Unit,
    onDelete: (String) -> Unit
) {
    val context = LocalContext.current
    var filterType by remember { mutableStateOf("PENDING") } // "PENDING", "ALL", "VERIFIED"

    val displayRequests = remember(requests, filterType) {
        when (filterType) {
            "PENDING" -> requests.filter { !it.isAdminVerified }
            "VERIFIED" -> requests.filter { it.isAdminVerified }
            else -> requests
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == "PENDING",
                onClick = { filterType = "PENDING" },
                label = { Text("Pending (${requests.count { !it.isAdminVerified }})", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = filterType == "ALL",
                onClick = { filterType = "ALL" },
                label = { Text("All (${requests.size})", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = filterType == "VERIFIED",
                onClick = { filterType = "VERIFIED" },
                label = { Text("Hospital Verified", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BloodRed, selectedLabelColor = Color.White)
            )
        }

        if (displayRequests.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = "No blood requests found under '$filterType'.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayRequests) { req ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_request_item_${req.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BloodGroupBadge(bloodGroup = req.bloodGroup, size = 44.dp, fontSize = 15)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = req.patientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(
                                            text = "${req.hospital} • ${req.units} Unit(s)",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    color = if (req.isAdminVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (req.isAdminVerified) "Hospital Verified ✓" else "Needs Verification ⚠️",
                                        color = if (req.isAdminVerified) VerifiedGreen else UrgentAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Contact: ${req.contactName} (${req.contactPhone})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (req.additionalNote.isNotBlank()) {
                                Text(
                                    text = "Notes: ${req.additionalNote}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Admin Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onVerify(req.id, !req.isAdminVerified) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (req.isAdminVerified) UrgentAmber else VerifiedGreen
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_verify_request_button_${req.id}")
                                ) {
                                    Icon(
                                        imageVector = if (req.isAdminVerified) Icons.Default.Close else Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (req.isAdminVerified) "Revoke" else "Approve & Verify",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { onBroadcast(req) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Broadcast Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { onDelete(req.id) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BloodRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
