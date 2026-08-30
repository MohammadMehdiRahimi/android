# Design: طراحی و مشخصات فنی صفحه اشتراک‌ها

## 1. UI Components & Layout Hierarchy

### 1.1 Top App Bar
- ساختار Row با توزیع `Arrangement.SpaceBetween`.
- آیکون سمت راست (در چیدمان راست‌به‌چپ): آیکون زنگوله نوتیفیکیشن با امکان بازگشت یا اعلان‌ها.
- در مرکز: متن «اشتراک‌ها» با اندازه `19.sp` و وزن `FontWeight.ExtraBold`.
- در سمت چپ: آیکون رسید/فاکتور درون کادر بنفش ملایم.

### 1.2 Active Subscription Hero Card (`ActiveSubscriptionCard`)
- پس‌زمینه: `Color(0xFFFAF9FE)` با بوردر `1.dp` به رنگ `Color(0xFFEDE9FE)` و انحنای `20.dp`.
- بخش بالا:
  - راست: تگ نقطه بنفش «اشتراک فعال شما»، عنوان «پلن ۳ ماهه» (`20.sp`, `FontWeight.Bold`)، تاریخ انقضا با آیکون تقویم و بج سبز «فعال» (`Color(0xFFE8F8F0)` با متن سبز `#10B981`).
  - چپ: دایره ملایم بنفش (`Color(0xFFEDE9FE)`) با آیکون تاج بنفش برجسته و ستاره‌های درخشان گرداگرد آن.
- بخش پایین:
  - سه ستون با خطوط جداکننده نازک:
    1. تاریخ شروع: «۶ شهریور ۱۴۰۳»
    2. تاریخ پایان: «۶ آذر ۱۴۰۳»
    3. روز باقی‌مانده: «۹۲ روز»

### 1.3 Plan Selection Cards (`SubscriptionPlanCard`)
- **پلن ۳ ماهه (ویژه):**
  - کادر با بوردر بنفش ملایم و بج ریبون بالا «پیشنهاد ویژه ★».
  - قیمت بولد بنفش «۲۴۹,۰۰۰ تومان» با بج سبز «۱۷٪ تخفیف».
  - دکمه بنفش توپر «خرید اشتراک».
  - ۲ سطر از ویژگی‌ها با چک‌مارک‌های بنفش دایره‌ای.
- **پلن ۱ ماهه:**
  - قیمت «۸۹,۰۰۰ تومان» با زیرعنوان «مناسب برای شروع».
  - دکمه اوت‌لاین نارنجی `Color(0xFFFF7A00)` «خرید اشتراک».
  - چک‌مارک‌های نارنجی.
- **پلن ۱۲ ماهه:**
  - آیکون الماس سبز، قیمت «۸۹۹,۰۰۰ تومان»، بج «۳۳٪ تخفیف».
  - دکمه اوت‌لاین سبز `Color(0xFF10B981)` «خرید اشتراک».
  - چک‌مارک‌های سبز.

### 1.4 Coupon Code Card (`DiscountCouponCard`)
- کادر با پس‌زمینه سفید متمایل به بنفش بسیار روشن.
- دکمه بنفش لاین با آیکون تگ «وارد کردن کد».
- متن راهنما و آیکون جعبه کادوی بنفش ۳بعدی.

### 1.5 Trust & Guarantee Row
- ردیف سه‌گانه با آیکون‌های:
  - لغو اشتراک در هر زمان (Cancel icon)
  - بازگشت وجه تا ۷ روز (Refund 7 days icon)
  - دسترسی آنی بعد از خرید (Instant access lightning icon)

## 2. State Management & Data Flow
- مدل داده `SubscriptionPlanItem`:
  - `id: String`, `durationTitle: String`, `subtitle: String`, `price: Long`, `discountPercent: Int`, `isFeatured: Boolean`, `themeColor: Color`, `features: List<String>`, `iconType: PlanIconType`
- مدل داده `UserActiveSubscription`:
  - `planName: String`, `isActive: Boolean`, `startDate: String`, `endDate: String`, `remainingDays: Int`
- قابلیت باز شدن دیالوگ شبیه‌ساز پرداخت شتاب و اعمال کد تخفیف با بازخورد تصویری.
