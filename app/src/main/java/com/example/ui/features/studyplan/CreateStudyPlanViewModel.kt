package com.example.ui.features.studyplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
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

data class BookPlanBlock(
    val bookBlockId: String = UUID.randomUUID().toString(),
    val selectedGrade: String = "GRADE_12",
    val selectedGradeName: String = "پایه دوازدهم",
    val selectedSubjectId: String = "",
    val chapterBlocks: List<ChapterBlockState> = listOf(ChapterBlockState()),
    val periodCount: Int = 3,
    val studyDurationMinutes: Int = 45,
    val breakDurationMinutes: Int = 15,
)

data class CreateStudyPlanUiState(
    val userMajor: String = "EXPERIMENTAL",
    val userMajorName: String = "رشته تجربی",
    val grades: List<Pair<String, String>> = listOf(
        "GRADE_12" to "پایه دوازدهم",
        "GRADE_11" to "پایه یازدهم",
        "GRADE_10" to "پایه دهم",
    ),
    val selectedDate: JalaliDate = DateTransformer.getTodayJalali(),
    val isLoadingCatalog: Boolean = true,
    val subjectsByGrade: Map<String, List<SubjectVisualItem>> = emptyMap(),
    val bookBlocks: List<BookPlanBlock> = emptyList(),
    val isSummaryModalVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    fun getSubjectsForGrade(gradeKey: String): List<SubjectVisualItem> {
        return subjectsByGrade[gradeKey] ?: emptyList()
    }

    val totalSelectedTopicCount: Int
        get() = bookBlocks.sumOf { book ->
            book.chapterBlocks.sumOf { it.selectedTopicIds.size }
        }

    val totalEstimatedMinutes: Int
        get() = bookBlocks.sumOf { book ->
            book.periodCount * (book.studyDurationMinutes + book.breakDurationMinutes)
        }

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
        val defaultSubjects = buildDefaultSubjects(majorName)
        val initialMap = mapOf(
            "GRADE_12" to defaultSubjects,
            "GRADE_11" to defaultSubjects,
            "GRADE_10" to defaultSubjects,
        )
        val firstSub = defaultSubjects.first()

        val initialBook = BookPlanBlock(
            bookBlockId = "book_block_init_1",
            selectedGrade = "GRADE_12",
            selectedGradeName = "پایه دوازدهم",
            selectedSubjectId = firstSub.id,
            chapterBlocks = listOf(
                ChapterBlockState(
                    blockId = "init_ch_1",
                    selectedChapterId = "",
                    selectedTopicIds = emptySet(),
                )
            ),
            periodCount = 3,
            studyDurationMinutes = 45,
            breakDurationMinutes = 15,
        )

        _state.update {
            it.copy(
                userMajor = savedMajor ?: "EXPERIMENTAL",
                userMajorName = majorName,
                subjectsByGrade = initialMap,
                bookBlocks = listOf(initialBook),
                isLoadingCatalog = true,
            )
        }

        loadCatalogForAllGrades()
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

    private fun loadCatalogForAllGrades() {
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
                    val newSubjectsMap = mapOf(
                        "GRADE_12" to mappedSubjects,
                        "GRADE_11" to mappedSubjects,
                        "GRADE_10" to mappedSubjects,
                    )
                    _state.update { current ->
                        val updatedBooks = current.bookBlocks.map { book ->
                            val currentGradeSubjects = newSubjectsMap[book.selectedGrade] ?: mappedSubjects
                            val validSubjectId = if (currentGradeSubjects.any { it.id == book.selectedSubjectId }) {
                                book.selectedSubjectId
                            } else {
                                currentGradeSubjects.firstOrNull()?.id ?: ""
                            }
                            book.copy(selectedSubjectId = validSubjectId)
                        }
                        current.copy(
                            isLoadingCatalog = false,
                            userMajorName = effectiveMajorName,
                            subjectsByGrade = newSubjectsMap,
                            bookBlocks = updatedBooks,
                        )
                    }
                    return@launch
                }
            }

            _state.update {
                it.copy(
                    isLoadingCatalog = false,
                )
            }
        }
    }

    fun retryCatalog() {
        _state.update { it.copy(isLoadingCatalog = true, errorMessage = null) }
        loadCatalogForAllGrades()
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

    fun selectDate(date: JalaliDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    // --- Multi-Book Actions ---

    fun addBookBlock() {
        _state.update { current ->
            val currentGrade = current.bookBlocks.lastOrNull()?.selectedGrade ?: "GRADE_12"
            val gradeName = current.grades.firstOrNull { it.first == currentGrade }?.second ?: "پایه دوازدهم"
            val subjects = current.getSubjectsForGrade(currentGrade)
            val firstSubId = subjects.firstOrNull()?.id ?: ""

            val newBlock = BookPlanBlock(
                bookBlockId = UUID.randomUUID().toString(),
                selectedGrade = currentGrade,
                selectedGradeName = gradeName,
                selectedSubjectId = firstSubId,
                chapterBlocks = listOf(
                    ChapterBlockState(
                        blockId = UUID.randomUUID().toString(),
                        selectedChapterId = "",
                        selectedTopicIds = emptySet(),
                    )
                ),
                periodCount = 3,
                studyDurationMinutes = 45,
                breakDurationMinutes = 15,
            )
            current.copy(bookBlocks = current.bookBlocks + newBlock)
        }
    }

    fun removeBookBlock(bookBlockId: String) {
        _state.update { current ->
            if (current.bookBlocks.size > 1) {
                current.copy(bookBlocks = current.bookBlocks.filterNot { it.bookBlockId == bookBlockId })
            } else {
                current
            }
        }
    }

    fun selectGradeForBook(bookBlockId: String, gradeKey: String, gradeName: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    val subjects = current.getSubjectsForGrade(gradeKey)
                    val firstSub = subjects.firstOrNull()?.id ?: ""
                    book.copy(
                        selectedGrade = gradeKey,
                        selectedGradeName = gradeName,
                        selectedSubjectId = firstSub,
                        chapterBlocks = listOf(
                            ChapterBlockState(
                                blockId = UUID.randomUUID().toString(),
                                selectedChapterId = "",
                                selectedTopicIds = emptySet(),
                            )
                        ),
                    )
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun selectSubjectForBook(bookBlockId: String, subjectId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    book.copy(
                        selectedSubjectId = subjectId,
                        chapterBlocks = listOf(
                            ChapterBlockState(
                                blockId = UUID.randomUUID().toString(),
                                selectedChapterId = "",
                                selectedTopicIds = emptySet(),
                            )
                        ),
                    )
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun addChapterBlockToBook(bookBlockId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    val newChBlock = ChapterBlockState(
                        blockId = UUID.randomUUID().toString(),
                        selectedChapterId = "",
                        selectedTopicIds = emptySet(),
                    )
                    book.copy(chapterBlocks = book.chapterBlocks + newChBlock)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun removeChapterBlockFromBook(bookBlockId: String, chapterBlockId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    if (book.chapterBlocks.size > 1) {
                        book.copy(chapterBlocks = book.chapterBlocks.filterNot { it.blockId == chapterBlockId })
                    } else {
                        book
                    }
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun selectChapterForBookBlock(bookBlockId: String, chapterBlockId: String, chapterId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    val newChBlocks = book.chapterBlocks.map { ch ->
                        if (ch.blockId == chapterBlockId) {
                            ch.copy(
                                selectedChapterId = chapterId,
                                selectedTopicIds = emptySet(),
                            )
                        } else {
                            ch
                        }
                    }
                    book.copy(chapterBlocks = newChBlocks)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun toggleTopicForBookBlock(bookBlockId: String, chapterBlockId: String, topicId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    val newChBlocks = book.chapterBlocks.map { ch ->
                        if (ch.blockId == chapterBlockId) {
                            val newTopics = if (ch.selectedTopicIds.contains(topicId)) {
                                ch.selectedTopicIds - topicId
                            } else {
                                ch.selectedTopicIds + topicId
                            }
                            ch.copy(selectedTopicIds = newTopics)
                        } else {
                            ch
                        }
                    }
                    book.copy(chapterBlocks = newChBlocks)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    // --- Direct Open Timing Controls ---

    fun incrementPeriodForBook(bookBlockId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId && book.periodCount < 20) {
                    book.copy(periodCount = book.periodCount + 1)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun decrementPeriodForBook(bookBlockId: String) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId && book.periodCount > 1) {
                    book.copy(periodCount = book.periodCount - 1)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun setStudyDurationForBook(bookBlockId: String, minutes: Int) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    book.copy(studyDurationMinutes = minutes)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    fun setBreakDurationForBook(bookBlockId: String, minutes: Int) {
        _state.update { current ->
            val updated = current.bookBlocks.map { book ->
                if (book.bookBlockId == bookBlockId) {
                    book.copy(breakDurationMinutes = minutes)
                } else {
                    book
                }
            }
            current.copy(bookBlocks = updated)
        }
    }

    // --- Summary & Submission ---

    fun requestPlanSummary() {
        val currentState = _state.value
        val totalTopics = currentState.totalSelectedTopicCount
        if (totalTopics == 0) {
            val errorMsg = "لطفاً حداقل یک مبحث برای مطالعه در یکی از کتاب‌ها انتخاب کنید."
            _state.update { it.copy(errorMessage = errorMsg) }
            viewModelScope.launch { _events.emit(CreateStudyPlanEvent.ShowError(errorMsg)) }
            return
        }

        val totalMinutes = currentState.totalEstimatedMinutes
        if (totalMinutes > 1440) {
            val errorMsg = "مجموع زمان کل مطالعه نمی‌تواند بیش از ۲۴ ساعت (۱۴۴۰ دقیقه) باشد."
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
        val scheduledDate = currentState.selectedDate.toGregorian().toString()

        val allRequests = mutableListOf<CreateManualStudyTaskDto>()
        for (book in currentState.bookBlocks) {
            val bookTopicIds = book.chapterBlocks.flatMap { it.selectedTopicIds }.distinct()
            for (topicId in bookTopicIds) {
                allRequests.add(
                    CreateManualStudyTaskDto(
                        requestId = UUID.randomUUID().toString(),
                        topicId = topicId,
                        scheduledOn = scheduledDate,
                        periodCount = book.periodCount,
                        minutesPerPeriod = book.studyDurationMinutes,
                    )
                )
            }
        }

        if (allRequests.isEmpty()) {
            _state.update { it.copy(isSummaryModalVisible = false) }
            return
        }

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            var lastErrorMessage: String? = null
            var successCount = 0

            for (request in allRequests) {
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
                StudyPlanDataCache.invalidate()
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
