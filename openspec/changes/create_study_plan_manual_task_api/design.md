# Technical Design: Refinement of Create Study Plan UI & Manual Task Creation API Integration

## 1. Architecture & Layers

### Data Layer (`network/ApiService.kt`)
- Define `CreateManualStudyTaskDto` and `ManualStudyTaskResponseDto`:
  ```kotlin
  @JsonClass(generateAdapter = true)
  data class CreateManualStudyTaskDto(
      val requestId: String,
      val topicId: String,
      val scheduledOn: String, // YYYY-MM-DD
      val periodCount: Int,
      val minutesPerPeriod: Int,
  )

  @JsonClass(generateAdapter = true)
  data class ManualStudyTaskResponseDto(
      val id: String,
      val requestId: String?,
      val sourceType: String?,
      val title: String?,
      val scheduledOn: String?,
      val book: CatalogBookReferenceDto?,
      val chapter: CatalogChapterReferenceDto?,
      val topic: CatalogTopicReferenceDto?,
      val periodCount: Int,
      val minutesPerPeriod: Int,
      val plannedMinutes: Int,
      val status: String?,
      val createdAt: String?,
      val updatedAt: String?,
  )
  ```
- Endpoint method:
  ```kotlin
  @POST("study-tasks/me/manual")
  suspend fun createManualStudyTask(
      @Body body: CreateManualStudyTaskDto,
  ): ApiResponse<ManualStudyTaskResponseDto>
  ```

### State Management (`CreateStudyPlanViewModel`)
- Multi-chapter selection data model:
  ```kotlin
  data class SelectedChapterBlock(
      val blockId: String = UUID.randomUUID().toString(),
      val selectedChapterId: String? = null,
      val selectedTopicIds: Set<String> = emptySet(),
  )
  ```
- UI State enhancements:
  ```kotlin
  data class CreateStudyPlanUiState(
      val isManualTiming: Boolean = false,
      val chapterBlocks: List<SelectedChapterBlock> = listOf(SelectedChapterBlock()),
      ...
  )
  ```
- Creation handler `createManualTask()`:
  - Iterates over all selected topics (or creates the batch of manual tasks).
  - Validates `scheduledOn`, `periodCount`, and `minutesPerPeriod`.
  - Sends requests with unique `UUID.randomUUID().toString()`.
  - Handles response codes 201, 400, 404, 409 with localized Persian error messages.

### Presentation Layer (`CreateStudyPlanScreen.kt` & Composables)
1. **Header:**
   - Remove subtitle. Display only clean centered title "ایجاد برنامه مطالعاتی".
2. **Book Selector:**
   - Simplify names (e.g., "فیزیک ۱" -> "فیزیک", "ریاضی و آمار ۱" -> "ریاضی و آمار").
3. **Chapter Dropdown:**
   - Anchor `DropdownMenu` with `Box` and `Modifier.fillMaxWidth()` directly below the selector.
   - Clean card styling with soft shadows and no green border.
4. **Multiple Chapter Blocks:**
   - Render a list of chapter & topic cards based on `state.chapterBlocks`.
   - "اضافه کردن فصل" appends a new `SelectedChapterBlock`.
5. **Pixel-Perfect Timing Component:**
   - Toggle labeled "زمان‌بندی دستی".
   - If `!isManualTiming`, the slider body is hidden with smooth animation.
   - If `isManualTiming`, custom Canvas/Surface slider matching the mockup:
     - Capsule track with active purple segment, inactive gray segment, and dot indicators.
     - Vertical pill thumb.
     - LTR layout direction.
