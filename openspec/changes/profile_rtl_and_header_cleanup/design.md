# Technical Design: Profile RTL Alignment and Header Removal

## 1. Presentation Layer Changes (`ProfileScreen.kt`)

### 1.1 RTL Direction Enforcement
- Wrap the main Profile content in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.

### 1.2 Top Header Removal
- Delete the `Row` holding the notification bell icon box and mini avatar with online dot.

### 1.3 Hero Card RTL Ordering
In RTL (`LayoutDirection.Rtl`):
- `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)`
  - **Left element in Row declaration** (which appears on the **RIGHT** visually in RTL):
    - User Details `Column`: Name with pencil icon, Grade row with `School` icon, Field row with `MenuBook` icon.
  - **Right element in Row declaration** (which appears on the **LEFT** visually in RTL) or vice-versa according to mockup:
    - Looking at `profile.png`:
      - User Avatar is on the **Top-Right** side of the card!
      - User Name, Grade, Field are on the **Top-Left** side of the card!
    - Under `LayoutDirection.Rtl`, the first child in a `Row` is rendered on the **RIGHT** (Start). Therefore, the **Avatar** is placed FIRST in the `Row`, and the **User Details Column** is placed SECOND in the `Row` (with `weight(1f)`).
    - In the User Details Column:
      - Name + Pencil icon: Text on Right (Start), Pencil on Left.
      - Grade row: `Icon(School)` on Right (Start), `Text("پایه دوازدهم")` next to it.
      - Field row: `Icon(MenuBook)` on Right (Start), `Text("رشته تجربی")` next to it.

### 1.4 Personal Information Card RTL
- In RTL:
  - Label (`"نام و نام خانوادگی"`) on the Right (Start).
  - Value (`"علی محمدی"`) + `KeyboardArrowLeft` on the Left (End).

### 1.5 Account Action Cards RTL
- In RTL:
  - Icon square box + (Title & Subtitle) on the Right (Start).
  - `KeyboardArrowLeft` chevron on the Left (End).
- Card Section Headers:
  - `Icon` on Right (Start), `Text("اطلاعات شخصی")` on Left of icon.
  - `Icon` on Right (Start), `Text("حساب کاربری")` on Left of icon.

---

## 2. Testing
- Run `compile_applet` and check RTL rendering alignment.
