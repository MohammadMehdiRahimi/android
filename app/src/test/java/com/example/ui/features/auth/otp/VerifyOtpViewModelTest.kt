package com.example.ui.features.auth.otp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VerifyOtpViewModelTest {

    private lateinit var viewModel: VerifyOtpViewModel

    @Before
    fun setUp() {
        viewModel = VerifyOtpViewModel("09123456789")
    }

    @Test
    fun `initial state sets formatted persian phone number and starts timer`() {
        val state = viewModel.uiState.value
        assertEquals("09123456789", state.rawPhoneNumber)
        assertEquals("۰۹۱۲ ۳۴۵ ۶۷۸۹", state.formattedPhoneNumber)
        assertEquals("", state.otpCode)
        assertFalse(state.isCodeComplete)
        assertTrue(state.isTimerActive)
        assertEquals(120, state.remainingSeconds)
        assertEquals("۰۲:۰۰", state.formattedTime)
        assertNull(state.errorMessage)
    }

    @Test
    fun `entering valid 6-digit OTP updates state and marks isCodeComplete as true`() {
        viewModel.onOtpCodeChanged("123456")
        val state = viewModel.uiState.value
        assertEquals("123456", state.otpCode)
        assertTrue(state.isCodeComplete)
        assertNull(state.errorMessage)
    }

    @Test
    fun `persian digits in OTP are converted to english and limited to 6 digits`() {
        viewModel.onOtpCodeChanged("۱۲۳۴۵۶۷")
        val state = viewModel.uiState.value
        assertEquals("123456", state.otpCode)
        assertTrue(state.isCodeComplete)
    }

    @Test
    fun `incomplete OTP code sets isCodeComplete to false`() {
        viewModel.onOtpCodeChanged("1234")
        val state = viewModel.uiState.value
        assertEquals("1234", state.otpCode)
        assertFalse(state.isCodeComplete)
    }

    @Test
    fun `timer countdown string converts properly to persian format`() {
        val state = VerifyOtpUiState(remainingSeconds = 119)
        assertEquals("۰۱:۵۹", state.formattedTime)
    }

    @Test
    fun `resend button text reflects remaining cooldown time`() {
        val activeState = VerifyOtpUiState(remainingSeconds = 120, isTimerActive = true)
        assertEquals("ارسال مجدد کد (۲ دقیقه بعد)", activeState.resendButtonText)

        val expiredState = VerifyOtpUiState(remainingSeconds = 0, isTimerActive = false)
        assertEquals("ارسال مجدد کد", expiredState.resendButtonText)
    }
}
