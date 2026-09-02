# Proposal: Focus Timer Cleanup

## What
Remove the "Finish Task" button, its associated modal, the settings button, settings modal, and the secondary timer UI (`LUXURY_FULLSCREEN` style) from the Focus Timer screen.

## Why
To simplify the user interface, reduce cognitive load during focus sessions, and streamline the timer experience by maintaining a single, consistent design language (the Circular Modern Timer).

## Acceptance Criteria
- The "Finish Task" (اتمام تسک) button is no longer visible on the Focus Timer screen.
- The task completion bottom sheet modal is removed.
- The Settings gear icon/button is removed from all header and control sections.
- The Settings bottom sheet modal is removed.
- The `LUXURY_FULLSCREEN` timer style is removed, defaulting exclusively to the Circular Modern style without any condition checks.
