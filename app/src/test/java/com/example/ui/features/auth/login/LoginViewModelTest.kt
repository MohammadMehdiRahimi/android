package com.example.ui.features.auth.login

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel()
    }

    @Test
    fun `initial state is empty and invalid`() {
        val state = viewModel.uiState.value
        assertEquals("", state.rawPhoneNumber)
        assertFalse(state.isValid)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `valid 11 digit iranian phone number starting with 09 sets isValid to true`() {
        viewModel.onPhoneNumberChanged("09123456789")
        val state = viewModel.uiState.value
        assertEquals("09123456789", state.rawPhoneNumber)
        assertTrue(state.isValid)
        assertEquals("۰۹۱۲۳۴۵۶۷۸۹", state.displayPhoneNumber)
    }

    @Test
    fun `valid 10 digit iranian phone number starting with 9 sets isValid to true and normalizes to 09`() {
        viewModel.onPhoneNumberChanged("9123456789")
        val state = viewModel.uiState.value
        assertEquals("9123456789", state.rawPhoneNumber)
        assertTrue(state.isValid)
        assertEquals("09123456789", viewModel.getNormalizedPhoneNumber())
    }

    @Test
    fun `persian digits are converted to english and limited to 11 digits`() {
        viewModel.onPhoneNumberChanged("۰۹۱۲۳۴۵۶۷۸۹۱")
        val state = viewModel.uiState.value
        assertEquals("09123456789", state.rawPhoneNumber)
        assertTrue(state.isValid)
    }

    @Test
    fun `invalid phone numbers are marked as not valid`() {
        viewModel.onPhoneNumberChanged("02112345678")
        assertFalse(viewModel.uiState.value.isValid)

        viewModel.onPhoneNumberChanged("12345")
        assertFalse(viewModel.uiState.value.isValid)
    }
}
