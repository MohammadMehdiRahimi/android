# Technical Design: Smart Study Plan Card Redesign & Home Layout Polish

## 1. Visual Specification (Based on Provided Image)

### 1.1 Tall Study Planner Card (`FeatureCardSmartPlan`)
- **Container**:
  - Shape: `RoundedCornerShape(24.dp)`
  - Background: Gradient `Brush.verticalGradient` / `Brush.linearGradient` using rich purple tones (`Color(0xFF8B5CF6)` to `Color(0xFF7C3AED)` or `Color(0xFF9333EA)`).
  - Shadow / Elevation: Subtle soft purple ambient shadow.
- **Header Section (Top-End in RTL)**:
  - Title:
    - Text: `"برنامه ریز\nهوشمند شتاب"`
    - Color: `Color.White`
    - Font: IranSans / Vazirmatn Bold (18.sp), line height 24.sp.
  - Subtitle:
    - Text: `"برنامه‌ریزی هوشمند\nو پیشرفت سؤال"`
    - Color: `Color.White.copy(alpha = 0.88f)`
    - Font: IranSans / Vazirmatn Medium (11.5sp), line height 16.sp.
- **Visual Graphic Element**:
  - Target / Bullseye concentric ring illustration with dart hitting center on an illuminated pedestal (clean Compose Canvas / Vector asset or drawable).
- **CTA Action Button**:
  - Position: Bottom-end corner.
  - Shape: `RoundedCornerShape(20.dp)` (Capsule / Pill).
  - Background: Pure White `Color.White` with soft shadow.
  - Content:
    - Text: `"شروع کنید"` in `Color(0xFF7C3AED)` (Bold, 12.sp).
    - Icon: `Icons.AutoMirrored.Filled.KeyboardArrowLeft` tinted `Color(0xFF7C3AED)`.

---

## 2. Layout & Spacing Adjustments

### 2.1 League Card (`FeatureCardLeague`)
- Update text from `"لایـک‌های رقابتی فعال"` to `"لیگ‌های رقابتی"`.

### 2.2 Performance Chart (`PerformanceChartCard`)
- Title updated to `"نمای کلی"`.
- Top margin / padding above the chart card reduced from `16.dp` to `6.dp` - `8.dp` to pull the chart upwards.

---

## 3. Testing Matrix
- `HomeScreenTest.kt`:
  - Assert `"نمای کلی"` is displayed.
  - Assert `"برنامه ریز"` & `"هوشمند شتاب"` are displayed.
  - Assert `"شروع کنید"` CTA button is displayed.
  - Assert `"لیگ‌های رقابتی"` is displayed.
