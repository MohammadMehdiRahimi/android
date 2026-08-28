# Technical Design: Double Smart Planner Image & Reposition CTA to Right Side

## 1. UI & Layout Specifications

### 1.1 Doubled 3D Target & Dart Image Scaling
- Increase the dimension from `195.dp` to `320.dp` - `360.dp` (effectively ~2x scaling).
- Use `Alignment.Center` or `Alignment.BottomCenter` with negative offsets to center the massive 3D pedestal and bullseye dynamically within the card clipping boundaries (`RoundedCornerShape(24.dp)`).
- Ensure background gradient remains visible around edges.

### 1.2 "شروع کنید" Pill Button Alignment & Internal Order
- **Card Placement**:
  - Change `.align(Alignment.BottomEnd)` to `.align(Alignment.BottomStart)` so it is situated at the right side in RTL.
- **Internal Row Order**:
  - In RTL, to place the arrow to the right of the text:
    - Put the `Icon` FIRST in the `Row`, followed by `Text("شروع کنید")`.
    - In an RTL layout, the first element appears at the Right (Start).
    - Use `Icons.AutoMirrored.Filled.KeyboardArrowLeft` or `Icons.AutoMirrored.Filled.KeyboardArrowRight` appropriately so it points in the intended navigation direction while sitting on the right of the text.

---

## 2. Testing Matrix
- `HomeScreenTest.kt`:
  - Assert "شروع کنید" button and "برنامه‌ریز هوشمند شتاب" text remain displayed and clickable.
