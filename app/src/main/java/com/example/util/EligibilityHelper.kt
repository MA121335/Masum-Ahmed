package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class EligibilityResult(
    val isEligible: Boolean,
    val cooldownPassed: Boolean,
    val daysRemainingCooldown: Long,
    val nextEligibleDateString: String,
    val reasons: List<String>,
    val verifiedHero: Boolean,
    val scorePercentage: Int
)

object EligibilityHelper {
    const val COOLDOWN_DAYS = 90L // Standard 3-month cooldown period between whole blood donations
    const val MIN_WEIGHT_KG = 50.0f
    const val MIN_AGE = 18
    const val MAX_AGE = 65
    const val MIN_HEMOGLOBIN = 12.5f

    fun checkEligibility(
        lastDonationTimestamp: Long,
        age: Int,
        weightKg: Float,
        hemoglobin: Float,
        isIdVerified: Boolean,
        isMedicalClearance: Boolean
    ): EligibilityResult {
        val now = System.currentTimeMillis()
        val diffMillis = now - lastDonationTimestamp
        val daysSinceLast = TimeUnit.MILLISECONDS.toDays(diffMillis)

        val cooldownPassed = daysSinceLast >= COOLDOWN_DAYS || lastDonationTimestamp == 0L
        val daysRemaining = if (cooldownPassed) 0L else (COOLDOWN_DAYS - daysSinceLast)

        val nextEligibleTimestamp = if (cooldownPassed) now else (lastDonationTimestamp + TimeUnit.DAYS.toMillis(COOLDOWN_DAYS))
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val nextEligibleDateString = dateFormat.format(Date(nextEligibleTimestamp))

        val reasons = mutableListOf<String>()
        var score = 100

        if (!cooldownPassed) {
            reasons.add("Must wait $daysRemaining more day(s) for the 90-day recovery cooldown.")
            score -= 30
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            reasons.add("Donor age must be between $MIN_AGE and $MAX_AGE years (Current: $age).")
            score -= 30
        }
        if (weightKg < MIN_WEIGHT_KG) {
            reasons.add("Body weight must be at least ${MIN_WEIGHT_KG}kg (Current: ${weightKg}kg).")
            score -= 25
        }
        if (hemoglobin < MIN_HEMOGLOBIN && hemoglobin > 0f) {
            reasons.add("Hemoglobin must be at least ${MIN_HEMOGLOBIN} g/dL (Current: $hemoglobin).")
            score -= 20
        }

        val isEligible = reasons.isEmpty()
        val isVerifiedHero = isIdVerified && isMedicalClearance

        return EligibilityResult(
            isEligible = isEligible,
            cooldownPassed = cooldownPassed,
            daysRemainingCooldown = daysRemaining,
            nextEligibleDateString = nextEligibleDateString,
            reasons = reasons,
            verifiedHero = isVerifiedHero,
            scorePercentage = score.coerceIn(0, 100)
        )
    }

    fun formatDate(timestamp: Long): String {
        if (timestamp == 0L) return "Never donated yet"
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days == 1L -> "Yesterday"
            days < 30 -> "$days days ago"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
