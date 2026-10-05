package com.example.util

import com.example.data.model.BloodRequest
import com.example.data.model.DonationHistory
import com.example.data.model.Donor
import java.util.concurrent.TimeUnit

object SampleDataGenerator {

    fun getInitialDonors(): List<Donor> {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        return listOf(
            Donor(
                id = "donor_1",
                name = "Tanvir Ahmed",
                bloodGroup = "O+",
                phone = "+8801712345671",
                email = "tanvir.sahiddirgonj@gmail.com",
                area = "Hospital Road, Sahiddirgonj",
                latitude = 24.0165,
                longitude = 90.4140,
                isAvailable = true,
                lastDonationDate = now - (98 * day), // 98 days ago -> eligible!
                totalDonations = 7,
                weightKg = 68.5f,
                age = 27,
                hemoglobin = 14.2f,
                verificationBadge = "GOLD_DONOR",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Male"
            ),
            Donor(
                id = "donor_2",
                name = "Dr. Sabrina Karim",
                bloodGroup = "A+",
                phone = "+8801819876542",
                email = "sabrina.dr@gmail.com",
                area = "College Mor, Sahiddirgonj",
                latitude = 24.0190,
                longitude = 90.4105,
                isAvailable = true,
                lastDonationDate = now - (115 * day), // eligible
                totalDonations = 12,
                weightKg = 56.0f,
                age = 32,
                hemoglobin = 13.0f,
                verificationBadge = "LIFE_SAVER",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Female"
            ),
            Donor(
                id = "donor_3",
                name = "Md. Rafiqul Islam",
                bloodGroup = "B+",
                phone = "+8801911223344",
                email = "rafiq.islam@gmail.com",
                area = "Station Road, Sahiddirgonj",
                latitude = 24.0120,
                longitude = 90.4180,
                isAvailable = false,
                lastDonationDate = now - (24 * day), // 24 days ago -> In cooldown (66 days remaining)
                totalDonations = 4,
                weightKg = 72.0f,
                age = 29,
                hemoglobin = 14.8f,
                verificationBadge = "VERIFIED_HERO",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Male"
            ),
            Donor(
                id = "donor_4",
                name = "Farhana Akter",
                bloodGroup = "O-", // Rare universal donor!
                phone = "+8801614567890",
                email = "farhana.akter@gmail.com",
                area = "Riverview Para, Sahiddirgonj",
                latitude = 24.0220,
                longitude = 90.4150,
                isAvailable = true,
                lastDonationDate = now - (140 * day),
                totalDonations = 6,
                weightKg = 54.5f,
                age = 24,
                hemoglobin = 12.8f,
                verificationBadge = "GOLD_DONOR",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Female"
            ),
            Donor(
                id = "donor_5",
                name = "Shahriar Hasan",
                bloodGroup = "AB+",
                phone = "+8801755667788",
                email = "shahriar.hasan@gmail.com",
                area = "East Bazar, Sahiddirgonj",
                latitude = 24.0110,
                longitude = 90.4070,
                isAvailable = true,
                lastDonationDate = now - (92 * day),
                totalDonations = 8,
                weightKg = 65.0f,
                age = 26,
                hemoglobin = 13.9f,
                verificationBadge = "GOLD_DONOR",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Male"
            ),
            Donor(
                id = "donor_6",
                name = "Nahid Rahman",
                bloodGroup = "A-",
                phone = "+8801823344556",
                email = "nahid.rahman@gmail.com",
                area = "Court Road, Sahiddirgonj",
                latitude = 24.0185,
                longitude = 90.4195,
                isAvailable = false,
                lastDonationDate = now - (40 * day), // In cooldown
                totalDonations = 3,
                weightKg = 61.0f,
                age = 25,
                hemoglobin = 13.4f,
                verificationBadge = "VERIFIED_HERO",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Male"
            ),
            Donor(
                id = "donor_7",
                name = "Kazi Mahinur",
                bloodGroup = "B-",
                phone = "+8801934567891",
                email = "mahinur.kazi@gmail.com",
                area = "South Bypass, Sahiddirgonj",
                latitude = 24.0090,
                longitude = 90.4130,
                isAvailable = true,
                lastDonationDate = now - (105 * day),
                totalDonations = 5,
                weightKg = 59.0f,
                age = 30,
                hemoglobin = 13.1f,
                verificationBadge = "GOLD_DONOR",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Female"
            ),
            Donor(
                id = "donor_8",
                name = "Tariqul Anam",
                bloodGroup = "AB-", // Ultra-rare
                phone = "+8801744556677",
                email = "tariqul.anam@gmail.com",
                area = "Central Mosque Road, Sahiddirgonj",
                latitude = 24.0145,
                longitude = 90.4160,
                isAvailable = true,
                lastDonationDate = now - (160 * day),
                totalDonations = 4,
                weightKg = 70.0f,
                age = 28,
                hemoglobin = 14.5f,
                verificationBadge = "PENDING_VERIFICATION",
                isIdVerified = false,
                isMedicalClearance = false,
                gender = "Male"
            ),
            Donor(
                id = "donor_9",
                name = "Sajjad Hossain",
                bloodGroup = "O+",
                phone = "+8801555667788",
                email = "sajjad.h@gmail.com",
                area = "Hospital Road, Sahiddirgonj",
                latitude = 24.0170,
                longitude = 90.4110,
                isAvailable = true,
                lastDonationDate = now - (95 * day),
                totalDonations = 9,
                weightKg = 67.0f,
                age = 31,
                hemoglobin = 14.1f,
                verificationBadge = "GOLD_DONOR",
                isIdVerified = true,
                isMedicalClearance = true,
                gender = "Male"
            ),
            Donor(
                id = "donor_10",
                name = "Nusrat Jahan",
                bloodGroup = "B+",
                phone = "+8801688990011",
                email = "nusrat.jahan@gmail.com",
                area = "Shanti Nagar, Sahiddirgonj",
                latitude = 24.0205,
                longitude = 90.4085,
                isAvailable = true,
                lastDonationDate = now - (120 * day),
                totalDonations = 5,
                weightKg = 53.0f,
                age = 23,
                hemoglobin = 12.9f,
                verificationBadge = "PENDING_VERIFICATION",
                isIdVerified = false,
                isMedicalClearance = false,
                gender = "Female"
            )
        )
    }

