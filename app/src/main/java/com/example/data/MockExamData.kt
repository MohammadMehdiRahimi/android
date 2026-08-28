package com.example.data

import androidx.compose.runtime.mutableStateListOf

data class ExamQuestionTip(
    val type: String,
    val content: String
)

data class ExamQuestionImages(
    val q: String? = null,
    val a: String? = null
)

data class ExamQuestionExplanation(
    val understandingTheQuestion: String = "",
    val conceptReview: String = "",
    val stepByStepSolution: String = "",
    val whyOthersAreWrong: String = ""
)

data class ExamQuestionFlashCard(
    val front: String = "",
    val back: String = ""
)

data class ExamQuestion(
    val order: Int,
    val grade: String,
    val fieldOfStudy: String,
    val book: String,
    val chapter: String,
    val questionCategory: String,
    val level: String,
    val type: String,
    val question: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String,
    val tips: List<ExamQuestionTip>,
    val needsImage: Boolean,
    val topic: String,
    val examSource: String,
    val isProblem: Boolean,
    val problem: List<String>,
    val sourcePdf: String,
    val images: ExamQuestionImages? = null,
    val explanationObj: ExamQuestionExplanation? = null,
    var flashCard: ExamQuestionFlashCard? = null
)

data class MyExamHistoryItem(
    val id: String,
    val subject: String,
    val topic: String,
    val questionCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val durationMinutes: Int,
    val date: String,
    val scorePercentage: Int
)

object MockExamData {
    var globalSelectedAnswers = mapOf<Int, Int>()
    var globalExamType = "تستی" // "تستی" or "تشریحی"
    var globalDescriptiveAnswers = mapOf<Int, String>() // to store typed answers for descriptive exams!

    val examsHistoryList = mutableStateListOf<MyExamHistoryItem>(
        MyExamHistoryItem("1", "ریاضی دهم", "معادله و نامعادله", 30, 18, 12, 45, "۱۴۰۳/۰۳/۲۵", 60),
        MyExamHistoryItem("2", "فیزیک دهم", "فشار و آثار آن", 25, 20, 5, 40, "۱۴۰۳/۰۳/۲۲", 80),
        MyExamHistoryItem("3", "شیمی دهم", "ساختار اتم", 20, 15, 5, 30, "۱۴۰۳/۰۳/۲۰", 75),
        MyExamHistoryItem("4", "زیست دهم", "گوارش و جذب مواد", 25, 21, 4, 35, "۱۴۰۳/۰۳/۱۸", 84),
        MyExamHistoryItem("5", "ریاضی دهم", "توابع و نمودارها", 30, 17, 13, 45, "۱۴۰۳/۰۳/۱۵", 57),
        MyExamHistoryItem("6", "فیزیک دهم", "کار و انرژی", 25, 24, 1, 30, "۱۴۰۳/۰۳/۱۰", 96),
        MyExamHistoryItem("7", "شیمی دهم", "پیوندهای شیمیایی", 20, 16, 4, 40, "۱۴۰۳/۰۳/۰۵", 80),
        MyExamHistoryItem("8", "زیست دهم", "گردش مواد در بدن", 25, 23, 2, 35, "۱۴۰۳/۰۲/۲۸", 92),
        MyExamHistoryItem("9", "ادبیات فارسی", "آرایه‌های ادبی و قرابت", 20, 18, 2, 20, "۱۴۰۳/۰۲/۲۰", 90),
        MyExamHistoryItem("10", "عربی دهم", "ترجمه و قواعد ثلاثی", 20, 19, 1, 25, "۱۴۰۳/۰۲/۱۵", 95),
        MyExamHistoryItem("11", "زبان انگلیسی", "گرامر زمان‌ها و واژگان", 30, 28, 2, 30, "۱۴۰۳/۰۲/۱۰", 93),
        MyExamHistoryItem("12", "دین و زندگی", "هدف آفرینش و توحید", 20, 20, 0, 25, "۱۴۰۳/۰۲/۰۲", 100)
    )

    fun addExamToHistory(
        subject: String,
        topic: String,
        questionCount: Int,
        correctCount: Int,
        incorrectCount: Int,
        durationMinutes: Int,
        percentage: Int
    ) {
        val calendar = java.util.Calendar.getInstance()
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val monthNames = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
        val month = monthNames[calendar.get(java.util.Calendar.MONTH)]
        val dateStr = "$day $month"
        
        val newId = (examsHistoryList.size + 1).toString()
        examsHistoryList.add(
            0,
            MyExamHistoryItem(
                id = newId,
                subject = subject,
                topic = topic,
                questionCount = questionCount,
                correctCount = correctCount,
                incorrectCount = incorrectCount,
                durationMinutes = durationMinutes,
                date = dateStr,
                scorePercentage = percentage
            )
        )
    }

