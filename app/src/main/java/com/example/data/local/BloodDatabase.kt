package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BloodRequest
import com.example.data.model.DonationHistory
import com.example.data.model.Donor

@Database(
    entities = [Donor::class, BloodRequest::class, DonationHistory::class],
    version = 2,
    exportSchema = false
)
abstract class BloodDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun bloodRequestDao(): BloodRequestDao
    abstract fun donationHistoryDao(): DonationHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: BloodDatabase? = null

        fun getInstance(context: Context): BloodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodDatabase::class.java,
                    "sahiddirgonj_blood_db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
