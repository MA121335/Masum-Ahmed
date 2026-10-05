package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Donor
import com.example.ui.components.BloodGroupBadge
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.DonorProfileDetailDialog
import com.example.ui.screens.DonorsDirectoryScreen
import com.example.ui.screens.EmergencyRequestsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MapSearchScreen
import com.example.ui.screens.PostEmergencyRequestBottomSheet
import com.example.ui.screens.VerificationScreen
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.SahiddirgonjBloodTheme
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.BloodViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BloodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SahiddirgonjBloodTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: BloodViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedDonorForDialog by remember { mutableStateOf<Donor?>(null) }
    var showRequestSheetFromFab by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.showToast("Emergency notification alerts enabled")
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToastMessage()
        }
    }

    // Hardware back button navigation: Return to Home screen if not already on Home
    BackHandler(enabled = currentTab != AppNavTab.HOME) {
        viewModel.selectTab(AppNavTab.HOME)
    }

    val openRequestsCount = remember(requests) { requests.count { it.status == "OPEN" } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BloodRed,
                            shape = CircleShape,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "SBG",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sahiddirgonj Blood",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Donner's Group",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BloodRed
                            )
                        }
                    }
                },
                actions = {
                    // Quick Helpline dial button
                    IconButton(
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+8801712345671"))
                            context.startActivity(dialIntent)
                        },
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .testTag("top_helpline_button")
                    ) {
                        Surface(
                            color = BloodRed.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call Emergency Helpline",
                                tint = BloodRed,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                    }

                    // Admin Portal Button
                    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
                    IconButton(
                        onClick = { viewModel.selectTab(AppNavTab.ADMIN) },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("top_admin_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (isAdminLoggedIn) {
                                    Badge(containerColor = VerifiedGreen) {
                                        Text("✓", color = Color.White, fontSize = 8.sp)
                                    }
                                }
                            }
                        ) {
                            Surface(
                                color = if (currentTab == AppNavTab.ADMIN) BloodRed else BloodRed.copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Portal",
                                    tint = if (currentTab == AppNavTab.ADMIN) Color.White else BloodRed,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                    }

                    // User Blood Group Badge at top-right
                    BloodGroupBadge(
                        bloodGroup = userProfile.bloodGroup,
                        size = 36.dp,
                        fontSize = 13,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.selectTab(AppNavTab.VERIFY) }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                // Home
                NavigationBarItem(
                    selected = currentTab == AppNavTab.HOME,
                    onClick = { viewModel.selectTab(AppNavTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                // Donor Radar Map
                NavigationBarItem(
                    selected = currentTab == AppNavTab.MAP,
                    onClick = { viewModel.selectTab(AppNavTab.MAP) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavTab.MAP) Icons.Filled.LocationSearching else Icons.Outlined.LocationSearching,
                            contentDescription = "Donor Radar"
                        )
                    },
                    label = { Text("Radar", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_radar")
                )

                // Donors Directory
                NavigationBarItem(
                    selected = currentTab == AppNavTab.DONORS,
                    onClick = { viewModel.selectTab(AppNavTab.DONORS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavTab.DONORS) Icons.Filled.People else Icons.Outlined.People,
                            contentDescription = "Directory"
                        )
                    },
                    label = { Text("Donors", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_donors")
                )

                // Emergency Requests
                NavigationBarItem(
                    selected = currentTab == AppNavTab.REQUESTS,
                    onClick = { viewModel.selectTab(AppNavTab.REQUESTS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (openRequestsCount > 0) {
                                    Badge(containerColor = BloodRed) {
                                        Text("$openRequestsCount", color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == AppNavTab.REQUESTS) Icons.Filled.Emergency else Icons.Outlined.Emergency,
                                contentDescription = "Emergency"
                            )
                        }
                    },
                    label = { Text("Urgent", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_requests")
                )

                // Verification
                NavigationBarItem(
                    selected = currentTab == AppNavTab.VERIFY,
                    onClick = { viewModel.selectTab(AppNavTab.VERIFY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavTab.VERIFY) Icons.Filled.Shield else Icons.Outlined.Shield,
                            contentDescription = "Verification"
                        )
                    },
                    label = { Text("Verify", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_verify")
                )

                // History
                NavigationBarItem(
                    selected = currentTab == AppNavTab.HISTORY,
                    onClick = { viewModel.selectTab(AppNavTab.HISTORY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRed,
                        selectedTextColor = BloodRed,
                        indicatorColor = BloodRed.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_history")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    AppNavTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateTab = { viewModel.selectTab(it) },
                        onRequestBloodClick = { showRequestSheetFromFab = true },
                        onDonorClick = { selectedDonorForDialog = it }
                    )

                    AppNavTab.MAP -> MapSearchScreen(
                        viewModel = viewModel,
                        onDonorDetailClick = { selectedDonorForDialog = it }
                    )

                    AppNavTab.DONORS -> DonorsDirectoryScreen(
                        viewModel = viewModel
                    )

                    AppNavTab.REQUESTS -> EmergencyRequestsScreen(
                        viewModel = viewModel
                    )

                    AppNavTab.VERIFY -> VerificationScreen(
                        viewModel = viewModel
                    )

                    AppNavTab.HISTORY -> HistoryScreen(
                        viewModel = viewModel
                    )

                    AppNavTab.ADMIN -> AdminPanelScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.selectTab(AppNavTab.HOME) }
                    )
                }
            }
        }

        // Donor details dialog
        selectedDonorForDialog?.let { donor ->
            DonorProfileDetailDialog(
                donor = donor,
                onDismiss = { selectedDonorForDialog = null },
                onToggleAvailability = {
                    viewModel.toggleDonorAvailability(donor.id, donor.isAvailable)
                    selectedDonorForDialog = donor.copy(isAvailable = !donor.isAvailable)
                }
            )
        }

        // Quick Post Emergency Request Sheet from Home button
        if (showRequestSheetFromFab) {
            PostEmergencyRequestBottomSheet(
                onDismiss = { showRequestSheetFromFab = false },
                onSubmit = { pName, bg, units, hosp, area, cName, cPhone, urg, note ->
                    viewModel.postEmergencyRequest(
                        patientName = pName,
                        bloodGroup = bg,
                        units = units,
                        hospital = hosp,
                        area = area,
                        contactName = cName,
                        contactPhone = cPhone,
                        urgency = urg,
                        additionalNote = note
                    )
                    showRequestSheetFromFab = false
                }
            )
        }
    }
}
