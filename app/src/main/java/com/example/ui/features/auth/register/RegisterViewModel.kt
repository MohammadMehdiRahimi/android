package com.example.ui.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthRepositoryImpl
import com.example.network.AcademicOptionDto
import com.example.network.ApiClient
import com.example.network.ApiService
import com.example.network.NetworkResult
import com.example.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository? = null,
    private val apiService: ApiService? = null,
    private val phoneParam: String? = null,
    private val registrationTokenParam: String? = null
) : ViewModel() {

    private fun getRepository(): AuthRepository {
        return authRepository ?: AuthRepositoryImpl(
            apiService = getService(),
            tokenManager = ApiClient.getTokenManager() ?: throw IllegalStateException("TokenManager is not initialized")
        )
    }

    private fun getService(): ApiService {
        return apiService ?: ApiClient.apiService
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        fetchOnboardingOptions()
    }

    fun fetchOnboardingOptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingOptions = true, errorMessage = null) }
            
            val result = safeApiCall {
                getService().getOnboardingOptions()
            }

            when (result) {
                is NetworkResult.Success -> {
                    val body = result.data
                    val grades = body.resolvedGrades.ifEmpty { defaultGrades }
                    val fields = body.resolvedFieldsOfStudy.ifEmpty { defaultFieldsOfStudy }

                    val defaultGrade = grades.firstOrNull { it.effectiveKey.contains("10") || it.effectiveKey.contains("TENTH") || it.effectiveValue == "دهم" }?.effectiveKey
                        ?: grades.firstOrNull()?.effectiveKey.orEmpty()

                    val defaultField = fields.firstOrNull { it.effectiveKey.contains("EXPERIMENTAL") || it.effectiveValue.contains("تجربی") }?.effectiveKey
                        ?: fields.firstOrNull()?.effectiveKey

                    _uiState.update {
                        it.copy(
                            isFetchingOptions = false,
                            grades = grades,
                            fieldsOfStudy = fields,
                            selectedGradeCode = if (it.selectedGradeCode.isBlank()) defaultGrade else it.selectedGradeCode,
                            selectedFieldCode = it.selectedFieldCode ?: defaultField
                        )
                    }
                }
                is NetworkResult.Error, is NetworkResult.Exception -> {
                    // Fallback to standard educational base data so the UI remains interactive
                    val defaultGrade = defaultGrades.firstOrNull { it.effectiveKey == "TENTH" }?.effectiveKey ?: defaultGrades.first().effectiveKey
                    val defaultField = defaultFieldsOfStudy.firstOrNull { it.effectiveKey == "EXPERIMENTAL" }?.effectiveKey ?: defaultFieldsOfStudy.first().effectiveKey

                    _uiState.update {
                        it.copy(
                            isFetchingOptions = false,
                            grades = defaultGrades,
                            fieldsOfStudy = defaultFieldsOfStudy,
                            selectedGradeCode = if (it.selectedGradeCode.isBlank()) defaultGrade else it.selectedGradeCode,
                            selectedFieldCode = it.selectedFieldCode ?: defaultField,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        val trimmed = name.trim()
        val error = when {
            name.isEmpty() -> null
            trimmed.length < 3 -> "نام و نام خانوادگی باید حداقل ۳ حرف باشد."
            else -> null
        }
        _uiState.update {
            it.copy(
                fullName = name,
                nameError = error,
                errorMessage = null
            )
        }
    }

    fun onGradeSelected(gradeKey: String) {
        _uiState.update { state ->
            val newState = state.copy(
                selectedGradeCode = gradeKey,
                errorMessage = null
            )
            // If the selected grade does not require field of study, clear selection or keep
            if (!newState.requiresFieldOfStudy) {
                newState.copy(selectedFieldCode = null)
            } else if (newState.selectedFieldCode == null) {
                val defaultField = state.fieldsOfStudy.firstOrNull { it.effectiveKey.contains("EXPERIMENTAL") || it.effectiveValue.contains("تجربی") }?.effectiveKey
                    ?: state.fieldsOfStudy.firstOrNull()?.effectiveKey
                newState.copy(selectedFieldCode = defaultField)
            } else {
                newState
            }
        }
    }

    fun onFieldSelected(fieldKey: String) {
        _uiState.update {
            it.copy(
                selectedFieldCode = fieldKey,
                errorMessage = null
            )
        }
    }

    fun register() {
        val currentState = _uiState.value
        val name = currentState.fullName.trim()

        if (name.length < 2) {
            _uiState.update { it.copy(nameError = "نام و نام خانوادگی باید حداقل ۲ حرف باشد.") }
            return
        }

        val grade = currentState.selectedGradeCode
        if (grade.isBlank()) {
            _uiState.update { it.copy(errorMessage = "لطفاً پایه تحصیلی خود را انتخاب کنید.") }
            return
        }

        val fieldOfStudy = if (currentState.requiresFieldOfStudy) currentState.selectedFieldCode else null

        if (currentState.requiresFieldOfStudy && fieldOfStudy.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "لطفاً رشته تحصیلی خود را انتخاب کنید.") }
            return
        }

        val tokenManager = ApiClient.getTokenManager()
        val phone = phoneParam?.takeIf { it.isNotBlank() }
            ?: tokenManager?.getRegistrationPhone()
            ?: tokenManager?.getUserPhone().orEmpty()

        val registrationToken = registrationTokenParam?.takeIf { it.isNotBlank() }
            ?: tokenManager?.getRegistrationToken().orEmpty()

        if (registrationToken.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "نشست ثبت‌نام منقضی شده است یا توکن یافت نشد. لطفاً مجدداً وارد شوید."
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                getRepository().register(
                    phone = phone,
                    registrationToken = registrationToken,
                    fullName = name,
                    grade = grade,
                    fieldOfStudy = fieldOfStudy,
                    deviceType = "ANDROID"
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        }
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message ?: "خطا در انجام ثبت‌نام"
                                )
                            }
                        }
                        is NetworkResult.Exception -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "خطا در برقراری ارتباط با سرور"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "خطای ناشناخته رخ داده است."
                    )
                }
            }
        }
    }

    companion object {
        val defaultGrades = listOf(
            AcademicOptionDto(key = "SEVENTH", value = "هفتم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "EIGHTH", value = "هشتم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "NINTH", value = "نهم", requiresFieldOfStudy = false),
            AcademicOptionDto(key = "TENTH", value = "دهم", requiresFieldOfStudy = true),
            AcademicOptionDto(key = "ELEVENTH", value = "یازدهم", requiresFieldOfStudy = true),
            AcademicOptionDto(key = "TWELFTH", value = "دوازدهم", requiresFieldOfStudy = true)
        )

        val defaultFieldsOfStudy = listOf(
            AcademicOptionDto(key = "MATHEMATICS", value = "ریاضی"),
            AcademicOptionDto(key = "HUMANITIES", value = "انسانی"),
            AcademicOptionDto(key = "EXPERIMENTAL", value = "تجربی")
        )
    }
}
