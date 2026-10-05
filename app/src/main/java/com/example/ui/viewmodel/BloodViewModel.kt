package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BloodDatabase
import com.example.data.model.BloodRequest
import com.example.data.model.DonationHistory
import com.example.data.model.Donor
import com.example.data.repository.BloodRepository
import com.example.util.EligibilityHelper
import com.example.util.EligibilityResult
import com.example.util.LocationUtils
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppNavTab(val title: String) {
    HOME("Home"),
    MAP("Donor Radar"),
    DONORS("Directory"),
    REQUESTS("Emergency"),
    VERIFY("Verification"),
    HISTORY("History"),
    ADMIN("Admin Panel")
}

data class UserProfileState(
    val name: String = "Sajid Hasan",
    val bloodGroup: String = "O+",
    val phone: String = "+8801700112233",
    val area: String = "Hospital Road, Sahiddirgonj",
    val age: Int = 26,
    val weightKg: Float = 68.0f,
    val hemoglobin: Float = 14.0f,
    val lastDonationTimestamp: Long = System.currentTimeMillis() - (98L * 24 * 60 * 60 * 1000), // 98 days ago
    val totalDonations: Int = 3,
    val isIdVerified: Boolean = true,
    val isMedicalClearance: Boolean = true,
    val isAvailable: Boolean = true
)

class BloodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BloodRepository

    init {
        val db = BloodDatabase.getInstance(application)
        repository = BloodRepository(db)
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }
        NotificationHelper.createNotificationChannel(application)
    }

    val donors: StateFlow<List<Donor>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requests: StateFlow<List<BloodRequest>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<DonationHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(AppNavTab.HOME)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _selectedBloodGroupFilter = MutableStateFlow("ALL")
    val selectedBloodGroupFilter: StateFlow<String> = _selectedBloodGroupFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _mapRadiusKm = MutableStateFlow(5.0f) // default 5 km
    val mapRadiusKm: StateFlow<Float> = _mapRadiusKm.asStateFlow()

    private val _onlyAvailableOnMap = MutableStateFlow(false)
    val onlyAvailableOnMap: StateFlow<Boolean> = _onlyAvailableOnMap.asStateFlow()

    private val _selectedMapDonor = MutableStateFlow<Donor?>(null)
    val selectedMapDonor: StateFlow<Donor?> = _selectedMapDonor.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileState())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    private val _userEligibility = MutableStateFlow(calculateCurrentEligibility(UserProfileState()))
    val userEligibility: StateFlow<EligibilityResult> = _userEligibility.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filtered Donors for Directory List
    val filteredDonors: StateFlow<List<Donor>> = combine(
        donors,
        _selectedBloodGroupFilter,
        _searchQuery
    ) { donorList, bgFilter, query ->
        donorList.filter { donor ->
            val matchesBg = (bgFilter == "ALL" || donor.bloodGroup.equals(bgFilter, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    donor.name.contains(query, ignoreCase = true) ||
                    donor.area.contains(query, ignoreCase = true) ||
                    donor.bloodGroup.contains(query, ignoreCase = true)
            matchesBg && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Donors on Map based on Radius and Blood Filter
    val mapDonors: StateFlow<List<Donor>> = combine(
        donors,
        _selectedBloodGroupFilter,
        _mapRadiusKm,
        _onlyAvailableOnMap
    ) { donorList, bgFilter, radius, onlyAvailable ->
        donorList.filter { donor ->
            val dist = LocationUtils.calculateDistanceKm(
                LocationUtils.SAHIDDIRGONJ_CENTER_LAT,
                LocationUtils.SAHIDDIRGONJ_CENTER_LNG,
                donor.latitude,
                donor.longitude
            )
            val withinRadius = dist <= radius
            val matchesBg = (bgFilter == "ALL" || donor.bloodGroup.equals(bgFilter, ignoreCase = true))
            val matchesAvailability = !onlyAvailable || donor.isAvailable
            withinRadius && matchesBg && matchesAvailability
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    fun setBloodGroupFilter(group: String) {
        _selectedBloodGroupFilter.value = group
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMapRadius(radius: Float) {
        _mapRadiusKm.value = radius
    }

    fun toggleOnlyAvailableOnMap(enabled: Boolean) {
        _onlyAvailableOnMap.value = enabled
    }

    fun selectMapDonor(donor: Donor?) {
        _selectedMapDonor.value = donor
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun postEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        units: Int,
        hospital: String,
        area: String,
        contactName: String,
        contactPhone: String,
        urgency: String,
        additionalNote: String
    ) {
        viewModelScope.launch {
            val reqId = "req_" + UUID.randomUUID().toString().take(8)
            val newRequest = BloodRequest(
                id = reqId,
                patientName = patientName.trim(),
                bloodGroup = bloodGroup,
                units = units,
                hospital = hospital.trim(),
                area = area.ifBlank { "Sahiddirgonj Hospital Area" }.trim(),
                latitude = LocationUtils.SAHIDDIRGONJ_CENTER_LAT + ((Math.random() - 0.5) * 0.015),
                longitude = LocationUtils.SAHIDDIRGONJ_CENTER_LNG + ((Math.random() - 0.5) * 0.015),
                urgency = urgency,
                contactName = contactName.trim(),
                contactPhone = contactPhone.trim(),
                createdAt = System.currentTimeMillis(),
                status = "OPEN",
                additionalNote = additionalNote.trim(),
                responsesCount = 0
            )

            repository.insertRequest(newRequest)

            // Trigger Real Android Notification
            NotificationHelper.sendEmergencyAlert(
                context = getApplication(),
                notificationId = System.currentTimeMillis().toInt(),
                bloodGroup = bloodGroup,
                hospital = hospital,
                patientName = patientName,
                units = units
            )

            _toastMessage.value = "🚨 Emergency alert broadcasted across Sahiddirgonj!"
        }
    }

    fun markRequestFulfilled(requestId: String) {
        viewModelScope.launch {
            repository.markRequestFulfilled(requestId)
            _toastMessage.value = "Request marked as fulfilled. Thank you for saving lives!"
        }
    }

    fun registerNewDonor(
        name: String,
        bloodGroup: String,
        phone: String,
        email: String,
        area: String,
        age: Int,
        weight: Float,
        hemoglobin: Float,
        gender: String
    ) {
        viewModelScope.launch {
            val donorId = "donor_" + UUID.randomUUID().toString().take(8)
            val newDonor = Donor(
                id = donorId,
                name = name.trim(),
                bloodGroup = bloodGroup,
                phone = phone.trim(),
                email = email.trim(),
                area = area.trim(),
                latitude = LocationUtils.SAHIDDIRGONJ_CENTER_LAT + ((Math.random() - 0.5) * 0.02),
                longitude = LocationUtils.SAHIDDIRGONJ_CENTER_LNG + ((Math.random() - 0.5) * 0.02),
                isAvailable = true,
                lastDonationDate = 0L,
                totalDonations = 0,
                weightKg = weight,
                age = age,
                hemoglobin = hemoglobin,
                verificationBadge = "VERIFIED_HERO",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = gender,
                isEmergencyVolunteer = true
            )

            repository.insertDonor(newDonor)
            _toastMessage.value = "Welcome to Sahiddirgonj Blood Donors Group, $name!"
        }
    }

    fun toggleDonorAvailability(donorId: String, currentAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateDonorAvailability(donorId, !currentAvailable)
            _toastMessage.value = if (!currentAvailable) "Status: Available to Donate" else "Status: Temporarily Paused"
        }
    }

    fun recordDonation(hospital: String, units: Int, condition: String) {
        viewModelScope.launch {
            val user = _userProfile.value
            repository.insertDonationHistory(
                donorName = user.name,
                bloodGroup = user.bloodGroup,
                hospital = hospital,
                units = units,
                condition = condition
            )

            // Update user profile state
            _userProfile.update {
                it.copy(
                    lastDonationTimestamp = System.currentTimeMillis(),
                    totalDonations = it.totalDonations + units
                )
            }
            _userEligibility.value = calculateCurrentEligibility(_userProfile.value)
            _toastMessage.value = "🎉 Donation recorded! Official Certificate generated."
        }
    }

    fun updateSelfProfile(
        name: String,
        bloodGroup: String,
        age: Int,
        weight: Float,
        hemoglobin: Float,
        lastDonationDateMillis: Long
    ) {
        _userProfile.update {
            it.copy(
                name = name,
                bloodGroup = bloodGroup,
                age = age,
                weightKg = weight,
                hemoglobin = hemoglobin,
                lastDonationTimestamp = lastDonationDateMillis
            )
        }
        _userEligibility.value = calculateCurrentEligibility(_userProfile.value)
        _toastMessage.value = "Profile and verification parameters updated."
    }

    fun triggerTestEmergencyNotification() {
        NotificationHelper.sendEmergencyAlert(
            context = getApplication(),
            notificationId = 101,
            bloodGroup = "O+",
            hospital = "Sahiddirgonj Central Hospital",
            patientName = "Emergency Surgery (Bed 4)",
            units = 2
        )
        _toastMessage.value = "Notification sent to system status bar!"
    }

    // --- Admin Panel & Verification Methods ---
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    fun adminLogin(emailOrPin: String, passwordOrPin: String): Boolean {
        val cleanUser = emailOrPin.trim()
        val cleanPass = passwordOrPin.trim()
        val isValid = (cleanUser.equals("admin@sahiddirgonj.org", ignoreCase = true) && cleanPass == "admin123") ||
                (cleanUser.equals("admin", ignoreCase = true) && cleanPass == "admin") ||
                (cleanUser == "1234" || cleanPass == "1234")
        if (isValid) {
            _isAdminLoggedIn.value = true
            _toastMessage.value = "Admin Login Successful! Welcome to Sahiddirgonj Admin Console."
        }
        return isValid
    }

    fun adminLogout() {
        _isAdminLoggedIn.value = false
        _toastMessage.value = "Admin logged out."
    }

    fun adminVerifyDonor(donorId: String, badge: String = "VERIFIED_HERO") {
        viewModelScope.launch {
            repository.verifyDonor(donorId, badge, isIdVerified = true, isMedicalClearance = true)
            _toastMessage.value = "✓ Donor verified as $badge!"
        }
    }

    fun adminRejectDonor(donorId: String) {
        viewModelScope.launch {
            repository.verifyDonor(donorId, "NOT_VERIFIED", isIdVerified = false, isMedicalClearance = false)
            _toastMessage.value = "Donor marked unverified."
        }
    }

    fun adminDeleteDonor(donorId: String) {
        viewModelScope.launch {
            repository.deleteDonor(donorId)
            _toastMessage.value = "Donor record deleted."
        }
    }

    fun adminVerifyBloodRequest(requestId: String, isVerified: Boolean) {
        viewModelScope.launch {
            repository.verifyRequest(requestId, isVerified)
            _toastMessage.value = if (isVerified) "✓ Blood request verified by Hospital Desk!" else "Request verification revoked."
        }
    }

    fun adminDeleteRequest(requestId: String) {
        viewModelScope.launch {
            repository.deleteRequest(requestId)
            _toastMessage.value = "Blood request removed."
        }
    }

    private companion object {
        fun calculateCurrentEligibility(user: UserProfileState): EligibilityResult {
            return EligibilityHelper.checkEligibility(
                lastDonationTimestamp = user.lastDonationTimestamp,
                age = user.age,
                weightKg = user.weightKg,
                hemoglobin = user.hemoglobin,
                isIdVerified = user.isIdVerified,
                isMedicalClearance = user.isMedicalClearance
            )
        }
    }
}
