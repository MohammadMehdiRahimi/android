package com.example.ui.features.auth.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.network.AcademicOptionDto
import com.example.ui.core.components.shimmerEffect
import com.example.ui.theme.VazirmatnFontFamily

private val PrimaryPurple = Color(0xFF6851FF)
private val BrandPurpleDark = Color(0xFF5E3CEE)
private val TextDark = Color(0xFF1E293B)
private val TextGray = Color(0xFF64748B)
private val TextPlaceholder = Color(0xFF94A3B8)
private val CardBackground = Color(0xFFF9F8FD)
private val CardBorderColor = Color(0xFFECE8FD)
private val ChipBorderColor = Color(0xFFE5E0FA)
private val SelectedFieldBackground = Color(0xFFF4F0FF)
private val BackgroundColor = Color(0xFFFAFAFE)

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate("dashboard") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("register_screen_root")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Main Content Block
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Minimal Top Header with only Back Button
                    RegisterTopHeader(
                        onBackClick = {
                            focusManager.clearFocus()
                            navController.popBackStack()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Screen Title & Subtitle (Compact & Minimal)
                    Text(
                        text = stringResource(id = R.string.register_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        fontFamily = VazirmatnFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_title")
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = stringResource(id = R.string.register_subtitle),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextGray,
                        fontFamily = VazirmatnFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_subtitle")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Full Name Input Card
                    FullNameInputSection(
                        fullName = uiState.fullName,
                        nameError = uiState.nameError,
                        onNameChanged = { viewModel.onNameChanged(it) },
                        onImeDone = { focusManager.clearFocus() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Grade Selection Card (پایه تحصیلی)
                    GradeSelectionSection(
                        grades = uiState.grades,
                        selectedGradeCode = uiState.selectedGradeCode,
                        isFetching = uiState.isFetchingOptions,
                        onGradeSelected = {
                            focusManager.clearFocus()
                            viewModel.onGradeSelected(it)
                        }
                    )

                    // 3. Field of Study Selection Card (رشته تحصیلی) - Conditionally Visible
                    AnimatedVisibility(
                        visible = uiState.requiresFieldOfStudy,
                        enter = fadeIn(tween(250)) + expandVertically(tween(300)),
                        exit = fadeOut(tween(200)) + shrinkVertically(tween(250))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            FieldOfStudySection(
                                fields = uiState.fieldsOfStudy,
                                selectedFieldCode = uiState.selectedFieldCode,
                                isFetching = uiState.isFetchingOptions,
                                onFieldSelected = {
                                    focusManager.clearFocus()
                                    viewModel.onFieldSelected(it)
                                }
                            )
                        }
                    }

                    // Error Message Display
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = Color(0xFFB91C1C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = VazirmatnFontFamily,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("register_error_text")
                            )
                        }
                    }
                }

                // Bottom Action Button Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.register()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(
                                elevation = if (uiState.isSubmitEnabled) 6.dp else 1.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = Color(0x356851FF),
                                ambientColor = Color(0x08000000)
                            )
                            .testTag("register_submit_button"),
                        enabled = uiState.isSubmitEnabled,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = PrimaryPurple.copy(alpha = 0.4f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.register_submit_button),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = VazirmatnFontFamily
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Continue",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterTopHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .testTag("register_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BrandPurpleDark,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun FullNameInputSection(
    fullName: String,
    nameError: String?,
    onNameChanged: (String) -> Unit,
    onImeDone: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_name_container"),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header Row: Title + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.register_name_label),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontFamily = VazirmatnFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "User Icon",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rounded Input Field Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(
                    width = if (nameError != null) 1.5.dp else 1.dp,
                    color = if (nameError != null) Color(0xFFEF4444) else Color(0xFFDCD6FB)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (fullName.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.register_name_placeholder),
                                fontSize = 13.sp,
                                color = TextPlaceholder,
                                fontFamily = VazirmatnFontFamily
                            )
                        }
                        BasicTextField(
                            value = fullName,
                            onValueChange = onNameChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_text_field"),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark,
                                fontFamily = VazirmatnFontFamily
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onImeDone() }
                            ),
                            cursorBrush = SolidColor(PrimaryPurple)
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "User",
                        tint = if (fullName.isNotEmpty()) PrimaryPurple else TextPlaceholder,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (nameError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nameError,
                    fontSize = 11.5.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium,
                    fontFamily = VazirmatnFontFamily,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun GradeSelectionSection(
    grades: List<AcademicOptionDto>,
    selectedGradeCode: String,
    isFetching: Boolean,
    onGradeSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_grade_container"),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header Row: Title + Book Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.register_grade_label),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontFamily = VazirmatnFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = "Grade Icon",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isFetching && grades.isEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .shimmerEffect(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            } else {
                val chunkedGrades = grades.chunked(3)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunkedGrades.forEach { rowGrades ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowGrades.forEach { gradeOption ->
                                val key = gradeOption.effectiveKey
                                val label = gradeOption.effectiveValue
                                val isSelected = key == selectedGradeCode

                                GradeChip(
                                    modifier = Modifier.weight(1f),
                                    text = label,
                                    isSelected = isSelected,
                                    onClick = { onGradeSelected(key) }
                                )
                            }
                            repeat(3 - rowGrades.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = tween(150),
        label = "chipScale"
    )

    Surface(
        modifier = modifier
            .height(42.dp)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 3.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isSelected) Color(0x356851FF) else Color.Transparent
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("grade_chip_$text"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryPurple else Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PrimaryPurple else ChipBorderColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) Color.White else TextDark,
                    fontFamily = VazirmatnFontFamily,
                    textAlign = TextAlign.Center
                )

                if (isSelected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldOfStudySection(
    fields: List<AcademicOptionDto>,
    selectedFieldCode: String?,
    isFetching: Boolean,
    onFieldSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_field_container"),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header Row: Title + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.register_field_label),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontFamily = VazirmatnFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = "Field Icon",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isFetching && fields.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .shimmerEffect(RoundedCornerShape(12.dp))
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fields.forEach { fieldOption ->
                        val key = fieldOption.effectiveKey
                        val label = fieldOption.effectiveValue
                        val isSelected = key == selectedFieldCode

                        val icon = getFieldIcon(key, label)

                        FieldOfStudyChip(
                            modifier = Modifier.weight(1f),
                            text = label,
                            icon = icon,
                            isSelected = isSelected,
                            onClick = { onFieldSelected(key) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldOfStudyChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = tween(150),
        label = "fieldChipScale"
    )

    Surface(
        modifier = modifier
            .height(42.dp)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 2.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isSelected) Color(0x306851FF) else Color.Transparent
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("field_chip_$text"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SelectedFieldBackground else Color.White,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryPurple else ChipBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(2.dp))
            }

            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isSelected) PrimaryPurple else TextDark,
                fontFamily = VazirmatnFontFamily,
                textAlign = TextAlign.Center
            )

            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun getFieldIcon(key: String, label: String): ImageVector {
    val upperKey = key.uppercase()
    return when {
        upperKey.contains("MATH") || label.contains("ریاضی") -> Icons.Outlined.Calculate
        upperKey.contains("HUMAN") || label.contains("انسانی") -> Icons.AutoMirrored.Outlined.MenuBook
        upperKey.contains("EXP") || upperKey.contains("SCIENCE") || label.contains("تجربی") -> Icons.Outlined.Science
        else -> Icons.Outlined.School
    }
}
