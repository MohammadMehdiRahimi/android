with open('app/src/main/java/com/example/ui/features/studyplan/PlanWizard.kt', 'r') as f:
    lines = f.readlines()

out = []
i = 0
while i < len(lines):
    line = lines[i]
    if line.strip() == "3 -> {":
        # insert new block
        out.append(line)
        out.append("""                    val primaryBook = selectedBooks.firstOrNull() ?: "زیست‌شناسی"
                    val chaptersText = selectedChapters[primaryBook]?.joinToString("، ") ?: "کل مباحث پایه"
                    
                    if (isTakingExam) {
                        com.example.ui.features.exams.ExamTakingContent(
                            colors = colors,
                            questions = com.example.ui.features.exams.mockExamQuestions.take(3),
                            isDescriptive = false,
                            initialRemainingSeconds = 180,
                            onFinishExam = { selAns, descAns, uplImgs ->
                                val hasExams = examChapters.isNotEmpty()
                                val calculatedExamBook = if (hasExams) {
                                    val firstExam = examChapters.first()
                                    bookChapters.entries.firstOrNull { it.value.contains(firstExam) }?.key ?: ""
                                } else ""
                                val calculatedExamChapter = if (hasExams) {
                                    examChapters.joinToString("، ") { chap ->
                                        val day = examDays[chap] ?: "شنبه"
                                        "$chap (امتحان در روز $day)"
                                    }
                                } else ""
                                onFinish(
                                    dailyHoursMap.toMap(),
                                    selectedBooks.toList(),
                                    selectedChapters.toMap(),
                                    hasExams,
                                    calculatedExamBook,
                                    calculatedExamChapter
                                )
                            },
                            onExitExam = {
                                isTakingExam = false
                            }
                        )
                    } else {
                        com.example.ui.features.exams.ExamStartScreenContent(
                            colors = colors,
                            examTitle = "آزمون تعیین سطح علمی رایا 🤖",
                            grade = "یازدهم",
                            field = "علوم تجربی",
                            book = "$primaryBook - $chaptersText",
                            questionCount = 3,
                            durationMinutes = 3,
                            isDescriptive = false,
                            hasNegativeScore = false,
                            onStartClick = {
                                isTakingExam = true
                            }
                        )
                    }
""")
        # skip lines until the closing brace of 3 -> {
        count = 1
        i += 1
        while count > 0:
            if "{" in lines[i]:
                count += lines[i].count("{")
            if "}" in lines[i]:
                count -= lines[i].count("}")
            if count == 0:
                out.append(lines[i])
                break
            i += 1
        
        i += 1
        continue
    
    out.append(line)
    i += 1

with open('app/src/main/java/com/example/ui/features/studyplan/PlanWizard.kt', 'w') as f:
    f.writelines(out)
