# Proposal: Create Study Plan UI Polish & Inset Fixes

## 1. Overview & Problem Statement
Following feedback on the Create Study Plan screen:
1. The top header title ("ایجاد برنامه مطالعاتی") needs to be positioned higher up with cleaner top spacing.
2. The bottom fixed action bar ("ثبت و ذخیره برنامه") is currently obscured by the Android system navigation bar (3-button navigation overlay).
3. The chapter selector dropdown previously took over the screen awkwardly. It must open a stylish, scrollable, max-height bounded selection card / popup matching the app's clean card styling without covering the entire screen.
4. The manual timing sliders need precise styling:
   - **Thumb (Handle):** Circular Ring / Donut Thumb (purple outer border with solid white center).
   - **Track:** Thin neutral gray baseline track.
   - **Progress / Filled Track:** Active filled track in purple.
   - **Marks / Step Indicators:** Fine vertical tick marks indicating discrete steps.
   - **Mark Labels:** Persian numbers (۵، ۱۰، ۱۵، ...) clearly aligned under the track.
5. The study period count stepper needs visual refinement with smaller `+` and `-` circle action buttons.
6. Selected topic chips should only display a green checkmark (`#22C55E`) without changing the border color or bolding the font.

## 2. Acceptance Criteria
- [ ] Top bar is aligned higher up with optimized status bar / top padding.
- [ ] Bottom action bar adheres to window insets (`navigationBarsPadding()`) and ensures the button is 100% visible above system buttons.
- [ ] Chapter selection uses a dedicated, compact, scrollable dropdown/dialog with maximum height constraints and matching card theme.
- [ ] Custom slider accurately renders the Donut Thumb, thin progress track, tick step indicators, and bottom Persian labels.
- [ ] Study period stepper buttons are compact (26-28dp) with refined spacing.
- [ ] Topic selection renders a green checkmark badge while keeping neutral borders and regular text weight.
