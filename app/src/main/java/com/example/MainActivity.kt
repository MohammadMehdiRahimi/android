package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.features.auth.login.LoginScreen
import com.example.ui.features.auth.otp.VerifyOtpScreen
import com.example.ui.features.onboarding.OnboardingScreen
import com.example.ui.main.ShetabApp
import com.example.ui.screens.LoginOtpScreen
import com.example.ui.screens.LoginPhoneScreen
import com.example.ui.screens.SplashScreens
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme

import androidx.navigation.compose.currentBackStackEntryAsState
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween

class MainActivity : ComponentActivity() {
    /* Firebase push navigation is temporarily disabled.
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("open_notifications", false)) {
            com.example.notifications.NotificationNavigation.openNotifications.tryEmit(Unit)
        }
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.network.ApiClient.init(this)
        /* Firebase notification permission is temporarily disabled.
        if (
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 204)
        }
        */
        com.example.data.MockExamData.loadQuestionsFromAssets(applicationContext)
        enableEdgeToEdge()
        setContent {
            var selectedTheme by remember { mutableStateOf(AppTheme.PESARANE) }
            val navController = rememberNavController()

            /* Firebase token registration/deep-link handling is temporarily disabled.
            LaunchedEffect(Unit) {
                com.example.notifications.PushTokenRegistrar.register(this@MainActivity)
                if (
                    intent.getBooleanExtra("open_notifications", false) &&
                    com.example.network.ApiClient.getTokenManager()?.isLoggedIn() == true
                ) {
                    navController.navigate("notifications")
                    intent.removeExtra("open_notifications")
                }
            }
            LaunchedEffect(Unit) {
                com.example.notifications.NotificationNavigation.openNotifications.collect {
                    if (com.example.network.ApiClient.getTokenManager()?.isLoggedIn() == true) {
                        navController.navigate("notifications")
                    }
                }
            }
            */

            val navBackStackEntry by navController.currentBackStackEntryAsState()

            // Observe global AuthEvents (401 session expiration & logout)
            LaunchedEffect(Unit) {
                com.example.network.SessionManager.authEvents.collect { event ->
                    when (event) {
                        is com.example.network.AuthEvent.SessionExpired -> {
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                event.message,
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            navController.navigate("login_phone") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        is com.example.network.AuthEvent.LoggedOut -> {
                            navController.navigate("login_phone") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            LaunchedEffect(navBackStackEntry) {
                val route = navBackStackEntry?.destination?.route
                /* Firebase token refresh is temporarily disabled.
                if (route == "dashboard") {
                    com.example.notifications.PushTokenRegistrar.register(this@MainActivity)
                }
                */
                try {
                    if (route == "exam_taking") {
                        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    } else {
                        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            MyApplicationTheme(appTheme = selectedTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    NavHost(
                        navController = navController,
                        startDestination = if (
                            com.example.network.ApiClient.getTokenManager()?.isLoggedIn() == true
                        ) "dashboard" else "onboarding",
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            )
                        }
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(navController)
                        }
                        composable("login_phone") {
                            LoginScreen(
                                navController = navController,
                                onNavigateToOtp = { phone ->
                                    navController.navigate("verify_otp/${android.net.Uri.encode(phone)}")
                                }
                            )
                        }
                        composable("verify_otp/{phoneNumber}") { backStackEntry ->
                            val phone = backStackEntry.arguments?.getString("phoneNumber").orEmpty()
                            VerifyOtpScreen(navController = navController, phoneNumber = phone)
                        }
                        composable("register_route") {
                            com.example.ui.features.auth.register.RegisterScreen(navController = navController)
                        }
                        composable("login_otp") {
                            VerifyOtpScreen(navController = navController)
                        }
                        composable("dashboard") {
                            var lastBackPressTime by remember { mutableStateOf(0L) }
                            val context = androidx.compose.ui.platform.LocalContext.current
                            BackHandler(enabled = true) {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastBackPressTime < 2000) {
                                    (context as? Activity)?.finish()
                                } else {
                                    lastBackPressTime = currentTime
                                    android.widget.Toast.makeText(
                                        context,
                                        "برای خروج، یک‌بار دیگر ضربه بزنید",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            ShetabApp(selectedTheme, navController) { newTheme ->
                                selectedTheme = newTheme
                            }
                        }
                        composable("raya_chat") {
                            com.example.ui.features.raya.RayaChatScreen(navController)
                        }
                        composable("exams_screen") {
                            com.example.ui.features.exams.ExamsScreen(navController)
                        }
                        composable("exam_details/{examId}") { backStackEntry ->
                            val examId = backStackEntry.arguments?.getString("examId").orEmpty()
                            com.example.ui.features.exams.ExamDetailsScreen(
                                navController = navController,
                                examId = examId
                            )
                        }
                        composable("build_exam") {
                            com.example.ui.features.exams.BuildExamScreen(navController)
                        }
                        composable("exam_taking") {
                            com.example.ui.features.exams.ExamTakingScreen(navController)
                        }
                        composable("study_plan") {
                            ShetabApp(selectedTheme, navController, initialTab = 1) { newTheme ->
                                selectedTheme = newTheme
                            }
                        }
                        composable("create_study_plan") {
                            com.example.ui.features.studyplan.CreateStudyPlanScreen(navController)
                        }
                        composable("focus_timer/{taskId}") { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            com.example.ui.features.studyplan.FocusTimerScreen(navController, taskId)
                        }
                        composable("flashcards") {
                            com.example.ui.features.flashcards.FlashcardsScreen(navController)
                        }
                        composable("peer_trouble") {
                            com.example.ui.features.trouble.PeerTroubleScreen(navController)
                        }
                        composable("premium_plans") {
                            com.example.ui.features.premium.PremiumPlansScreen(navController)
                        }
                        composable("academic_report") {
                            com.example.ui.features.academicreport.AcademicReportScreen(navController)
                        }
                        composable("my_group") {
                            com.example.ui.features.mygroup.MyGroupScreen(navController)
                        }
                        composable("league") {
                            com.example.ui.features.leaderboard.CompetitiveLeagueScreen(navController)
                        }
                        composable("notifications") {
                            com.example.ui.features.notifications.NotificationScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
