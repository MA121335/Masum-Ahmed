package com.example

import com.example.util.EligibilityHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExampleUnitTest {
    @Test
    fun testEligibleDonorCheck() {
        val now = System.currentTimeMillis()
        val ninetyDaysAgo = now - TimeUnit.DAYS.toMillis(95)

        val result = EligibilityHelper.checkEligibility(
            lastDonationTimestamp = ninetyDaysAgo,
            age = 25,
            weightKg = 65f,
            hemoglobin = 14f,
            isIdVerified = true,
            isMedicalClearance = true
        )

        assertTrue(result.isEligible)
        assertTrue(result.cooldownPassed)
        assertEquals(0L, result.daysRemainingCooldown)
        assertTrue(result.verifiedHero)
    }

    @Test
    fun testCooldownUnder90Days() {
        val now = System.currentTimeMillis()
        val twentyDaysAgo = now - TimeUnit.DAYS.toMillis(20)

        val result = EligibilityHelper.checkEligibility(
            lastDonationTimestamp = twentyDaysAgo,
            age = 28,
            weightKg = 70f,
            hemoglobin = 13.5f,
            isIdVerified = true,
            isMedicalClearance = true
        )

        assertFalse(result.isEligible)
        assertFalse(result.cooldownPassed)
        assertTrue(result.daysRemainingCooldown in 69L..71L)
    }

    @Test
    fun testUnderweightDonor() {
        val now = System.currentTimeMillis()
        val ninetyDaysAgo = now - TimeUnit.DAYS.toMillis(100)

        val result = EligibilityHelper.checkEligibility(
            lastDonationTimestamp = ninetyDaysAgo,
            age = 22,
            weightKg = 45f, // Under 50kg requirement
            hemoglobin = 13.0f,
            isIdVerified = true,
            isMedicalClearance = true
        )

        assertFalse(result.isEligible)
        assertTrue(result.reasons.any { it.contains("weight") })
    }
}
