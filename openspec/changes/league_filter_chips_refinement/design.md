# Design: جزئیات طراحی فیلترهای لیگ

## 1. Filter Chips Row Styling
- **ارتفاع باکس چیپ:** `height = 36.dp`
- **پدینگ افقی درون چیپ:** `horizontal = 20.dp`
- **رنگ پس‌زمینه انتخاب‌شده (Selected):** رنگ بنفش غنی‌تر `Color(0xFF5B21B6)` یا `Color(0xFF6D28D9)` یا گرادینت بنفش شاداب `Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)))`.
- **رنگ متن و آیکون:** سفید خالص با فونت سایز `12.5.sp` و وزن بولد.
- **حالت عادی (Unselected):** پس‌زمینه سفید با بوردر ملایم `Color(0xFFE2E8F0)` و رنگ متن `Color(0xFF64748B)`.