    fun getInitialRequests(): List<BloodRequest> {
        val now = System.currentTimeMillis()
        val hour = TimeUnit.HOURS.toMillis(1)

        return listOf(
            BloodRequest(
                id = "req_1",
                patientName = "Arman Ali (ICU Bed 04)",
                bloodGroup = "O+",
                units = 2,
                hospital = "Sahiddirgonj Central Hospital",
                area = "Hospital Road, Sahiddirgonj",
                latitude = 24.0165,
                longitude = 90.4140,
                urgency = "EMERGENCY_NOW",
                contactName = "Kamrul Islam (Brother)",
                contactPhone = "+8801711002233",
                createdAt = now - (1 * hour),
                status = "OPEN",
                additionalNote = "Road accident trauma surgery, urgent negative or positive cross-match ready.",
                responsesCount = 3,
                isAdminVerified = true
            ),
            BloodRequest(
                id = "req_2",
                patientName = "Rokeya Begum (Maternity Ward)",
                bloodGroup = "B+",
                units = 1,
                hospital = "Upazila Health Complex",
                area = "College Mor, Sahiddirgonj",
                latitude = 24.0190,
                longitude = 90.4105,
                urgency = "EMERGENCY_NOW",
                contactName = "Jahangir Alam (Husband)",
                contactPhone = "+8801819554433",
                createdAt = now - (3 * hour),
                status = "OPEN",
                additionalNote = "Emergency Cesarean delivery scheduled tonight. Immediate donor requested.",
                responsesCount = 2,
                isAdminVerified = true
            ),
            BloodRequest(
                id = "req_3",
                patientName = "Little Sourav (Thalassemia)",
                bloodGroup = "A+",
                units = 1,
                hospital = "LifeLine Care Hospital",
                area = "Riverview Avenue, Sahiddirgonj",
                latitude = 24.0220,
                longitude = 90.4150,
                urgency = "CRITICAL_TODAY",
                contactName = "Shirin Sultana (Mother)",
                contactPhone = "+8801912998877",
                createdAt = now - (7 * hour),
                status = "OPEN",
                additionalNote = "Routine monthly blood transfusion required for 8-year-old child.",
                responsesCount = 1,
                isAdminVerified = false
            ),
            BloodRequest(
                id = "req_4",
                patientName = "Abdur Rashid",
                bloodGroup = "O-",
                units = 2,
                hospital = "Sahiddirgonj Red Crescent Clinic",
                area = "Station Road, Sahiddirgonj",
                latitude = 24.0120,
                longitude = 90.4180,
                urgency = "EMERGENCY_NOW",
                contactName = "Habibur Rahman (Son)",
                contactPhone = "+8801611776655",
                createdAt = now - (12 * hour),
                status = "OPEN",
                additionalNote = "Rare O negative blood needed for cardiac surgery tomorrow morning.",
                responsesCount = 0,
                isAdminVerified = false
            )
        )
    }

    fun getInitialHistory(): List<DonationHistory> {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        return listOf(
            DonationHistory(
                id = "hist_1",
                donorId = "user_me",
                donorName = "You (Community Hero)",
                bloodGroup = "O+",
                recipientOrHospital = "Sahiddirgonj Central Hospital",
                donationDate = now - (98 * day),
                units = 1,
                certificateNumber = "SBG-2026-HERO-9912",
                patientCondition = "Emergency Cesarean Section Support",
                verifiedByHospital = true
            ),
            DonationHistory(
                id = "hist_2",
                donorId = "user_me",
                donorName = "You (Community Hero)",
                bloodGroup = "O+",
                recipientOrHospital = "Upazila Health Complex",
                donationDate = now - (210 * day),
                units = 1,
                certificateNumber = "SBG-2025-HERO-5521",
                patientCondition = "Thalassemia Monthly Support",
                verifiedByHospital = true
            ),
            DonationHistory(
                id = "hist_3",
                donorId = "donor_1",
                donorName = "Tanvir Ahmed",
                bloodGroup = "O+",
                recipientOrHospital = "Sahiddirgonj Red Crescent Clinic",
                donationDate = now - (98 * day),
                units = 1,
                certificateNumber = "SBG-2026-HERO-7734",
                patientCondition = "Accident Trauma Unit",
                verifiedByHospital = true
            ),
            DonationHistory(
                id = "hist_4",
                donorId = "donor_2",
                donorName = "Dr. Sabrina Karim",
                bloodGroup = "A+",
                recipientOrHospital = "Sahiddirgonj Central Hospital",
                donationDate = now - (115 * day),
                units = 1,
                certificateNumber = "SBG-2026-GOLD-4412",
                patientCondition = "Dialysis Patient Emergency Transfusion",
                verifiedByHospital = true
            )
        )
    }
}
