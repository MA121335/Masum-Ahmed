package com.example.data.repository

import com.example.data.local.BloodDatabase
import com.example.data.model.BloodRequest
import com.example.data.model.DonationHistory
import com.example.data.model.Donor
import com.example.util.SampleDataGenerator
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BloodRepository(private val database: BloodDatabase) {
    private val donorDao = database.donorDao()
    private val requestDao = database.bloodRequestDao()
    private val historyDao = database.donationHistoryDao()

    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors()
    val allRequests: Flow<List<BloodRequest>> = requestDao.getAllRequests()
    val allHistory: Flow<List<DonationHistory>> = historyDao.getAllHistory()

    suspend fun initializeIfNeeded() {
        if (donorDao.getDonorCount() == 0) {
            donorDao.insertDonors(SampleDataGenerator.getInitialDonors())
        }
        if (requestDao.getRequestCount() == 0) {
            requestDao.insertRequests(SampleDataGenerator.getInitialRequests())
        }
        if (historyDao.getHistoryCount() == 0) {
            historyDao.insertHistories(SampleDataGenerator.getInitialHistory())
        }
    }

    suspend fun insertDonor(donor: Donor) {
        donorDao.insertDonor(donor)
    }

    suspend fun updateDonorAvailability(id: String, isAvailable: Boolean) {
        donorDao.updateAvailability(id, isAvailable)
    }

    suspend fun insertRequest(request: BloodRequest) {
        requestDao.insertRequest(request)
    }

    suspend fun markRequestFulfilled(id: String) {
        requestDao.markFulfilled(id)
    }

    suspend fun verifyDonor(id: String, badge: String, isIdVerified: Boolean, isMedicalClearance: Boolean) {
        donorDao.updateDonorVerification(id, badge, isIdVerified, isMedicalClearance)
    }

    suspend fun deleteDonor(id: String) {
        donorDao.deleteDonorById(id)
    }

    suspend fun verifyRequest(id: String, isVerified: Boolean) {
        requestDao.updateRequestVerification(id, isVerified)
    }

    suspend fun deleteRequest(id: String) {
        requestDao.deleteRequestById(id)
    }

    suspend fun insertDonationHistory(
        donorName: String,
        bloodGroup: String,
        hospital: String,
        units: Int,
        condition: String
    ) {
        val certCode = "SBG-2026-HERO-" + (1000..9999).random()
        val history = DonationHistory(
            id = UUID.randomUUID().toString(),
            donorId = "user_me",
            donorName = donorName,
            bloodGroup = bloodGroup,
            recipientOrHospital = hospital,
            donationDate = System.currentTimeMillis(),
            units = units,
            certificateNumber = certCode,
            patientCondition = condition,
            verifiedByHospital = true
        )
        historyDao.insertHistory(history)
    }
}
