package com.example.ui.features.studyplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.ApiClient
import com.example.network.CreateManualStudyTaskDto
import com.example.network.NetworkResult
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.TokenManager
import com.example.network.safeApiCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

data class SubjectVisualItem(
    val id: String,
    val name: String,
    val minimalName: String,
    val fieldName: String = "رشته تجربی",
    val drawableRes: Int,
    val tintHex: Long,
    val chapters: List<ChapterVisualItem> = emptyList(),
)

data class ChapterVisualItem(
    val id: String,
    val name: String,
    val topics: List<TopicVisualItem> = emptyList(),
)

data class TopicVisualItem(
    val id: String,
    val name: String,
)

data class ChapterBlockState(
    val blockId: String = UUID.randomUUID().toString(),
    val selectedChapterId: String = "",
    val selectedTopicIds: Set<String> = emptySet(),
)

data class CreateStudyPlanUiState(
    val userMajor: String = "EXPERIMENTAL",
    val userMajorName: String = "رشته تجربی",
    val selectedGrade: String = "GRADE_12",
    val selectedGradeName: String = "پایه دوازدهم",
    val grades: List<Pair<String, String>> = listOf(
        "GRADE_12" to "پایه دوازدهم",
        "GRADE_11" to "پایه یازدهم",
        "GRADE_10" to "پایه دهم",
    ),
    val isLoadingCatalog: Boolean = true,
    val subjects: List<SubjectVisualItem> = emptyList(),
    val selectedSubjectId: String = "",
    val chapterBlocks: List<ChapterBlockState> = emptyList(),
    val periodCount: Int = 3,
    val isManualTiming: Boolean = false,
    val studyDurationMinutes: Int = 45,
    val breakDurationMinutes: Int = 15,
    val isSummaryModalVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val selectedSubject: SubjectVisualItem?
        get() = subjects.firstOrNull { it.id == selectedSubjectId } ?: subjects.firstOrNull()

    val allSelectedTopicIds: Set<String>
        get() = chapterBlocks.flatMap { it.selectedTopicIds }.toSet()

    val selectedTopicCount: Int
        get() = allSelectedTopicIds.size

    val totalEstimatedMinutes: Int
        get() = periodCount * (studyDurationMinutes + breakDurationMinutes)

    val totalHours: Int
        get() = totalEstimatedMinutes / 60

    val remainingMinutes: Int
        get() = totalEstimatedMinutes % 60
}

sealed interface CreateStudyPlanEvent {
    object PlanSaved : CreateStudyPlanEvent
    data class ShowError(val message: String) : CreateStudyPlanEvent
}

object StudyPlanCatalogCache {
    private val cache = java.util.concurrent.ConcurrentHashMap<String, List<SubjectVisualItem>>()

    fun get(gradeKey: String, majorKey: String): List<SubjectVisualItem>? {
        return cache["${gradeKey}_${majorKey}"]
    }

    fun put(gradeKey: String, majorKey: String, subjects: List<SubjectVisualItem>) {
        cache["${gradeKey}_${majorKey}"] = subjects
    }

    fun clear() {
        cache.clear()
    }
}

class CreateStudyPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.apiService
    private val tokenManager = TokenManager(application)

    private val _state = MutableStateFlow(CreateStudyPlanUiState())
    val state: StateFlow<CreateStudyPlanUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateStudyPlanEvent>()
    val events: SharedFlow<CreateStudyPlanEvent> = _events.asSharedFlow()

    init {
        val savedMajor = tokenManager.getUserMajor()
        val majorName = mapMajorToPersian(savedMajor)
        val initialGrade = _state.value.selectedGrade
        val defaultSubjects = buildDefaultSubjects(majorName)
        val cached = StudyPlanCatalogCache.get(initialGrade, savedMajor ?: "EXPERIMENTAL")
        val effectiveSubjects = if (!cached.isNullOrEmpty()) cached else defaultSubjects
        val firstSub = effectiveSubjects.first()
        val initialBlock = ChapterBlockState(
            blockId = "init_block_1",
            selectedChapterId = "",
            selectedTopicIds = emptySet(),
        )

        _state.update {
            it.copy(
                userMajor = savedMajor ?: "EXPERIMENTAL",
                userMajorName = majorName,
                subjects = effectiveSubjects,
                selectedSubjectId = firstSub.id,
                chapterBlocks = listOf(initialBlock),
                isLoadingCatalog = cached == null,
            )
        }

        if (cached == null) {
            loadCatalog(initialGrade)
        }
    }

    private fun cleanBookNameToMinimal(raw: String): String {
        return raw
            .replace(" ۱", "")
            .replace(" ۲", "")
            .replace(" ۳", "")
            .replace(" 1", "")
            .replace(" 2", "")
            .replace(" 3", "")
            .trim()
    }

    private fun mapMajorToPersian(raw: String?): String {
        return when (raw?.uppercase()) {
            "MATHEMATICS", "MATH", "ریاضی" -> "رشته ریاضی و فیزیک"
            "HUMANITIES", "HUMAN", "انسانی" -> "رشته علوم انسانی"
            "VOCATIONAL", "TECHNICAL", "فنی" -> "رشته فنی و حرفه‌ای"
            else -> "رشته تجربی"
        }
    }

    private fun loadCatalog(targetGrade: String = _state.value.selectedGrade) {
        viewModelScope.launch {
            val userMajor = _state.value.userMajor
            val userMajorName = _state.value.userMajorName
            val defaultSubjects = buildDefaultSubjects(userMajorName)
            val result = safeApiCall { api.getStudyTaskCatalog() }
            if (result is NetworkResult.Success && result.data.body != null) {
                val catalogBody = result.data.body
                val profileField = catalogBody.academicProfile?.fieldOfStudy
                val effectiveMajorName = if (!profileField.isNullOrBlank()) {
                    val mapped = mapMajorToPersian(profileField)
                    tokenManager.saveUserAcademicInfo(profileField, catalogBody.academicProfile.grade)
                    mapped
                } else {
                    userMajorName
                }

                val mappedSubjects = mapCatalogToSubjects(catalogBody, effectiveMajorName)
                if (mappedSubjects.isNotEmpty()) {
                    StudyPlanCatalogCache.put(targetGrade, userMajor, mappedSubjects)
                    val firstSubject = mappedSubjects.first()
                    val initialBlock = ChapterBlockState(
                        blockId = UUID.randomUUID().toString(),
                        selectedChapterId = "",
                        selectedTopicIds = emptySet(),
                    )

                    _state.update {
                        it.copy(
                            isLoadingCatalog = false,
                            userMajorName = effectiveMajorName,
                            subjects = mappedSubjects,
                            selectedSubjectId = firstSubject.id,
                            chapterBlocks = listOf(initialBlock),
                        )
                    }
                    return@launch
                }
            }

            // Fallback to rich default educational catalog based on user's field
            StudyPlanCatalogCache.put(targetGrade, userMajor, defaultSubjects)
            val firstSub = defaultSubjects.first()
            val initialBlock = ChapterBlockState(
                blockId = UUID.randomUUID().toString(),
                selectedChapterId = "",
                selectedTopicIds = emptySet(),
            )
            _state.update {
                it.copy(
                    isLoadingCatalog = false,
                    subjects = defaultSubjects,
                    selectedSubjectId = firstSub.id,
                    chapterBlocks = listOf(initialBlock),
                )
            }
        }
    }

    private fun buildDefaultSubjects(majorName: String): List<SubjectVisualItem> {
        return when {
            majorName.contains("ریاضی") -> listOf(
                SubjectVisualItem(
                    id = "math_hesaban",
                    name = "حسابان ۱",
                    minimalName = "حسابان",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_calc,
                    tintHex = 0xFFEC4899,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "hes_chap_1",
                            name = "فصل اول: تابع و مدل‌سازی",
                            topics = listOf(
                                TopicVisualItem("hes_top_1", "گفتار ۱: ویژگی‌های تابع"),
                                TopicVisualItem("hes_top_2", "گفتار ۲: رسم و وارون"),
                                TopicVisualItem("hes_top_3", "جمع‌بندی و تست"),
                            ),
                        ),
                        ChapterVisualItem(
                            id = "hes_chap_2",
                            name = "فصل دوم: مثلثات و اتحادها",
                            topics = listOf(
                                TopicVisualItem("hes_top_2_1", "گفتار ۱: دایره مثلثاتی"),
                                TopicVisualItem("hes_top_2_2", "گفتار ۲: معادلات مثلثاتی"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "math_physics",
                    name = "فیزیک ۱",
                    minimalName = "فیزیک",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_atom,
                    tintHex = 0xFF3B82F6,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "phy_m_chap_1",
                            name = "فصل اول: اندازه‌گیری و چگالی",
                            topics = listOf(
                                TopicVisualItem("phy_m_top_1", "گفتار ۱: کمیت‌ها و یکاها"),
                                TopicVisualItem("phy_m_top_2", "گفتار ۲: خطا و ارقام بامعنی"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "math_geometry",
                    name = "هندسه ۱",
                    minimalName = "هندسه",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_calc,
                    tintHex = 0xFF8B5CF6,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "geo_chap_1",
                            name = "فصل اول: ترسیم‌های هندسی",
                            topics = listOf(
                                TopicVisualItem("geo_top_1", "گفتار ۱: زاویه و تعامد"),
                                TopicVisualItem("geo_top_2", "گفتار ۲: تالس و تشابه"),
                            ),
                        ),
                    ),
                ),
            )
            majorName.contains("انسانی") -> listOf(
                SubjectVisualItem(
                    id = "human_math",
                    name = "ریاضی و آمار ۱",
                    minimalName = "ریاضی و آمار",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_calc,
                    tintHex = 0xFFEC4899,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "h_math_chap_1",
                            name = "فصل اول: معادله درجه دوم",
                            topics = listOf(
                                TopicVisualItem("h_math_top_1", "حل معادله به روش تجزیه"),
                                TopicVisualItem("h_math_top_2", "حل به روش مربع کامل"),
                                TopicVisualItem("h_math_top_3", "فرمول کلی (دلتا)"),
                            ),
                        ),
                        ChapterVisualItem(
                            id = "h_math_chap_2",
                            name = "فصل دوم: تابع و نمودارها",
                            topics = listOf(
                                TopicVisualItem("h_math_top_2_1", "مفهوم تابع و بازه‌ها"),
                                TopicVisualItem("h_math_top_2_2", "توابع خطی و درجه دو"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "human_history",
                    name = "تاریخ ۱",
                    minimalName = "تاریخ",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_leaf,
                    tintHex = 0xFFF59E0B,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "hist_chap_1",
                            name = "فصل اول: تاریخ‌شناسی و کاوش",
                            topics = listOf(
                                TopicVisualItem("hist_top_1", "درس اول: تاریخ و کاوشگری"),
                                TopicVisualItem("hist_top_2", "درس دوم: زمان و گاه‌شماری"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "human_literature",
                    name = "علوم و فنون ادبی ۱",
                    minimalName = "علوم و فنون",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_flask,
                    tintHex = 0xFF6366F1,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "lit_chap_1",
                            name = "فصل اول: کلیات زبان و ادبیات",
                            topics = listOf(
                                TopicVisualItem("lit_top_1", "درس اول: تاریخ ادبیات پیش از اسلام"),
                                TopicVisualItem("lit_top_2", "درس دوم: پایه‌های آوایی شعر"),
                            ),
                        ),
                    ),
                ),
            )
            else -> listOf(
                SubjectVisualItem(
                    id = "biology",
                    name = "زیست‌شناسی ۱",
                    minimalName = "زیست‌شناسی",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_leaf,
                    tintHex = 0xFF22C55E,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "bio_chap_1",
                            name = "فصل اول: مولکول‌های اطلاعاتی",
                            topics = listOf(
                                TopicVisualItem("bio_top_1", "گفتار ۱: ساختار DNA و ژنتیک"),
                                TopicVisualItem("bio_top_2", "گفتار ۲: همانندسازی و رونویسی"),
                                TopicVisualItem("bio_top_summary", "جمع‌بندی و مرور نکات تستی"),
                            ),
                        ),
                        ChapterVisualItem(
                            id = "bio_chap_2",
                            name = "فصل دوم: جریان اطلاعات در یاخته",
                            topics = listOf(
                                TopicVisualItem("bio_top_2_1", "گفتار ۱: ترجمه و ساخت پروتئین"),
                                TopicVisualItem("bio_top_2_2", "گفتار ۲: تنظیم بیان ژن"),
                                TopicVisualItem("bio_top_2_summary", "جمع‌بندی فصل ۲"),
                            ),
                        ),
                        ChapterVisualItem(
                            id = "bio_chap_3",
                            name = "فصل سوم: انتقال مواد در گیاهان",
                            topics = listOf(
                                TopicVisualItem("bio_top_3_1", "گفتار ۱: بافت‌های آوندی"),
                                TopicVisualItem("bio_top_3_2", "گفتار ۲: تعرق و صعود شیره خام"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "physics",
                    name = "فیزیک ۱",
                    minimalName = "فیزیک",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_atom,
                    tintHex = 0xFF3B82F6,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "phy_chap_1",
                            name = "فصل اول: حرکت بر خط راست",
                            topics = listOf(
                                TopicVisualItem("phy_top_1", "گفتار ۱: شناخت حرکت"),
                                TopicVisualItem("phy_top_2", "گفتار ۲: شتاب ثابت"),
                                TopicVisualItem("phy_top_summary", "جمع‌بندی و تست"),
                            ),
                        ),
                        ChapterVisualItem(
                            id = "phy_chap_2",
                            name = "فصل دوم: دینامیک و حرکت دایره‌ای",
                            topics = listOf(
                                TopicVisualItem("phy_top_2_1", "گفتار ۱: قوانین نیوتون"),
                                TopicVisualItem("phy_top_2_2", "گفتار ۲: اصطکاک و کشش"),
                                TopicVisualItem("phy_top_2_summary", "جمع‌بندی"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "chemistry",
                    name = "شیمی ۱",
                    minimalName = "شیمی",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_flask,
                    tintHex = 0xFFF59E0B,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "chem_chap_1",
                            name = "فصل اول: مولکول‌ها در خدمت تندرستی",
                            topics = listOf(
                                TopicVisualItem("chem_top_1", "گفتار ۱: اسیدها و بازها"),
                                TopicVisualItem("chem_top_2", "گفتار ۲: پاک‌کننده‌ها"),
                                TopicVisualItem("chem_top_summary", "جمع‌بندی و تست"),
                            ),
                        ),
                    ),
                ),
                SubjectVisualItem(
                    id = "math",
                    name = "ریاضی ۱",
                    minimalName = "ریاضی",
                    fieldName = majorName,
                    drawableRes = com.example.R.drawable.ic_subject_calc,
                    tintHex = 0xFFEC4899,
                    chapters = listOf(
                        ChapterVisualItem(
                            id = "math_chap_1",
                            name = "فصل اول: تابع و معادلات",
                            topics = listOf(
                                TopicVisualItem("math_top_1", "گفتار ۱: دامنه و برد"),
                                TopicVisualItem("math_top_2", "گفتار ۲: وارون تابع"),
                                TopicVisualItem("math_top_summary", "جمع‌بندی و تست"),
                            ),
                        ),
                    ),
                ),
            )
        }
    }

    private fun mapCatalogToSubjects(catalog: StudyTaskCatalogBodyDto, majorName: String): List<SubjectVisualItem> {
        val defaults = buildDefaultSubjects(majorName)
        return catalog.books.map { book ->
            val match = defaults.firstOrNull { it.name.contains(book.name) || book.name.contains(it.name) || it.minimalName == cleanBookNameToMinimal(book.name) }
            val minimal = cleanBookNameToMinimal(book.name)
            SubjectVisualItem(
                id = book.id,
                name = book.name,
                minimalName = minimal.ifBlank { book.name },
                fieldName = majorName,
                drawableRes = match?.drawableRes ?: com.example.R.drawable.ic_subject_leaf,
                tintHex = match?.tintHex ?: 0xFF6C5CE7,
                chapters = book.chapters.map { chapter ->
                    ChapterVisualItem(
                        id = chapter.id,
                        name = chapter.name,
                        topics = chapter.topics.map { topic ->
                            TopicVisualItem(id = topic.id, name = topic.name)
                        },
                    )
                },
            )
        }
    }

    fun selectGrade(gradeKey: String, gradeName: String) {
        val userMajor = _state.value.userMajor
        val userMajorName = _state.value.userMajorName
        val defaultSubjects = buildDefaultSubjects(userMajorName)
        val cached = StudyPlanCatalogCache.get(gradeKey, userMajor)
        val effectiveSubjects = if (!cached.isNullOrEmpty()) cached else defaultSubjects
        val firstSub = effectiveSubjects.first()
        val initialBlock = ChapterBlockState(
            blockId = UUID.randomUUID().toString(),
            selectedChapterId = "",
            selectedTopicIds = emptySet(),
        )

        _state.update {
            it.copy(
                selectedGrade = gradeKey,
                selectedGradeName = gradeName,
                subjects = effectiveSubjects,
                selectedSubjectId = firstSub.id,
                chapterBlocks = listOf(initialBlock),
                isLoadingCatalog = cached == null,
            )
        }

        if (cached == null) {
            loadCatalog(gradeKey)
        }
    }

    fun selectSubject(subjectId: String) {
        _state.update { current ->
            val initialBlock = ChapterBlockState(
                blockId = UUID.randomUUID().toString(),
                selectedChapterId = "",
                selectedTopicIds = emptySet(),
            )
            current.copy(
                selectedSubjectId = subjectId,
                chapterBlocks = listOf(initialBlock),
            )
        }
    }

    fun addChapterBlock() {
        _state.update { current ->
            val newBlock = ChapterBlockState(
                blockId = UUID.randomUUID().toString(),
                selectedChapterId = "",
                selectedTopicIds = emptySet(),
            )
            current.copy(chapterBlocks = current.chapterBlocks + newBlock)
        }
    }

    fun removeChapterBlock(blockId: String) {
        _state.update { current ->
            if (current.chapterBlocks.size > 1) {
                current.copy(chapterBlocks = current.chapterBlocks.filterNot { it.blockId == blockId })
            } else {
                current
            }
        }
    }

    fun selectChapterForBlock(blockId: String, chapterId: String) {
        _state.update { current ->
            val updatedBlocks = current.chapterBlocks.map { block ->
                if (block.blockId == blockId) {
                    block.copy(
                        selectedChapterId = chapterId,
                        selectedTopicIds = emptySet(),
                    )
                } else {
                    block
                }
            }
            current.copy(chapterBlocks = updatedBlocks)
        }
    }

    fun toggleTopicForBlock(blockId: String, topicId: String) {
        _state.update { current ->
            val updatedBlocks = current.chapterBlocks.map { block ->
                if (block.blockId == blockId) {
                    val updated = if (block.selectedTopicIds.contains(topicId)) {
                        block.selectedTopicIds - topicId
                    } else {
                        block.selectedTopicIds + topicId
                    }
                    block.copy(selectedTopicIds = updated)
                } else {
                    block
                }
            }
            current.copy(chapterBlocks = updatedBlocks)
        }
    }

    fun incrementPeriod() {
        _state.update {
            if (it.periodCount < 20) it.copy(periodCount = it.periodCount + 1) else it
        }
    }

    fun decrementPeriod() {
        _state.update {
            if (it.periodCount > 1) it.copy(periodCount = it.periodCount - 1) else it
        }
    }

    fun setManualTiming(enabled: Boolean) {
        _state.update { it.copy(isManualTiming = enabled) }
    }

    fun setStudyDuration(minutes: Int) {
        _state.update { it.copy(studyDurationMinutes = minutes) }
    }

    fun setBreakDuration(minutes: Int) {
        _state.update { it.copy(breakDurationMinutes = minutes) }
    }

    fun requestPlanSummary() {
        val currentState = _state.value
        val allTopics = currentState.allSelectedTopicIds
        if (allTopics.isEmpty()) {
            val errorMsg = "لطفاً حداقل یک مبحث برای مطالعه انتخاب کنید."
            _state.update { it.copy(errorMessage = errorMsg) }
            viewModelScope.launch { _events.emit(CreateStudyPlanEvent.ShowError(errorMsg)) }
            return
        }

        val totalMinutes = currentState.periodCount * currentState.studyDurationMinutes
        if (totalMinutes > 1440) {
            val errorMsg = "مجموع زمان مطالعه نمی‌تواند بیش از ۲۴ ساعت (۱۴۴۰ دقیقه) باشد."
            _state.update { it.copy(errorMessage = errorMsg) }
            viewModelScope.launch { _events.emit(CreateStudyPlanEvent.ShowError(errorMsg)) }
            return
        }

        _state.update { it.copy(isSummaryModalVisible = true, errorMessage = null) }
    }

    fun hideSummaryModal() {
        _state.update { it.copy(isSummaryModalVisible = false) }
    }

    fun saveStudyPlan(onSuccess: () -> Unit = {}) {
        requestPlanSummary()
    }

    fun confirmAndSubmitPlan(onSuccess: () -> Unit = {}) {
        val currentState = _state.value
        val today = LocalDate.now(ZoneId.of("Asia/Tehran")).toString()
        val allTopics = currentState.allSelectedTopicIds

        if (allTopics.isEmpty()) {
            _state.update { it.copy(isSummaryModalVisible = false) }
            return
        }

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            var lastErrorMessage: String? = null
            var successCount = 0

            for (topicId in allTopics) {
                val request = CreateManualStudyTaskDto(
                    requestId = UUID.randomUUID().toString(),
                    topicId = topicId,
                    scheduledOn = today,
                    periodCount = currentState.periodCount,
                    minutesPerPeriod = currentState.studyDurationMinutes,
                )

                val result = safeApiCall { api.createManualStudyTask(request) }
                when (result) {
                    is NetworkResult.Success -> {
                        successCount++
                    }
                    is NetworkResult.Error -> {
                        val message = when (result.code) {
                            400 -> {
                                if (result.message.contains("MANUAL_TASK_DATE_OUTSIDE_ALLOWED_RANGE")) {
                                    "تاریخ مدنظر خارج از بازه مجاز (امروز تا ۳۰ روز آینده) است."
                                } else if (result.message.contains("MANUAL_TASK_DURATION_TOO_LARGE")) {
                                    "مجموع زمان مطالعه بیش از حد مجاز است."
                                } else {
                                    result.message
                                }
                            }
                            404 -> "مبحث انتخاب شده در دسترس نیست یا با رشته شما همخوانی ندارد."
                            409 -> "پروفایل تحصیلی شما ناقص است. لطفاً ابتدا رشته و پایه تحصیلی خود را کامل کنید."
                            else -> result.message
                        }
                        lastErrorMessage = message
                    }
                    is NetworkResult.Exception -> {
                        lastErrorMessage = "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
                    }
                }
            }

            _state.update { it.copy(isSubmitting = false) }

            if (successCount > 0 || lastErrorMessage == null) {
                _state.update { it.copy(isSummaryModalVisible = false) }
                _events.emit(CreateStudyPlanEvent.PlanSaved)
                onSuccess()
            } else {
                val error = lastErrorMessage ?: "خطایی رخ داد."
                _state.update { it.copy(errorMessage = error) }
                _events.emit(CreateStudyPlanEvent.ShowError(error))
            }
        }
    }
}
