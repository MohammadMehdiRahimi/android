package com.example.ui.features.auth.register

import com.example.network.AcademicOptionDto

data class RegisterUiState(
    val fullName: String = "",
    val nameError: String? = null,
    val selectedGradeCode: String = "",
    val selectedFieldCode: String? = null,
    val grades: List<AcademicOptionDto> = emptyList(),
    val fieldsOfStudy: List<AcademicOptionDto> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingOptions: Boolean = true,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val selectedGradeOption: AcademicOptionDto?
        get() = grades.firstOrNull { it.effectiveKey == selectedGradeCode }

    val selectedFieldOption: AcademicOptionDto?
        get() = fieldsOfStudy.firstOrNull { it.effectiveKey == selectedFieldCode }

    val requiresFieldOfStudy: Boolean
        get() {
            val option = selectedGradeOption ?: return false
            if (option.requiresFieldOfStudy) return true

            val keyUpper = option.effectiveKey.uppercase()
            val valText = option.effectiveValue

            // Explicit check for lower grades (5th to 9th)
            val isLowerGrade = keyUpper.contains("FIFTH") || keyUpper.contains("SIXTH") ||
                    keyUpper.contains("SEVENTH") || keyUpper.contains("EIGHTH") ||
                    keyUpper.contains("NINTH") || keyUpper.matches(Regex(".*GRADE_[5-9].*")) ||
                    valText.contains("پنجم") || valText.contains("ششم") ||
                    valText.contains("هفتم") || valText.contains("هشتم") || valText.contains("نهم")

            if (isLowerGrade) return false

            // Check for high school / secondary grades (10th to 12th / Konkour)
            val isHighSchool = keyUpper.contains("TENTH") || keyUpper.contains("ELEVENTH") ||
                    keyUpper.contains("TWELFTH") || keyUpper.contains("KONKOUR") ||
                    keyUpper.contains("10") || keyUpper.contains("11") || keyUpper.contains("12") ||
                    valText.contains("دهم") || valText.contains("یازدهم") ||
                    valText.contains("دوازدهم") || valText.contains("کنکور")

            return isHighSchool
        }

    val isSubmitEnabled: Boolean
        get() = fullName.trim().length >= 3 &&
                selectedGradeCode.isNotBlank() &&
                (!requiresFieldOfStudy || !selectedFieldCode.isNullOrBlank()) &&
                !isLoading &&
                !isFetchingOptions
}
