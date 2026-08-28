# Proposal: Profile Screen Strict RTL Alignment and Header Removal

## 1. Problem Statement & User Intent
The user specified two key requirements for the Profile Screen:
1. **Strict and Pure RTL Layout Structure**:
   - The entire Profile Screen must be strictly and correctly aligned in Right-to-Left (RTL) mode.
   - Enforce `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
   - **Hero Card**: Large User Avatar with camera badge must be on the Right (`Start` in RTL), and user text details (Full name with edit pencil icon, Academic Grade with graduation cap icon, Field of study with book icon) must be on the Left (`End` in RTL).
   - **Personal Information Rows ("اطلاعات شخصی")**: Row title on the Right (`Start` in RTL), value + left chevron arrow on the Left (`End` in RTL).
   - **User Account Cards ("حساب کاربری")**: Action icon box and titles on the Right (`Start` in RTL), and left chevron arrow on the Left (`End` in RTL).
2. **Removal of Top Header**:
   - Completely remove the top header containing the mini online avatar and the notification bell container. The screen starts cleanly from the top padding directly with the Hero Profile Card.

---

## 2. Acceptance Criteria
1. Profile Screen is wrapped in strict `LayoutDirection.Rtl`.
2. Top header (bell & mini avatar) is completely removed.
3. Hero Card has user avatar on the Right and name/grade/field details on the Left.
4. Personal info rows and account action cards align properly in RTL.
5. Automated compilation and tests succeed.
