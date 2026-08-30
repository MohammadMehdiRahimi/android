package com.example.ui.features.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// Enum for icon types in plans
enum class PlanType {
    ONE_MONTH,
    THREE_MONTHS,
    TWELVE_MONTHS
}

// Data class for a Subscription Plan
data class SubscriptionPlan(
    val id: String,
    val type: PlanType,
    val durationTitle: String,
    val subtitle: String,
    val price: Long,
    val originalPrice: Long? = null,
    val discountPercent: Int? = null,
    val isFeatured: Boolean = false,
    val ribbonTitle: String? = null,
    val themeColor: Color,
    val lightBgColor: Color,
    val features: List<String>
)

// Active subscription model
data class ActiveSubscriptionInfo(
    val planTitle: String = "پلن ۳ ماهه",
    val activeUntil: String = "فعال تا ۶ آذر ۱۴۰۳",
    val isCurrentlyActive: Boolean = true,
    val startDate: String = "۶ شهریور ۱۴۰۳",
    val endDate: String = "۶ آذر ۱۴۰۳",
    val remainingDays: Int = 92
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPlansScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    
    // Active subscription state
    var activeSub by remember { mutableStateOf(ActiveSubscriptionInfo()) }

    // Subscription Plans exactly matching the image
    val plans = remember {
        listOf(
            SubscriptionPlan(
                id = "plan_3_months",
                type = PlanType.THREE_MONTHS,
                durationTitle = "۳ ماهه",
                subtitle = "محبوب‌ترین انتخاب",
                price = 249000L,
                originalPrice = 300000L,
                discountPercent = 17,
                isFeatured = true,
                ribbonTitle = "پیشنهاد ویژه ★",
                themeColor = Color(0xFF6320EE),
                lightBgColor = Color(0xFFFAF9FE),
                features = listOf(
                    "گزارش‌های تحلیلی",
                    "آزمون‌های استاندارد",
                    "پشتیبانی درون‌برنامه",
                    "پشتیبانی تحلیلی"
                )
            ),
            SubscriptionPlan(
                id = "plan_1_month",
                type = PlanType.ONE_MONTH,
                durationTitle = "۱ ماهه",
                subtitle = "مناسب برای شروع",
                price = 89000L,
                originalPrice = null,
                discountPercent = null,
                isFeatured = false,
                ribbonTitle = null,
                themeColor = Color(0xFFFF7A00),
                lightBgColor = Color(0xFFFFFBF7),
                features = listOf(
                    "دسترسی کامل امکانات",
                    "آزمون‌های استاندارد",
                    "گزارش‌های تحلیلی",
                    "پشتیبانی درون‌برنامه"
                )
            ),
            SubscriptionPlan(
                id = "plan_12_months",
                type = PlanType.TWELVE_MONTHS,
                durationTitle = "۱۲ ماهه",
                subtitle = "بصرفه‌ترین انتخاب",
                price = 899000L,
                originalPrice = 1350000L,
                discountPercent = 33,
                isFeatured = false,
                ribbonTitle = null,
                themeColor = Color(0xFF10B981),
                lightBgColor = Color(0xFFF6FDF9),
                features = listOf(
                    "دسترسی کامل امکانات",
                    "آزمون‌های استاندارد",
                    "گزارش‌های تحلیلی",
                    "پشتیبانی درون‌برنامه"
                )
            )
        )
    }

    // Selected plan for checkout
    var selectedPlanToBuy by remember { mutableStateOf<SubscriptionPlan?>(null) }
    
    // Coupon Dialog State
    var showCouponDialog by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }
    var appliedDiscountPercent by remember { mutableIntStateOf(0) }
    var couponFeedbackMessage by remember { mutableStateOf("") }
    var isCouponSuccess by remember { mutableStateOf(false) }

    // Payment Simulator State
    var showPaymentSimulator by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var isPaymentSuccess by remember { mutableStateOf(false) }
    
    // Receipt/History Dialog
    var showReceiptsDialog by remember { mutableStateOf(false) }

    // Format currency to Persian formatted string
    fun formatToman(amount: Long): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(amount).toPersianNumber()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                SubscriptionTopBar(
                    onBackClick = { navController.popBackStack() },
                    onReceiptsClick = { showReceiptsDialog = true }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
            ) {
                // 1. Active Subscription Hero Card
                item {
                    ActiveSubscriptionHeroCard(activeSub = activeSub)
                }

                // 2. Section Header: "انتخاب اشتراک" + Badge "پرداخت امن و سریع"
                item {
                    SubscriptionSectionHeader()
                }

                // 3. Subscription Plan Cards
                items(plans.size) { index ->
                    val plan = plans[index]
                    SubscriptionPlanCard(
                        plan = plan,
                        formattedPrice = formatToman(plan.price),
                        onBuyClick = {
                            selectedPlanToBuy = plan
                            showPaymentSimulator = true
                        }
                    )
                }

                // 4. Coupon Code Section Card
                item {
                    CouponCodeCard(
                        appliedPercent = appliedDiscountPercent,
                        onOpenCouponDialog = { showCouponDialog = true }
                    )
                }

                // 5. Trust & Guarantee Footer
                item {
                    TrustAndGuaranteeFooter()
                }
            }
        }
    }

    // Coupon Code Input Dialog
    if (showCouponDialog) {
        Dialog(onDismissRequest = { showCouponDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Discount,
                            contentDescription = null,
                            tint = Color(0xFF6320EE),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "کد تخفیف دارید؟",
                        fontFamily = IranSansFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "کد تخفیف اختصاصی یا مناسبتی خود را وارد کنید تا روی مبلغ نهایی اعمال شود.",
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = { couponInput = it },
                        placeholder = { Text("مثال: SHETAB20 یا NOROOZ", fontSize = 12.5.sp, color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6320EE),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (couponFeedbackMessage.isNotEmpty()) {
                        Text(
                            text = couponFeedbackMessage,
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCouponSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCouponDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("انصراف", color = Color(0xFF64748B), fontFamily = IranSansFontFamily)
                        }

                        Button(
                            onClick = {
                                val clean = couponInput.trim().uppercase()
                                if (clean.isEmpty()) {
                                    couponFeedbackMessage = "لطفاً ابتدا کد تخفیف را وارد کنید."
                                    isCouponSuccess = false
                                } else if (clean == "SHETAB20" || clean == "NOROOZ" || clean == "VIP" || clean == "OFF20") {
                                    appliedDiscountPercent = 20
                                    couponFeedbackMessage = "کد تخفیف ۲۰٪ ویژه با موفقیت اعمال شد!"
                                    isCouponSuccess = true
                                    coroutineScope.launch {
                                        delay(1200)
                                        showCouponDialog = false
                                    }
                                } else if (clean == "SHETAB50") {
                                    appliedDiscountPercent = 50
                                    couponFeedbackMessage = "کد تخفیف ۵۰٪ شتاب طلایی با موفقیت اعمال شد!"
                                    isCouponSuccess = true
                                    coroutineScope.launch {
                                        delay(1200)
                                        showCouponDialog = false
                                    }
                                } else {
                                    appliedDiscountPercent = 0
                                    couponFeedbackMessage = "کد تخفیف وارد شده معتبر یا فعال نمی‌باشد."
                                    isCouponSuccess = false
                                }
                            },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6320EE))
                        ) {
                            Text("اعمال کد", color = Color.White, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Payment Simulator Dialog
    if (showPaymentSimulator && selectedPlanToBuy != null) {
        val plan = selectedPlanToBuy!!
        val finalAmount = if (appliedDiscountPercent > 0) {
            plan.price - (plan.price * appliedDiscountPercent / 100)
        } else {
            plan.price
        }

        Dialog(onDismissRequest = {
            if (!isProcessingPayment) {
                showPaymentSimulator = false
                isPaymentSuccess = false
            }
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isPaymentSuccess) {
                        // Success View
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "اشتراک شما با موفقیت فعال شد! 🎉",
                            fontFamily = IranSansFontFamily,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "تمام امکانات پریمیوم و آزمون‌های تحلیلی فوراً برای حساب شما فعال گردید.",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                activeSub = activeSub.copy(
                                    planTitle = "پلن ${plan.durationTitle}",
                                    activeUntil = "فعال برای دوره جدید",
                                    remainingDays = when (plan.type) {
                                        PlanType.ONE_MONTH -> 30
                                        PlanType.THREE_MONTHS -> 92
                                        PlanType.TWELVE_MONTHS -> 365
                                    }
                                )
                                showPaymentSimulator = false
                                isPaymentSuccess = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text(
                                text = "بازگشت به برنامه",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        // Checkout Confirmation View
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تأیید سفارش خرید",
                                fontFamily = IranSansFontFamily,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            IconButton(
                                onClick = { showPaymentSimulator = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color(0xFF94A3B8))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("پلن انتخابی:", fontSize = 12.5.sp, color = Color(0xFF64748B))
                                    Text("${plan.durationTitle} (${plan.subtitle})", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("مبلغ پلن:", fontSize = 12.5.sp, color = Color(0xFF64748B))
                                    Text("${formatToman(plan.price)} تومان", fontSize = 12.5.sp, color = Color(0xFF0F172A))
                                }

                                if (appliedDiscountPercent > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("تخفیف اعمال‌شده:", fontSize = 12.5.sp, color = Color(0xFF10B981))
                                        Text("${appliedDiscountPercent.toPersianNumber()}٪ تخفیف", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("مبلغ نهایی پرداخت:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(
                                        text = "${formatToman(finalAmount)} تومان",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF6320EE)
                                    )
                                }
                            }
                        }

                        // Simulated Bank Gateway Button
                        Button(
                            onClick = {
                                isProcessingPayment = true
                                coroutineScope.launch {
                                    delay(1600)
                                    isProcessingPayment = false
                                    isPaymentSuccess = true
                                }
                            },
                            enabled = !isProcessingPayment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6320EE))
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "پرداخت آنلاین و فعال‌سازی آنی",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Receipts / History Dialog
    if (showReceiptsDialog) {
        Dialog(onDismissRequest = { showReceiptsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سوابق پرداخت و فاکتورها",
                            fontFamily = IranSansFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        IconButton(
                            onClick = { showReceiptsDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color(0xFF94A3B8))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("پلن ۳ ماهه نخبگان", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text("موفق", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("تاریخ: ۶ شهریور ۱۴۰۳", fontSize = 11.5.sp, color = Color(0xFF64748B))
                                Text("۲۴۹,۰۰۰ تومان", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            }
                            Text("کد پیگیری: TR-89234710", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Button(
                        onClick = { showReceiptsDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6320EE))
                    ) {
                        Text("متوجه شدم", color = Color.White, fontFamily = IranSansFontFamily)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// UI Sub-Components
// -------------------------------------------------------------

/**
 * Top App Bar with centered title, notification bell on right, and receipt icon on left
 */
@Composable
fun SubscriptionTopBar(
    onBackClick: () -> Unit,
    onReceiptsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right Action in RTL: Back / Notification Bell
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onBackClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "اعلان‌ها",
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Center Title
        Text(
            text = "اشتراک‌ها",
            fontFamily = IranSansFontFamily,
            fontSize = 18.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )

        // Left Action in RTL: Receipts / Invoices Icon in purple-tinted card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFAF9FE),
            border = BorderStroke(1.dp, Color(0xFFEDE9FE)),
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onReceiptsClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = "سوابق فاکتورها",
                    tint = Color(0xFF6320EE),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Hero Card displaying user's currently active subscription status and details
 */
@Composable
fun ActiveSubscriptionHeroCard(activeSub: ActiveSubscriptionInfo) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFFAF9FE),
        border = BorderStroke(1.dp, Color(0xFFEDE9FE)),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Section: Status & Title on Right, Illustrated Crown on Left
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right Info Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dot + "اشتراک فعال شما"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6320EE))
                        )
                        Text(
                            text = "اشتراک فعال شما",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6320EE)
                        )
                    }

                    // Plan Title
                    Text(
                        text = activeSub.planTitle,
                        fontFamily = IranSansFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    // Calendar Expiration Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = activeSub.activeUntil,
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Green "فعال" Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F8F0)
                    ) {
                        Text(
                            text = "فعال",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.5.dp)
                        )
                    }
                }

                // Left Illustrated Glowing Crown Badge
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFEDE9FE),
                                    Color(0xFFF5F3FF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glowing Crown Graphic
                    CrownVectorIcon(
                        size = 38.dp,
                        tint = Color(0xFF6320EE)
                    )

                    // Tiny sparkling stars around crown
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.5f),
                            radius = 3.dp.toPx(),
                            center = Offset(size.width * 0.85f, size.height * 0.25f)
                        )
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.5f),
                            radius = 2.dp.toPx(),
                            center = Offset(size.width * 0.15f, size.height * 0.75f)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = Color(0xFFEDE9FE),
                thickness = 1.dp
            )

            // Bottom 3-Column Meta: Start Date, End Date, Remaining Days
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Start Date (Right in RTL)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "تاریخ شروع",
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = activeSub.startDate,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(Color(0xFFEDE9FE))
                )

                // 2. End Date (Center)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "تاریخ پایان",
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = activeSub.endDate,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(Color(0xFFEDE9FE))
                )

                // 3. Remaining Days (Left in RTL)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "روز باقی‌مانده",
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF6320EE),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${activeSub.remainingDays.toPersianNumber()} روز",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section Header: "انتخاب اشتراک" + "پرداخت امن و سریع"
 */
