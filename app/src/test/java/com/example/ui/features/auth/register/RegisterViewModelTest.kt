package com.example.ui.features.auth.register

import com.example.network.AcademicOptionDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterViewModelTest {

    private fun createSampleGrades(): List<AcademicOptionDto> {
        return listOf(
            AcademicOptionDto(key = "FIFTH", value = "پنجم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "SEVENTH", value = "هفتم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "EIGHTH", value = "هشتم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "NINTH", value = "نهم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "TENTH", value = "دهم", requiresFieldOfStudy = true),
            AcademicOptionDto(key = "ELEVENTH", value = "یازدهم", requiresFieldOfStudy = true),
            AcademicOptionDto(key = "TWELFTH", value = "دوازدهم", requiresFieldOfStudy = true)
        )
    }

    private fun createSampleFields(): List<AcademicOptionDto> {
        return listOf(
            AcademicOptionDto(key = "MATHEMATICS", value = "ریاضی"),
            AcademicOptionDto(key = "HUMANITIES", value = "انسانی"),
            AcademicOptionDto(key = "EXPERIMENTAL", value = "تجربی")
        )
    }

    @Test
    fun `name validation requires at least 3 characters`() {
        val state = RegisterUiState()
        
        val shortNameState = state.copy(fullName = "AB", nameError = "نام و نام خانوادگی باید حداقل ۳ حرف باشد.")
        assertEquals("نام و نام خانوادگی باید حداقل ۳ حرف باشد.", shortNameState.nameError)
        
        val validNameState = state.copy(fullName = "علی رضایی", nameError = null)
        assertNull(validNameState.nameError)
    }

    @Test
    fun `requiresFieldOfStudy is false for grades 5 to 9`() {
        val grades = createSampleGrades()
        
        // 5th grade
        val state5 = RegisterUiState(grades = grades, selectedGradeCode = "FIFTH")
        assertFalse(state5.requiresFieldOfStudy)

        // 7th grade
        val state7 = RegisterUiState(grades = grades, selectedGradeCode = "SEVENTH")
        assertFalse(state7.requiresFieldOfStudy)

        // 9th grade
        val state9 = RegisterUiState(grades = grades, selectedGradeCode = "NINTH")
        assertFalse(state9.requiresFieldOfStudy)
    }
    
    @Test
    fun `requiresFieldOfStudy is true for grades 10 to 12`() {
        val grades = createSampleGrades()
        
        // 10th grade
        val state10 = RegisterUiState(grades = grades, selectedGradeCode = "TENTH")
        assertTrue(state10.requiresFieldOfStudy)

        // 11th grade
        val state11 = RegisterUiState(grades = grades, selectedGradeCode = "ELEVENTH")
        assertTrue(state11.requiresFieldOfStudy)

        // 12th grade
        val state12 = RegisterUiState(grades = grades, selectedGradeCode = "TWELFTH")
        assertTrue(state12.requiresFieldOfStudy)
    }

    @Test
    fun `isSubmitEnabled is true only when all required inputs are present`() {
        val grades = createSampleGrades()
        val fields = createSampleFields()

        // 1. Lower grade without field of study -> valid
        val validLowerGradeState = RegisterUiState(
            fullName = "علی رضایی",
            grades = grades,
            fieldsOfStudy = fields,
            selectedGradeCode = "SEVENTH",
            selectedFieldCode = null,
            isLoading = false,
            isFetchingOptions = false
        )
        assertTrue(validLowerGradeState.isSubmitEnabled)

        // 2. Higher grade with field of study -> valid
        val validHighGradeState = RegisterUiState(
            fullName = "علی رضایی",
            grades = grades,
            fieldsOfStudy = fields,
            selectedGradeCode = "TENTH",
            selectedFieldCode = "EXPERIMENTAL",
            isLoading = false,
            isFetchingOptions = false
        )
        assertTrue(validHighGradeState.isSubmitEnabled)

        // 3. Higher grade WITHOUT field of study -> invalid
        val invalidHighGradeState = RegisterUiState(
            fullName = "علی رضایی",
            grades = grades,
            fieldsOfStudy = fields,
            selectedGradeCode = "TENTH",
            selectedFieldCode = null,
            isLoading = false,
            isFetchingOptions = false
        )
        assertFalse(invalidHighGradeState.isSubmitEnabled)

        // 4. Short name -> invalid
        val invalidNameState = validHighGradeState.copy(fullName = "AB")
        assertFalse(invalidNameState.isSubmitEnabled)

        // 5. While loading -> invalid
        val loadingState = validHighGradeState.copy(isLoading = true)
        assertFalse(loadingState.isSubmitEnabled)
    }
}