    // List of dynamic questions loaded from questions.json
    var dynamicQuestions: List<ExamQuestion> = emptyList()

    val questions: List<ExamQuestion>
        get() = dynamicQuestions

    fun loadQuestionsFromAssets(context: android.content.Context): List<ExamQuestion> {
        if (dynamicQuestions.isNotEmpty()) return dynamicQuestions
        try {
            val inputStream = context.assets.open("questions.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            
            val jsonArray = try {
                org.json.JSONArray(jsonString)
            } catch (e: Exception) {
                val jsonObject = org.json.JSONObject(jsonString)
                jsonObject.getJSONArray("questions")
            }
            val list = mutableListOf<ExamQuestion>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                
                val order = item.optInt("order")
                val grade = item.optString("grade")
                val fieldOfStudy = item.optString("fieldOfStudy")
                val book = item.optString("book")
                val chapter = item.optString("chapter")
                val topic = item.optString("topic")
                val questionCategory = item.optString("questionCategory")
                val level = item.optString("level")
                val type = item.optString("type")
                val question = item.optString("question")
                
                // Options
                val options = listOf(
                    item.optString("option1"),
                    item.optString("option2"),
                    item.optString("option3"),
                    item.optString("option4")
                )
                
                val answerVal = item.opt("answer")
                val answer = when (answerVal) {
                    is Number -> answerVal.toInt()
                    is String -> answerVal.toIntOrNull() ?: 1
                    else -> 1
                }
                
                // Explanation
                val explanationObjJson = item.optJSONObject("explanation")
                val explanationObj = if (explanationObjJson != null) {
                    ExamQuestionExplanation(
                        understandingTheQuestion = explanationObjJson.optString("understanding_the_question"),
                        conceptReview = explanationObjJson.optString("concept_review"),
                        stepByStepSolution = explanationObjJson.optString("step_by_step_solution"),
                        whyOthersAreWrong = explanationObjJson.optString("why_others_are_wrong")
                    )
                } else {
                    ExamQuestionExplanation()
                }
                
                val explanationStr = if (explanationObjJson == null) {
                    item.optString("explanation")
                } else {
                    ""
                }
                
                // Tips
                val tipsList = mutableListOf<ExamQuestionTip>()
                val tipArray = item.optJSONArray("tip")
                if (tipArray != null) {
                    for (j in 0 until tipArray.length()) {
                        val tipItem = tipArray.getJSONObject(j)
                        tipsList.add(
                            ExamQuestionTip(
                                type = tipItem.optString("type"),
                                content = tipItem.optString("content")
                            )
                        )
                    }
                }
                
                // Flashcard
                val flashCardObjJson = item.optJSONObject("flash_card")
                val flashCard = if (flashCardObjJson != null) {
                    ExamQuestionFlashCard(
                        front = flashCardObjJson.optString("front"),
                        back = flashCardObjJson.optString("back")
                    )
                } else {
                    null
                }
                
                // Vision/Images
                val visionArray = item.optJSONArray("vision")
                val hasVision = visionArray != null && visionArray.length() > 0
                val qImage = if (hasVision) "vision.svg" else null
                val aImage = if (hasVision) "vision.svg" else null
                
                list.add(
                    ExamQuestion(
                        order = order,
                        grade = grade,
                        fieldOfStudy = fieldOfStudy,
                        book = book,
                        chapter = chapter,
                        questionCategory = questionCategory,
                        level = level,
                        type = type,
                        question = question,
                        options = options,
                        answer = answer,
                        explanation = explanationStr,
                        tips = tipsList,
                        needsImage = hasVision,
                        topic = topic,
                        examSource = "تالیفی",
                        isProblem = item.optBoolean("isProblem", false),
                        problem = emptyList(),
                        sourcePdf = "",
                        images = ExamQuestionImages(q = qImage, a = aImage),
                        explanationObj = explanationObj,
                        flashCard = flashCard
                    )
                )
            }
            dynamicQuestions = list
            return list
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    val oldQuestions = listOf(
        ExamQuestion(
            order = 1,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "آسان",
            type = "تستی",
            question = "قطر مربعی \\(30\\sqrt{2}\\) است. محیط مربع کدام است؟",
            options = listOf("\\(160\\)", "\\(120\\)", "\\(100\\)", "\\(80\\)"),
            answer = 2,
            explanation = "در مربعی به ضلع \\(x\\)، قطر مربع برابر \\(x\\sqrt{2}\\) است.<br/>\\[x^2 + x^2 = d^2 \\Rightarrow 2x^2 = d^2\\]<br/>طبق رابطه فیثاغورس:<br/>\\[x^2 + x^2 = (30\\sqrt{2})^2 \\Rightarrow 2x^2 = 1800 \\Rightarrow x^2 = 900 \\Rightarrow x = 30\\]<br/>(طول ضلع مثبت است.)<br/>محیط مربع: \\(4x = 4 \\times 30 = 120\\)",
            tips = listOf(ExamQuestionTip("نکته", "در مربعی به ضلع \\(x\\)، قطر مربع برابر \\(x\\sqrt{2}\\) است.")),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 2,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "کدام یک از معادله‌های زیر دارای جواب‌های \\(-2\\) و \\(3\\) است؟",
            options = listOf("\\(2x^2 - 7x + 3 = 0\\)", "\\(2x^2 - 5x - 3 = 0\\)", "\\(2x^2 + 7x + 3 = 0\\)", "\\(2x^2 + 5x - 3 = 0\\)"),
            answer = 1,
            explanation = "معادله درجه دومی که ریشه‌های \\(-2\\) و \\(3\\) داشته باشد به صورت \\(k(x + 2)(x - 3) = 0\\) است.<br/>با ساده کردن: \\((x + 2)(x - 3) = 0 \\Rightarrow x^2 - x - 6 = 0\\)<br/>حال معادله را در 2 ضرب می‌کنیم: \\(2x^2 - 2x - 12 = 0\\)",
            tips = listOf(
                ExamQuestionTip("نکته", "اگر ریشه‌های معادله درجه دوم α و β باشند، معادله به صورت \\(k(x-\\alpha)(x-\\beta)=0\\) است."),
                ExamQuestionTip("دام تستی", "اگر به اشتباه معادله را به صورت \\((x-3)(x-1)=0\\) بنویسیم، به گزینه 2 می‌رسیم.")
            ),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = true,
            problem = listOf("اشکال در صورت سوال: ریشه‌های 2- و 3 در هیچ گزینه‌ای وجود ندارد. پاسخنامه گزینه 1 را صحیح دانسته در حالی که ریشه‌های گزینه 1، 3 و 0.5 است."),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 3,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "معادله \\((x - \\frac{2}{3})^2 = m - 1\\) یک ریشهٔ مضاعف دارد. مجموع مقدار \\(m\\) و آن ریشهٔ مضاعف کدام گزینه می‌تواند باشد؟",
            options = listOf("\\(-\\frac{2}{3}\\)", "\\(-\\frac{4}{3}\\)", "\\(\\frac{1}{3}\\)", "\\(\\frac{2}{3}\\)"),
            answer = 1,
            explanation = "برای اینکه معادله ریشه مضاعف داشته باشد، باید \\(m-1=0 \\Rightarrow m=1\\).<br/>در این حالت معادله به صورت \\((x-\\frac{2}{3})^2=0 \\Rightarrow x=\\frac{2}{3}\\) می‌شود.<br/>مجموع m و ریشه مضاعف: \\(1 + \\frac{2}{3} = \\frac{5}{3}\\)",
            tips = listOf(ExamQuestionTip("نکته", "معادله \\((ax+b)^2 = k\\):<br/>- اگر \\(k>0\\): دو ریشه ساده<br/>- اگر \\(k=0\\): یک ریشه مضاعف<br/>- اگر \\(k<0\\): ریشه حقیقی ندارد")),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = true,
            problem = listOf("تناقض در پاسخنامه: محاسبات مجموع m و ریشه را 5/3 نشان می‌دهد اما گزینه 1 (-2/3) را صحیح اعلام کرده است."),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 4,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "در مورد معادله \\(\\frac{11}{x^2-4} + \\frac{x+3}{2-x} = \\frac{2x-3}{x^2-4}\\) کدام گزینه صحیح است؟",
            options = listOf("یک ریشهٔ مضاعف دارد.", "یک ریشهٔ ساده دارد.", "دو ریشهٔ ساده دارد.", "ریشه حقیقی ندارد."),
            answer = 4,
            explanation = "ابتدا مخرج‌ها را تجزیه کرده و مخرج مشترک می‌گیریم:<br/>\\[\\frac{11}{(x-2)(x+2)} + \\frac{x+3}{2-x} = \\frac{2x-3}{(x-2)(x+2)}\\]<br/>ضریب دادن و حل...",
            tips = emptyList(),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = true,
            problem = listOf("پاسخنامه تشریحی دچار اشتباه محاسباتی شده است. معادله دو ریشه ساده دارد (8- و 1) اما پاسخنامه گزینه 4 (ریشه حقیقی ندارد) را صحیح اعلام کرده است."),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 5,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "مفهومی",
            level = "آسان",
            type = "تستی",
            question = "کدام یک از رابطه‌های زیر تابع می‌باشد؟",
            options = listOf("نمودار دایره", "نمودار پیکانی با دو پیکان از 1- به 2 و 0", "\\(f = \\{(-1, -1)\\}\\)", "جدول مقادیر با زوج‌های (-1,-1)، (-3,2)، (1,0)، (1,-1)"),
            answer = 3,
            explanation = "گزینه 3 تابع است؛ زیرا در نمایش زوج‌مرتبی، مؤلفه‌های اول همگی متمایز هستند.",
            tips = emptyList(),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 6,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "نکات شکل",
            level = "آسان",
            type = "تستی",
            question = "با توجه به نمودار تابع f، مجموع اعضای دامنهٔ تابع f کدام است؟",
            options = listOf("2", "8", "3", "6"),
            answer = 3,
            explanation = "برای به دست آوردن دامنه تابع کافی است مجموعه طول نقاط را مشخص کنیم.<br/>\\(D_f = \\{-2, -1, 1, 2, 3\\}\\)<br/>مجموع اعضای دامنه: \\(-2 + (-1) + 1 + 2 + 3 = 3\\)",
            tips = listOf(ExamQuestionTip("دام تستی", "اگر به اشتباه مجموع برد تابع را محاسبه کنید، به گزینه 4 (6) می‌رسید.")),
            needsImage = true,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 7,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "ضابطه جبری تابعی را که جذر یک واحد کمتر از مربع هر عدد ورودی را گرفته و سه واحد از آن کم می‌کند و عدد خروجی را مشخص می‌کند، فرض کنید. اگر ورودی این تابع \\(\\sqrt{5}\\) باشد، چه عددی خارج می‌شود؟",
            options = listOf("-1", "1", "\\(\\sqrt{6} - 3\\)", "\\(\\sqrt{5} - 4\\)"),
            answer = 1,
            explanation = "عدد ورودی را \\(x\\) در نظر می‌گیریم.<br/>«جذر یک واحد کمتر از مربع آن عدد»: \\(\\sqrt{x^2 - 1}\\)<br/>«سه واحد از آن کم می‌کند»: \\(f(x) = \\sqrt{x^2 - 1} - 3\\)<br/>مقدار \\(f(\\sqrt{5}) = -1\\)",
            tips = emptyList(),
            needsImage = false,
            topic = "تعریف تابع، دامنه و برد",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = ""
        ),
        ExamQuestion(
            order = 8,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "برد تابع خطی \\(f(x) = -2x - 3\\) به صورت \\(B = \\{y \\in \\mathbb{R} \\mid 5 \\le y \\le 9\\}\\) است. دامنه آن کدام است؟",
            options = listOf("\\(A = \\{x \\in \\mathbb{R} \\mid -6 \\le x \\le -4\\}\\)", "\\(A = \\{x \\in \\mathbb{R} \\mid -21 \\le x \\le -13\\}\\)", "\\(A = \\{x \\in \\mathbb{R} \\mid -21 \\le x \\le 0\\}\\)", "\\(A = \\{x \\in \\mathbb{R} \\mid -13 \\le x \\le -6\\}\\)"),
            answer = 1,
            explanation = "از آنجا که ضریب x منفی است \\((-2)\\)، تابع نزولی است:<br/>\\(f(a) = 5 \\Rightarrow -2a - 3 = 5 \\Rightarrow a = -4\\)",
            tips = emptyList(),
            needsImage = false,
            topic = "توابع خطی",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = ""
        ),
        ExamQuestion(
            order = 9,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "تابع",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "تابع درآمد و هزینه در یک شرکت به ترتیب برابر با \\(R(x) = 10x - x^2\\) و \\(C(x) = 10 + 2x\\) می‌باشند (x تعداد کالاها می‌باشد). بیشترین سود شرکت با تولید چه مقدار کالا بدست می‌آید؟",
            options = listOf("3", "4", "6", "5"),
            answer = 2,
            explanation = "تابع سود: \\(P(x) = R(x) - C(x) = -x^2 + 8x - 10\\)<br/>رأس ماکزیمم: \\(\\frac{-b}{2a} = 4\\)",
            tips = listOf(ExamQuestionTip("نکته", "اگر تابع سود به صورت \\(P(x) = ax^2 + bx + c\\) باشد، بیشترین سود شرکت با تولید \\(\\frac{-b}{2a}\\) کالا ایجاد می‌شود.")),
            needsImage = false,
            topic = "انواع توابع",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = ""
        ),
        ExamQuestion(
            order = 10,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "شاخص‌های آماری و سری‌های زمانی",
            questionCategory = "مفهومی",
            level = "آسان",
            type = "تستی",
            question = "چند تا از موارد زیر صحیح است؟<br/>الف: پارامتر از جامعه به دست می‌آید و آماره از نمونه و مقدار هر دو همواره ثابت است.<br/>ب: داده‌ها واقعیت‌هایی درباره یک چیزند که در محاسبه، استنباط یا برنامه‌ریزی به کار می‌روند.<br/>ج: متغیر به هر ویژگی از اشخاص یا اشیاء که قرار است بررسی شود، می‌گویند.<br/>د: در گردآوری داده‌ها به روش مشاهده به افراد پاسخگو نیاز است.",
            options = listOf("1", "2", "3", "4"),
            answer = 2,
            explanation = "الف: نادرست. آماره ثابت نیست.<br/>ب: صحیح.<br/>ج: صحیح.<br/>د: نادرست.<br/>بنابراین 2 مورد صحیح است.",
            tips = emptyList(),
            needsImage = false,
            topic = "شاخص‌های آماری",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = ""
        ),
        ExamQuestion(
            order = 11,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "شاخص‌های آماری و سری‌های زمانی",
            questionCategory = "مفهومی",
            level = "متوسط",
            type = "تستی",
            question = "در داده‌هایی که به شکل توزیع نرمال هستند، میانگین 11 و واریانس 4 است. تقریباً چند درصد داده‌ها بین اعداد 9 و 15 واقع هستند؟",
            options = listOf("82", "68", "96", "78"),
            answer = 1,
            explanation = "واریانس \\(\\sigma^2 = 4 \\Rightarrow \\sigma = 2\\)<br/>حدود 68% داده‌ها بین 9 تا 13<br/>مجموع = 34% + 47.5% = 81.5% ≈ 82%",
            tips = listOf(ExamQuestionTip("نکته", "در توزیع نرمال:<br/>- 68% داده‌ها بین \\(\\mu \\pm \\sigma\\)<br/>- 95% داده‌ها بین \\(\\mu \\pm 2\\sigma\\)<br/>- 99.7% داده‌ها بین \\(\\mu \\pm 3\\sigma\\)")),
            needsImage = false,
            topic = "شاخص‌های آماری",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = ""
        ),
        ExamQuestion(
            order = 12,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "شاخص‌های آماری و سری‌های زمانی",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "در نمودار جعبه‌ای زیر دامنه تغییرات برابر \\(5k-1\\) و اختلاف چارک‌های سوم و دوم برابر 5 است. اختلاف چارک‌های دوم و اول کدام است؟",
            options = listOf("2", "3", "4", "5"),
            answer = 2,
            explanation = "حداقل = 2، حداکثر = \\(2a+6\\)<br/>از محاسبات \\(k=3\\) و \\(Q_2 - Q_1 = 3\\)",
            tips = emptyList(),
            needsImage = true,
            topic = "شاخص‌های آماری",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        ),
        ExamQuestion(
            order = 13,
            grade = "یازدهم",
            fieldOfStudy = "علوم انسانی",
            book = "ریاضی و آمار",
            chapter = "شاخص‌های آماری و سری‌های زمانی",
            questionCategory = "محاسباتی",
            level = "متوسط",
            type = "تستی",
            question = "در یک نمودار راداری، اگر به تعداد متغیرها 3 متغیر جدید اضافه کنیم، از زاویهٔ بین دو شعاع متوالی 20 درجه کاسته می‌شود. تعداد متغیرها در ابتدا چندتا بوده است؟",
            options = listOf("6", "8", "9", "10"),
            answer = 1,
            explanation = "در نمودار راداری، زاویه بین دو شعاع متوالی از رابطه \\(\\frac{360}{n}\\) به دست می‌آید که \\(n\\) تعداد متغیرهاست.<br/>با حل معادله \\(n=6\\) به دست می‌آید.",
            tips = emptyList(),
            needsImage = true,
            topic = "شاخص‌های آماری",
            examSource = "تالیفی",
            isProblem = false,
            problem = emptyList(),
            sourcePdf = "",
            images = ExamQuestionImages(q = "vision.svg", a = "vision.svg")
        )
    )
}