@Composable
fun SubscriptionSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right: Title & Subtitle in RTL
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "انتخاب اشتراک",
                fontFamily = IranSansFontFamily,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "پلن مناسب خودت رو انتخاب کن",
                fontFamily = IranSansFontFamily,
                fontSize = 11.5.sp,
                color = Color(0xFF64748B)
            )
        }

        // Left: "پرداخت امن و سریع" Badge in RTL
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFAF9FE),
            border = BorderStroke(1.dp, Color(0xFFEDE9FE))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF6320EE),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "پرداخت امن و سریع",
                    fontFamily = IranSansFontFamily,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6320EE)
                )
            }
        }
    }
}

/**
 * Generic Plan Card for 1-Month, 3-Months (Featured), and 12-Months
 */
@Composable
fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    formattedPrice: String,
    onBuyClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (plan.isFeatured) 1.5.dp else 1.dp,
            color = if (plan.isFeatured) Color(0xFFC7D2FE) else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (plan.isFeatured) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Featured Ribbon at the top if applicable
            if (plan.isFeatured && plan.ribbonTitle != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF6320EE)
                    ) {
                        Text(
                            text = plan.ribbonTitle,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.5.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Main Info Row: Title on Right, Button on Left (RTL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right Title & Icon Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Title
                        Text(
                            text = plan.durationTitle,
                            fontFamily = IranSansFontFamily,
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )

                        // Plan Indicator Icon
                        Surface(
                            shape = CircleShape,
                            color = plan.themeColor.copy(alpha = 0.1f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                when (plan.type) {
                                    PlanType.THREE_MONTHS -> {
                                        Icon(
                                            imageVector = Icons.Outlined.StarOutline,
                                            contentDescription = null,
                                            tint = plan.themeColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    PlanType.ONE_MONTH -> {
                                        Icon(
                                            imageVector = Icons.Outlined.StarOutline,
                                            contentDescription = null,
                                            tint = plan.themeColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    PlanType.TWELVE_MONTHS -> {
                                        Icon(
                                            imageVector = Icons.Outlined.Diamond,
                                            contentDescription = null,
                                            tint = plan.themeColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Subtitle
                    Text(
                        text = plan.subtitle,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Left "خرید اشتراک" Button
                if (plan.isFeatured) {
                    // Solid Purple Button
                    Button(
                        onClick = onBuyClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6320EE)),
                        modifier = Modifier
                            .height(38.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "خرید اشتراک",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // Outlined Colored Button (Orange for 1M, Green for 12M)
                    OutlinedButton(
                        onClick = onBuyClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, plan.themeColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = plan.themeColor),
                        modifier = Modifier
                            .height(38.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "خرید اشتراک",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = plan.themeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price + Toman
                Text(
                    text = formattedPrice,
                    fontFamily = IranSansFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (plan.isFeatured) Color(0xFF6320EE) else Color(0xFF0F172A)
                )

                Text(
                    text = " تومان",
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (plan.isFeatured) Color(0xFF6320EE) else Color(0xFF475569),
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Discount Badge if present
                if (plan.discountPercent != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F8F0)
                    ) {
                        Text(
                            text = "${plan.discountPercent.toPersianNumber()}٪ تخفیف",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2-Column Features Grid with Circular Checkmarks
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Item 0 and Item 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (plan.features.isNotEmpty()) {
                        FeatureCheckItem(
                            title = plan.features[0],
                            tint = plan.themeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (plan.features.size > 1) {
                        FeatureCheckItem(
                            title = plan.features[1],
                            tint = plan.themeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2: Item 2 and Item 3
                if (plan.features.size > 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FeatureCheckItem(
                            title = plan.features[2],
                            tint = plan.themeColor,
                            modifier = Modifier.weight(1f)
                        )
                        if (plan.features.size > 3) {
                            FeatureCheckItem(
                                title = plan.features[3],
                                tint = plan.themeColor,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

/**
 * Individual feature check item with a colored circle checkmark
 */
@Composable
fun FeatureCheckItem(
    title: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            fontFamily = IranSansFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF334155)
        )
    }
}

/**
 * Coupon Code Card
 */
@Composable
fun CouponCodeCard(
    appliedPercent: Int,
    onOpenCouponDialog: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFAF9FE),
        border = BorderStroke(1.dp, Color(0xFFEDE9FE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right info & 3D Gift Box Icon in RTL
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Purple 3D Gift Illustration
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CardGiftcard,
                        contentDescription = null,
                        tint = Color(0xFF6320EE),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (appliedPercent > 0) "تخفیف فعال: ${appliedPercent.toPersianNumber()}٪" else "کد تخفیف دارید؟",
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (appliedPercent > 0) Color(0xFF10B981) else Color(0xFF0F172A)
                    )

                    Text(
                        text = "برای وارد کردن کد تخفیف، روی دکمه مقابل بزنید.",
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Left "وارد کردن کد" Button in RTL
            OutlinedButton(
                onClick = onOpenCouponDialog,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF6320EE)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6320EE)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sell,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "وارد کردن کد",
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Trust & Guarantees Footer: 3 items with dividers
 */
@Composable
fun TrustAndGuaranteeFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Cancel anytime (Right in RTL)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Cancel,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "لغو اشتراک در هر زمان",
                fontFamily = IranSansFontFamily,
                fontSize = 10.5.sp,
                color = Color(0xFF64748B)
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(14.dp)
                .background(Color(0xFFE2E8F0))
        )

        // 2. 7-day Refund (Center)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Cached,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "بازگشت وجه تا ۷ روز",
                fontFamily = IranSansFontFamily,
                fontSize = 10.5.sp,
                color = Color(0xFF64748B)
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(14.dp)
                .background(Color(0xFFE2E8F0))
        )

        // 3. Instant Access (Left in RTL)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Bolt,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "دسترسی آنی بعد از خرید",
                fontFamily = IranSansFontFamily,
                fontSize = 10.5.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

/**
 * Vector Crown Icon representation for the Hero Card
 */
@Composable
fun CrownVectorIcon(size: Dp = 32.dp, tint: Color = Color(0xFF6320EE)) {
    Canvas(modifier = Modifier.size(size)) {
        val width = this.size.width
        val height = this.size.height

        val path = Path().apply {
            moveTo(width * 0.12f, height * 0.72f)
            lineTo(width * 0.88f, height * 0.72f)
            lineTo(width * 0.80f, height * 0.32f)
            lineTo(width * 0.62f, height * 0.52f)
            lineTo(width * 0.50f, height * 0.22f)
            lineTo(width * 0.38f, height * 0.52f)
            lineTo(width * 0.20f, height * 0.32f)
            close()
        }

        drawPath(path = path, color = tint)

        // Base rectangle of the crown
        drawRoundRect(
            color = tint,
            topLeft = Offset(width * 0.12f, height * 0.76f),
            size = androidx.compose.ui.geometry.Size(width * 0.76f, height * 0.10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
    }
}
