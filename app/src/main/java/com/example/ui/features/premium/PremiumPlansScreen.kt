package com.example.ui.features.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Model for dynamic Pricing Plan
data class PricingPlan(
    val id: String,
    val title: String,
    val period: String,
    val originalPrice: Long,
    val salePrice: Long,
    val badge: String? = null,
    val badgeColor: Color = Color(0xFFFF9800)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPlansScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    val coroutineScope = rememberCoroutineScope()
    
    // Plans selection
    val plans = listOf(
        PricingPlan(
            id = "plan_1",
            title = "عضویت طلایی شتاب دهم",
            period = "۱ ماهه آزمایشی",
            originalPrice = 99000L,
            salePrice = 79000L,
            badge = "تست شروع کار"
        ),
        PricingPlan(
            id = "plan_3",
            title = "پک نخبگان کنکور نهایی",
            period = "۳ ماهه طلایی",
            originalPrice = 289000L,
            salePrice = 199000L,
            badge = "پرطرفدارترین",
            badgeColor = colors.accentMain
        ),
        PricingPlan(
            id = "plan_12",
            title = "پک سالانه شتاب بی‌نهایت",
            period = "۱۲ ماهه کل سال تحصیلی",
            originalPrice = 890000L,
            salePrice = 590000L,
            badge = "۶۰٪ تخفیف ویژه",
            badgeColor = Color(0xFF4CAF50)
        )
    )

    var selectedPlanId by remember { mutableStateOf("plan_3") }
    val selectedPlan = plans.find { it.id == selectedPlanId } ?: plans[1]

    // Coupon System States
    var couponText by remember { mutableStateOf("") }
    var couponAppliedPercent by remember { mutableIntStateOf(0) }
    var couponFeedback by remember { mutableStateOf("") }
    var couponStatusIsError by remember { mutableStateOf(false) }

    // Selected final cost after coupon
    val calculatedPrice = remember(selectedPlan, couponAppliedPercent) {
        val discountAmount = (selectedPlan.salePrice * couponAppliedPercent) / 100
        selectedPlan.salePrice - discountAmount
    }

    // Interactive Purchase flow state
    var showPaymentSimulator by remember { mutableStateOf(false) }
    var isTransactionProcessing by remember { mutableStateOf(false) }
    var transactionSuccess by remember { mutableStateOf(false) }

    // Simulated card fields states
    var cardNumber by remember { mutableStateOf("") }
    var cardCvv2 by remember { mutableStateOf("") }
    var cardPin by remember { mutableStateOf("") }
    var otpCounter by remember { mutableStateOf(120) }
    var isOtpRequested by remember { mutableStateOf(false) }
    var isOtpError by remember { mutableStateOf(false) }

    // Start localized OTP countdown when requested
    LaunchedEffect(isOtpRequested) {
        if (isOtpRequested) {
            otpCounter = 120
            while (otpCounter > 0) {
                delay(1000)
                otpCounter--
            }
            isOtpRequested = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "عضویت ویژه شتاب طلایی 👑",
                        color = colors.primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "برگشت",
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bgMain)
            )
        },
        containerColor = colors.bgMain
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HERO BANNER & INTRO ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(colors.accentMain, colors.bgTopHeader)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 44.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "شتاب طلایی؛ برگ برنده آزمون‌های شما!",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "با خرید اشتراک طلایی شتاب، قفل تمام امکانات هوش مصنوعی، تحلیل‌های پيشرفته و برنامه‌ریز خودکار را باز کنید و معدل نهایی و تراز آزمونها را تضمین کنید.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // --- VALUE COMPARISON / FEATURES CHECKLIST ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "مزیت‌های عضویت طلایی در یک نگاه",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = colors.primaryText
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val benefits = listOf(
                            "مشاوره و تحلیل ۲۴ ساعته با دستیار هوشمند، رایا" to "نامحدود (عضو عادی: ۵ پیام)",
                            "آزمون‌ساز پیشرفته تشریحی و تستی" to "دسترسی کامل (عضو عادی: غیرفعال)",
                            "برنامه‌ریزی هوشمند تحصیلی اختصاصی" to "ویرایش نامحدود (عضو عادی: محدود)",
                            "رفع اشکال فوری در تالار همگانی با تصاویر" to "اولویت VIP (عضو عادی: اولویت عادی)",
                            "نمودارهای پیشرفت تحصیلی و بهداشت روان" to "بازه ۱ ساله (عضو عادی: بازه ۷ روزه)"
                        )

                        benefits.forEach { (title, subtitle) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Check Icon",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 9.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- PLAN SLIDER & INTERACTIVE CARDS ---
            item {
                Text(
                    text = "۱. یکی از لایسنس‌های زیر را انتخاب کنید:",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = colors.primaryText,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(plans) { plan ->
                val isSelected = plan.id == selectedPlanId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlanId = plan.id },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) colors.accentMain.copy(alpha = 0.08f) else colors.cardBg
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.2.dp else 1.dp,
                        color = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Selection indicator shape
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) colors.accentMain else colors.secondaryText.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .background(if (isSelected) colors.accentMain else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.White))
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Describe offer
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plan.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colors.primaryText
                                )
                                Text(
                                    text = plan.period,
                                    fontSize = 11.sp,
                                    color = colors.secondaryText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Pricing column
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${plan.originalPrice.toString().toPersianNumber()} تومان",
                                    fontSize = 10.sp,
                                    color = colors.secondaryText.copy(alpha = 0.7f),
                                    style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough)
                                )
                                Text(
                                    text = "${plan.salePrice.toString().toPersianNumber()} تومان",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colors.accentMain,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // Super badge overlay
                        plan.badge?.let { bText ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = (-8).dp, y = (-12).dp)
                                    .background(plan.badgeColor, RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = bText,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // --- DISCOUNT COUPON SYSTEM ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "کد تخفیف داری؟ دکمه اعمال رو لمس کن!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = colors.primaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponText,
                                onValueChange = { couponText = it },
                                placeholder = { Text("مثلاً: SHETAB_GIFT یا VIP_POUYA", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.accentMain,
                                    unfocusedBorderColor = colors.secondaryText.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = {
                                    val safeCode = couponText.trim().uppercase()
                                    if (safeCode == "SHETAB_GIFT") {
                                        couponAppliedPercent = 25
                                        couponFeedback = "کد تخفیف طلایی فعال شد! ۲۵٪ از هزینه کسر شد 🎉"
                                        couponStatusIsError = false
                                    } else if (safeCode == "VIP_POUYA") {
                                        couponAppliedPercent = 50
                                        couponFeedback = "تخفیف عالی ۵۰ درصد ویژه با موفقیت اعمال شد! 😍"
                                        couponStatusIsError = false
                                    } else {
                                        couponFeedback = "کد تخفیف معتبر نیست! مجدد چک کنید."
                                        couponStatusIsError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("اعمال کد", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (couponFeedback.isNotEmpty()) {
                            Text(
                                text = couponFeedback,
                                color = if (couponStatusIsError) Color.Red else Color(0xFF2E7D32),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // --- FINAL INVOICE & REDIRECT ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "فاکتور عضویت:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                            Text(
                                text = selectedPlan.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentMain
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مبلغ اصلی دوره:",
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                            Text(
                                text = "${selectedPlan.salePrice.toString().toPersianNumber()} تومان",
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                        }

                        if (couponAppliedPercent > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "میزان تخفیف کوپن:",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "$couponAppliedPercent٪ تخفیف",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "قابل پرداخت نهایی:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryText
                            )
                            Text(
                                text = "${calculatedPrice.toString().toPersianNumber()} تومان",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPaymentSimulator = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تایید و پرداخت ایمن با شتاب نت 💳",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // --- DEVIATION/SUPPORT SECTION ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "نیاز به مشورت داری؟ تماس با پشتیبانی ۲۴ ساعته: ۰۲۱-۸۸۸۸۴۴۲۲".toPersianNumber(),
                        fontSize = 10.sp,
                        color = colors.secondaryText
                    )
                }
            }
        }

        // --- SECTION E: REAL WORKING TRANSACTION BANK SHEET DIALOG ---
        if (showPaymentSimulator) {
            Dialog(
                onDismissRequest = { 
                    if (!isTransactionProcessing) showPaymentSimulator = false 
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (!transactionSuccess) {
                            // Header of banking portal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32).copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💳", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "درگاه پرداخت الکترونیک شتاب دهم",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E2E15)
                                    )
                                }
                                IconButton(
                                    onClick = { showPaymentSimulator = false },
                                    enabled = !isTransactionProcessing
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                                }
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                            // Show current transaction invoice
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("مبلغ تراکنش:", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                    Text("${calculatedPrice.toString().toPersianNumber()} تومان", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }

                            // Input fields simulated
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "۱. شماره کارت بانکی (۱۶ رقمی):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { if (it.length <= 16) cardNumber = it },
                                    placeholder = { Text("۶۰۳۷-۹۹۱۹-...", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    maxLines = 1,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "۲. کد CVV2:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        OutlinedTextField(
                                            value = cardCvv2,
                                            onValueChange = { if (it.length <= 4) cardCvv2 = it },
                                            placeholder = { Text("۴ رقمی", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            maxLines = 1,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "۳. رمز دوم کارت:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        OutlinedTextField(
                                            value = cardPin,
                                            onValueChange = { cardPin = it },
                                            placeholder = { Text("رمز پویا یا ایستا", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            maxLines = 1,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }

                            // Dynamic OTP simulated trigger button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { isOtpRequested = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentSecondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isOtpRequested) "ارسال مجدد رمزدوم" else "درخواست رمز پویا 📱",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isOtpRequested) {
                                    Text(
                                        text = "اعتبار رمز پویا: ${otpCounter.toString().toPersianNumber()} ثانیه",
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Submit confirmation button with loading
                            Button(
                                onClick = {
                                    if (cardNumber.length < 12) {
                                        isOtpError = true
                                        return@Button
                                    }
                                    isOtpError = false
                                    isTransactionProcessing = true
                                    coroutineScope.launch {
                                        delay(2000) // Simulated secure transaction step
                                        isTransactionProcessing = false
                                        transactionSuccess = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isTransactionProcessing,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                if (isTransactionProcessing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("اتصال به شتاب‌بانک و پرداخت", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            if (isOtpError) {
                                Text(
                                    text = "خطا! شماره کارت بانکی معتبر وارد کنید.",
                                    color = Color.Red,
                                    fontSize = 10.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // SUCCESS MODE! Celebrate VIP subscription activation with high polish confetti graphic
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🎉👑🏆", fontSize = 48.sp)
                                Text(
                                    text = "پرداخت و فعال‌سازی موفقیت‌آمیز بود!",
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "شما با موفقیت به جمع طلایی‌های شتاب پیوستید! قفل تمام سوالات کنکور سراسری، آزمون نهایی شبیه‌ساز و الگوها باز شد.",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("رسید دیجیتال:", fontSize = 10.sp, color = Color.Gray)
                                            Text("SHETAB_TX_98319".toPersianNumber(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("اشتراک فعال‌شده:", fontSize = 10.sp, color = Color.Gray)
                                            Text(selectedPlan.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        showPaymentSimulator = false
                                        navController.popBackStack() // Go back or refresh
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("شروع پرقدرت در مسیر طلایی", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
