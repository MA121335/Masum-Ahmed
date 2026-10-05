package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BloodRequest
import com.example.data.model.DonationHistory
import com.example.data.model.Donor
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY isAvailable DESC, totalDonations DESC")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup ORDER BY isAvailable DESC")
    fun getDonorsByBloodGroup(bloodGroup: String): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: String): Donor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<Donor>)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Query("UPDATE donors SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: String, isAvailable: Boolean)

    @Query("UPDATE donors SET verificationBadge = :badge, isIdVerified = :isIdVerified, isMedicalClearance = :isMedicalClearance WHERE id = :id")
    suspend fun updateDonorVerification(id: String, badge: String, isIdVerified: Boolean, isMedicalClearance: Boolean)

    @Query("DELETE FROM donors WHERE id = :id")
    suspend fun deleteDonorById(id: String)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun getDonorCount(): Int
}

@Dao
interface BloodRequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY CASE WHEN urgency = 'EMERGENCY_NOW' THEN 0 WHEN urgency = 'CRITICAL_TODAY' THEN 1 ELSE 2 END, createdAt DESC")
    fun getAllRequests(): Flow<List<BloodRequest>>

    @Query("SELECT * FROM blood_requests WHERE status = 'OPEN' ORDER BY createdAt DESC")
    fun getOpenRequests(): Flow<List<BloodRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<BloodRequest>)

    @Update
    suspend fun updateRequest(request: BloodRequest)

    @Query("UPDATE blood_requests SET status = 'FULFILLED' WHERE id = :id")
    suspend fun markFulfilled(id: String)

    @Query("UPDATE blood_requests SET isAdminVerified = :isVerified WHERE id = :id")
    suspend fun updateRequestVerification(id: String, isVerified: Boolean)

    @Query("DELETE FROM blood_requests WHERE id = :id")
    suspend fun deleteRequestById(id: String)

    @Query("SELECT COUNT(*) FROM blood_requests")
    suspend fun getRequestCount(): Int
}

@Dao
interface DonationHistoryDao {
    @Query("SELECT * FROM donation_history ORDER BY donationDate DESC")
    fun getAllHistory(): Flow<List<DonationHistory>>

    @Query("SELECT * FROM donation_history WHERE donorId = :donorId ORDER BY donationDate DESC")
    fun getHistoryForDonor(donorId: String): Flow<List<DonationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: DonationHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<DonationHistory>)

    @Query("SELECT COUNT(*) FROM donation_history")
    suspend fun getHistoryCount(): Int
}
