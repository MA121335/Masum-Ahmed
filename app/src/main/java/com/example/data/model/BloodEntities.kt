package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey
    val id: String,
    val name: String,
    val bloodGroup: String, // "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    val phone: String,
    val email: String,
    val area: String,
    val latitude: Double,
    val longitude: Double,
    val isAvailable: Boolean = true,
    val lastDonationDate: Long, // timestamp millis
    val totalDonations: Int = 1,
    val weightKg: Float = 62.0f,
    val age: Int = 26,
    val hemoglobin: Float = 13.5f,
    val verificationBadge: String = "VERIFIED_HERO", // "VERIFIED_HERO", "GOLD_DONOR", "LIFE_SAVER", "BASIC"
    val isIdVerified: Boolean = true,
    val isMedicalClearance: Boolean = true,
    val gender: String = "Male",
    val isEmergencyVolunteer: Boolean = true
)

@Entity(tableName = "blood_requests")
data class BloodRequest(
    @PrimaryKey
    val id: String,
    val patientName: String,
    val bloodGroup: String,
    val units: Int,
    val hospital: String,
    val area: String,
    val latitude: Double,
    val longitude: Double,
    val urgency: String = "EMERGENCY_NOW", // "EMERGENCY_NOW", "CRITICAL_TODAY", "STANDARD"
    val contactName: String,
    val contactPhone: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "OPEN", // "OPEN", "FULFILLED"
    val additionalNote: String = "",
    val responsesCount: Int = 0,
    val isAdminVerified: Boolean = false
)

@Entity(tableName = "donation_history")
data class DonationHistory(
    @PrimaryKey
    val id: String,
    val donorId: String,
    val donorName: String,
    val bloodGroup: String,
    val recipientOrHospital: String,
    val donationDate: Long,
    val units: Int = 1,
    val certificateNumber: String,
    val patientCondition: String = "Emergency Surgery Support",
    val verifiedByHospital: Boolean = true
)
