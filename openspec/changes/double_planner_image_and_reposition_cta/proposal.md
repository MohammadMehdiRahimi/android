# Proposal: Double Smart Planner 3D Illustration Size & Reposition CTA to Right Side

## 1. Problem Statement & User Intent
The user requested two specific design refinements for the Smart Planner card (`FeatureCardSmartPlan`) on the Home Dashboard:
1. **Double the Image Size**: The 3D target and dart illustration (`home_plan_dart`) should be doubled in size so it becomes a super prominent, immersive hero visual on the card.
2. **Move "شروع کنید" Button to Right (Start in RTL) with Arrow on Right**:
   - Move the white pill CTA container to the right side (Start in RTL / `Alignment.BottomStart`).
   - Place the arrow icon to the right (Start in RTL) of the text "شروع کنید" inside the button.

---

## 2. Acceptance Criteria
1. The 3D dart/target illustration in `FeatureCardSmartPlan` is enlarged (approx. 2x) and cropped/scaled smoothly inside the card container.
2. The "شروع کنید" pill button is positioned at `BottomStart` (the right side in Persian RTL).
3. The arrow icon inside the pill button is placed to the right of the text "شروع کنید".
4. Compose tests in `HomeScreenTest.kt` pass and app builds cleanly.
