package com.example.ui.features.trouble

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.R
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Model representing a student peer troubleshooting question
data class TroublePost(
    val id: String,
    val authorName: String,
    val authorRole: String, // "پایه دوازدهم", "پایه یازدهم" etc
    val authorLevel: Int,
    val subject: String, // "ریاضی", "فیزیک", "شیمی", "زیست", "عمومی"
    val subjectIcon: Int,
    val questionText: String,
    val attachedImageRes: Int?, // localized question snapshot (e.g. pattern, raya, mountain)
    val replies: List<PostReply>,
    val votes: Int,
    var isUpvoted: Boolean = false,
    val isSolved: Boolean = false,
    val timestamp: String
)

// Model representing reply from helper peers
data class PostReply(
    val replyId: String,
    val authorName: String,
    val authorRole: String,
    val text: String,
    val attachedImageRes: Int? = null,
    val votes: Int,
    var isUpvoted: Boolean = false,
    val isBestAnswer: Boolean = false,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerTroubleScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Full State Architecture
    var selectedFilter by remember { mutableStateOf("همه") }
    val filters = listOf("همه", "ریاضی", "فیزیک", "شیمی", "زیست", "عمومی")
    var searchQuery by remember { mutableStateOf("") }

    // Hardcoded high-fidelity mock questions representing actual exam issues
    var troublePosts by remember {
        mutableStateOf(
            listOf(
                TroublePost(
                    id = "tp_1",
                    authorName = "محمدجواد قربانی",
                    authorRole = "پایه دوازدهم تجربی",
                    authorLevel = 14,
                    subject = "ریاضی",
                    subjectIcon = R.drawable.ic_feature_schedule,
                    questionText = "سلام رفقا. توی این بخش از سوال مثلثات کنکور نهایی چطور باید از فرمول تبدیل به ضرب استفاده کنیم؟ هر کاری می‌کنم مخرج ساده نمیشه. عکس فرمولم گذاشتم.",
                    attachedImageRes = R.drawable.pattern,
                    timestamp = "۱۰ دقیقه پیش",
                    votes = 15,
                    isSolved = false,
                    replies = listOf(
                        PostReply(
                            replyId = "rep_1_1",
                            authorName = "کیمیا زارعی",
                            authorRole = "رتبه ۲۸۰ کشوری",
                            text = "سلام محمدجواد عزیز. در صورت کسر ابتدا عبارت سینوس آلفا به اضافه سینوس بتا رو به فرمول ۲سینوس جمع دوم کسینوس تفاضل دوم باز کن. اینجوری سینوس نصف مخرج ساده میشه و به کتانژانت میرسی.",
                            votes = 8,
                            isBestAnswer = true,
                            timestamp = "۶ دقیقه پیش"
                        )
                    )
                ),
                TroublePost(
                    id = "tp_2",
                    authorName = "نرگس صدیقی",
                    authorRole = "پایه یازدهم ریاضی",
                    authorLevel = 11,
                    subject = "فیزیک",
                    subjectIcon = R.drawable.ic_subject_physics,
                    questionText = "بچه ها فرمول شتاب متوسط رو توی حرکت کندشونده چطور بنویسم که علامت منفی به درستی لحاظ بشه؟ نمودار سرعت-زمان پیوست شده رو ببینید.",
                    attachedImageRes = R.drawable.raya,
                    timestamp = "۲۵ دقیقه پیش",
                    votes = 8,
                    isSolved = true,
                    replies = listOf(
                        PostReply(
                            replyId = "rep_2_1",
                            authorName = "پارسا کریمی",
                            authorRole = "پایه یازدهم",
                            text = "چون سرعت در جهت خلاف محور کم میشه شتاب همیشه باید منفی باشه. فرمول دلتا v رو همیشه بدون تغییر علامت بنویس، تهش مقادیر جایگذاری بشن علامت خودش درست درمیاد.",
                            votes = 12,
                            isBestAnswer = true,
                            timestamp = "۱۵ دقیقه پیش"
                        )
                    )
                ),
                TroublePost(
                    id = "tp_3",
                    authorName = "علیرضا عباسی",
                    authorRole = "کنکوری ۱۴۰۵",
                    authorLevel = 19,
                    subject = "زیست",
                    subjectIcon = R.drawable.ic_subject_dna,
                    questionText = "موقع رونویسی DNA آیا غشای هسته کلاً ناپدید میشه یا فقط تراوایی لوله‌های پروتئینی بالا میره؟ استاد ما یه چیز میگه کتاب یه چیز دیگه.",
                    attachedImageRes = null,
                    timestamp = "۱ ساعت پیش",
                    votes = 22,
                    isSolved = true,
                    replies = listOf(
                        PostReply(
                            replyId = "rep_3_1",
                            authorName = "دکتر رایا (هوش مصنوعی)",
                            authorRole = "دستیار هوشمند شتاب",
                            text = "سلام علیرضا! در فرآیند رونویسی (Transcription) بر خلاف تقسیم میتوز، غشای هسته به هیچ وجه ناپدید نمی‌شود. ژن‌ها درون هسته قرار دارند و رونویسی همانجا انجام شده و mRNA ثانویه از منافذ هسته خارج می‌شود.",
                            votes = 35,
                            isBestAnswer = true,
                            timestamp = "۴۵ دقیقه پیش"
                        )
                    )
                ),
                TroublePost(
                    id = "tp_4",
                    authorName = "غزل رضازاده",
                    authorRole = "پایه دهم تجربی",
                    authorLevel = 8,
                    subject = "شیمی",
                    subjectIcon = R.drawable.ic_subject_chemistry,
                    questionText = "توی حل مسائل استوکیومتری، جرم مولی رو با تقریب دو رقم اعشار تو نهایی می‌نویسید یا گرد میکنید؟ مثلاً کلر رو ۳۵.۵ می‌گیرید یا ۳۵؟ خیلی روی نمره نهایی حساسم.",
                    attachedImageRes = null,
                    timestamp = "۲ ساعت پیش",
                    votes = 4,
                    isSolved = false,
                    replies = emptyList()
                )
            )
        )
    }

    // Modal Details overlay states
    var selectedPostForDetails by remember { mutableStateOf<TroublePost?>(null) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    // New Question States
    var newPostSubject by remember { mutableStateOf("ریاضی") }
    var newPostText by remember { mutableStateOf("") }
    var newPostImageSimulated by remember { mutableStateOf<Int?>(null) } // Local image selector
    var newPostFeedbackMessage by remember { mutableStateOf("") }

    // Filtered computation
    val filteredPosts = troublePosts.filter { post ->
        val matchesSearch = post.questionText.contains(searchQuery) || post.authorName.contains(searchQuery)
        val matchesCategory = selectedFilter == "همه" || post.subject == selectedFilter
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "رفع اشکال همگانی 👥",
                        color = colors.primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "برگشت",
                            tint = colors.primaryText
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "شتاب همیاری",
                            color = colors.accentMain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.bgMain)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreatePostDialog = true },
                containerColor = colors.accentMain,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.AddComment, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("طرح سوال جدید", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        containerColor = colors.bgMain
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar & Info Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Interactive informational card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "سوالت رو بپرس، اشکال بقیه رو حل کن!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colors.primaryText
                            )
                            Text(
                                text = "با پاسخ‌دادن به سوال دانش‌آموزان به عنوان بهترین پاسخ، ۵۰+ امتیاز تراز هفتگی تالار افتخارات دریافت کنید.",
                                fontSize = 10.sp,
                                color = colors.secondaryText
                            )
                        }
                    }
                }

                // Dynamic Live Search Textfield
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجو در بین سوالات، درس‌ها یا نام رفقا...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = colors.secondaryText
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.cardBg,
                        unfocusedContainerColor = colors.cardBg,
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f)
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }

            // Quick Category Filter Slider
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { item ->
                    val isSelected = item == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.accentMain else colors.cardBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedFilter = item }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item,
                            color = if (isSelected) Color.White else colors.secondaryText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Questions List with Beautiful UX Transitions
            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔍", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "سوالی متناسب با این فیلتر یا جستجو یافت نشد.",
                            color = colors.secondaryText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "فرصت خوبیه که به عنوان اولین نفر سوالت رو بنویسی!",
                            color = colors.accentMain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPosts) { post ->
                        TroublePostCard(
                            post = post,
                            onVoteClick = {
                                troublePosts = troublePosts.map {
                                    if (it.id == post.id) {
                                        val isUp = !it.isUpvoted
                                        it.copy(
                                            isUpvoted = isUp,
                                            votes = if (isUp) it.votes + 1 else it.votes - 1
                                        )
                                    } else it
                                }
                            },
                            onCardClick = { selectedPostForDetails = post }
                        )
                    }
                }
            }
        }

        // --- SECTION A: Question Detail and Community Thread Dialog ---
        selectedPostForDetails?.let { mainPost ->
            // Locate real in-state post to maintain state updates live
            val currentPost = troublePosts.find { it.id == mainPost.id } ?: mainPost
            var newCommentText by remember { mutableStateOf("") }

            Dialog(
                onDismissRequest = { selectedPostForDetails = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = colors.bgMain
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Dialog Header toolbar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.cardBg)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { selectedPostForDetails = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.primaryText)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "جزئیات سوال و پاسخ هم کلاسی‌ها",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colors.primaryText
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (currentPost.isSolved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (currentPost.isSolved) "حل شده" else "در جریان",
                                    color = if (currentPost.isSolved) Color(0xFF2E7D32) else Color(0xFFE65100),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Thread Messages Scroll
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // The Question Card itself in full details
                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.2f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(colors.accentMain.copy(alpha = 0.1f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Person, contentDescription = null, tint = colors.accentMain)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = currentPost.authorName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = colors.primaryText
                                                    )
                                                    Text(
                                                        text = "${currentPost.authorRole} • سطح ${currentPost.authorLevel}".toPersianNumber(),
                                                        fontSize = 10.sp,
                                                        color = colors.secondaryText
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = currentPost.subject,
                                                    color = colors.accentMain,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = currentPost.questionText,
                                            color = colors.primaryText,
                                            fontSize = 13.sp,
                                            lineHeight = 22.sp,
                                            textAlign = TextAlign.Start
                                        )

                                        if (currentPost.attachedImageRes != null) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Image(
                                                painter = painterResource(id = currentPost.attachedImageRes),
                                                contentDescription = "Question attachment",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .border(1.dp, colors.primaryText.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "منتشر شده در ${currentPost.timestamp}".toPersianNumber(),
                                                color = colors.secondaryText,
                                                fontSize = 9.sp
                                            )

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.ThumbUp,
                                                    contentDescription = null,
                                                    tint = if (currentPost.isUpvoted) colors.accentMain else colors.secondaryText,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable {
                                                            troublePosts = troublePosts.map {
                                                                if (it.id == currentPost.id) {
                                                                    val isUp = !it.isUpvoted
                                                                    it.copy(
                                                                        isUpvoted = isUp,
                                                                        votes = if (isUp) it.votes + 1 else it.votes - 1
                                                                    )
                                                                } else it
                                                            }
                                                        }
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${currentPost.votes} تایید علمی".toPersianNumber(),
                                                    color = colors.secondaryText,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Community Replies Count Header
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "پاسخ هم کلاسی‌ها (${currentPost.replies.size})".toPersianNumber(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = "بهترین پاسخ با دکمه تیک مشخص میشود",
                                        fontSize = 9.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }

                            // List of replies
                            if (currentPost.replies.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "هنوز پاسخی ثبت نشده. اولین نفر باش که همیاری می‌کنی!",
                                            color = colors.secondaryText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                items(currentPost.replies) { reply ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (reply.isBestAnswer) Color(0xFFE8F5E9) else colors.cardBg
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = 1.dp,
                                            color = if (reply.isBestAnswer) Color(0xFF81C784) else colors.primaryText.copy(alpha = 0.05f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(CircleShape)
                                                            .background(colors.primaryText.copy(alpha = 0.05f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Person, contentDescription = null, tint = colors.secondaryText)
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = reply.authorName,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp,
                                                                color = colors.primaryText
                                                            )
                                                            if (reply.isBestAnswer) {
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Box(
                                                                    modifier = Modifier
                                                                        .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                                ) {
                                                                    Text("✓ پاسخ منتخب", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                        }
                                                        Text(text = reply.authorRole, fontSize = 9.sp, color = colors.secondaryText)
                                                    }
                                                }

                                                // Awarding solver peer with dynamic toggle
                                                IconButton(
                                                    onClick = {
                                                        troublePosts = troublePosts.map { p ->
                                                            if (p.id == currentPost.id) {
                                                                p.copy(
                                                                    isSolved = true,
                                                                    replies = p.replies.map { r ->
                                                                        if (r.replyId == reply.replyId) r.copy(isBestAnswer = !r.isBestAnswer) else r.copy(isBestAnswer = false)
                                                                    }
                                                                )
                                                            } else p
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (reply.isBestAnswer) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = "Mark as best answer",
                                                        tint = if (reply.isBestAnswer) Color(0xFF2E7D32) else colors.secondaryText.copy(alpha = 0.4f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = reply.text,
                                                color = colors.primaryText,
                                                fontSize = 12.sp,
                                                lineHeight = 20.sp,
                                                textAlign = TextAlign.Start
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = reply.timestamp.toPersianNumber(), color = colors.secondaryText, fontSize = 8.sp)

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.ThumbUp,
                                                        contentDescription = null,
                                                        tint = if (reply.isUpvoted) colors.accentMain else colors.secondaryText,
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clickable {
                                                                troublePosts = troublePosts.map { p ->
                                                                    if (p.id == currentPost.id) {
                                                                        p.copy(
                                                                            replies = p.replies.map { r ->
                                                                                if (r.replyId == reply.replyId) {
                                                                                    val isUp = !r.isUpvoted
                                                                                    r.copy(
                                                                                        isUpvoted = isUp,
                                                                                        votes = if (isUp) r.votes + 1 else r.votes - 1
                                                                                    )
                                                                                } else r
                                                                            }
                                                                        )
                                                                    } else p
                                                                }
                                                            }
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = reply.votes.toString().toPersianNumber(),
                                                        color = colors.secondaryText,
                                                        fontSize = 9.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Text Comment Entry Block
                        Surface(
                            shadowElevation = 8.dp,
                            color = colors.cardBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("پاسخ علمی یا راهنمایی خودت رو بنویس...", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = colors.bgMain,
                                        unfocusedContainerColor = colors.bgMain,
                                        focusedBorderColor = colors.accentMain,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCommentText.trim().isNotEmpty()) {
                                            val myReply = PostReply(
                                                replyId = "rep_${System.currentTimeMillis()}",
                                                authorName = "شما (پویا)",
                                                authorRole = "پایه دوازدهم شتاب نخبگان",
                                                text = newCommentText,
                                                votes = 0,
                                                timestamp = "هم‌اکنون"
                                            )
                                            troublePosts = troublePosts.map {
                                                if (it.id == currentPost.id) {
                                                    it.copy(replies = it.replies + myReply)
                                                } else it
                                            }
                                            newCommentText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text("ارسال", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION B: Create New Question Dialog ---
        if (showCreatePostDialog) {
            Dialog(onDismissRequest = { showCreatePostDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "طرح سوال جدید در تالار همیاری 👥",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = colors.primaryText,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "سوال علمی، اشکال تستی یا نهایی خودت رو مطرح کن تا بقیه کمکت کنند.",
                            fontSize = 10.sp,
                            color = colors.secondaryText,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Subject Chip Selector
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("انتخاب کنید:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val listSubjects = listOf("ریاضی", "فیزیک", "شیمی", "زیست", "عمومی")
                                listSubjects.forEach { subj ->
                                    val isSelected = subj == newPostSubject
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) colors.accentMain else colors.bgMain)
                                            .clickable { newPostSubject = subj }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = subj,
                                            color = if (isSelected) Color.White else colors.secondaryText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Question Body
                        OutlinedTextField(
                            value = newPostText,
                            onValueChange = { newPostText = it },
                            placeholder = { Text("متن کامل اشکال درسی یا سوال گزینه ای رو بنویسید...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accentMain,
                                unfocusedBorderColor = colors.primaryText.copy(alpha = 0.08f)
                            )
                        )

                        // Photographic simulation attachment
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("ضمیمه کردن عکس سوالت:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        newPostImageSimulated = R.drawable.pattern
                                        newPostFeedbackMessage = "عکس چرک‌نویس با موفقیت متصل شد!"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (newPostImageSimulated == R.drawable.pattern) Color(0xFF4CAF50) else colors.accentMain.copy(alpha = 0.1f),
                                        contentColor = if (newPostImageSimulated == R.drawable.pattern) Color.White else colors.accentMain
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("گرفتن عکس جدید", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        newPostImageSimulated = R.drawable.raya
                                        newPostFeedbackMessage = "فایل نمودار از گالری برگزیده شد."
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (newPostImageSimulated == R.drawable.raya) Color(0xFF4CAF50) else colors.accentMain.copy(alpha = 0.1f),
                                        contentColor = if (newPostImageSimulated == R.drawable.raya) Color.White else colors.accentMain
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("از گالری گوشی", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (newPostFeedbackMessage.isNotEmpty()) {
                            Text(
                                text = newPostFeedbackMessage,
                                color = Color(0xFF2E7D32),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Submit action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showCreatePostDialog = false }) {
                                Text("انصراف", color = colors.secondaryText, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newPostText.trim().isNotEmpty()) {
                                        val newQuestion = TroublePost(
                                            id = "tp_${System.currentTimeMillis()}",
                                            authorName = "شما (پویا)",
                                            authorRole = "پایه دوازدهم نخبگان",
                                            authorLevel = 15,
                                            subject = newPostSubject,
                                            subjectIcon = when (newPostSubject) {
                                                "ریاضی" -> R.drawable.ic_feature_schedule
                                                "فیزیک" -> R.drawable.ic_subject_physics
                                                "شیمی" -> R.drawable.ic_subject_chemistry
                                                "زیست" -> R.drawable.ic_subject_dna
                                                else -> R.drawable.ic_subject_mosque
                                            },
                                            questionText = newPostText,
                                            attachedImageRes = newPostImageSimulated,
                                            replies = emptyList(),
                                            votes = 1,
                                            timestamp = "هم‌اکنون"
                                        )
                                        troublePosts = listOf(newQuestion) + troublePosts
                                        newPostText = ""
                                        newPostImageSimulated = null
                                        newPostFeedbackMessage = ""
                                        showCreatePostDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("ثبت و انتشار سوال", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

