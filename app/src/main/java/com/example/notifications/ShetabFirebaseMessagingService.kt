package com.example.notifications

/*
 * Firebase Cloud Messaging is temporarily disabled because the Android
 * Firebase configuration values are not available yet. Keep this source
 * commented until the Gradle fields, dependencies, manifest service, and
 * MainActivity hooks are re-enabled together.
 *
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.BuildConfig
import com.example.R
import com.example.network.ApiClient
import com.example.network.RegisterPushTokenDto
import com.example.network.safeApiCall
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

private const val CHANNEL_ID = "peer_feedback"

object NotificationNavigation {
    val openNotifications = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}

class ShetabFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ApiClient.init(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            PushTokenRegistrar.send(applicationContext, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "بازخورد جدید"
        val body = message.notification?.body ?: return
        createChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_notifications", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            301,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        runCatching { NotificationManagerCompat.from(this).notify(message.messageId?.hashCode() ?: 301, notification) }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "بازخورد لیگ", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}

object PushTokenRegistrar {
    fun register(context: Context) {
        if (ApiClient.getTokenManager()?.isLoggedIn() != true) return
        if (!ensureFirebase(context)) return
        runCatching {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                CoroutineScope(Dispatchers.IO).launch { send(context.applicationContext, token) }
            }
        }
    }

    private fun ensureFirebase(context: Context): Boolean {
        if (FirebaseApp.getApps(context).isNotEmpty()) return true
        val values = listOf(
            BuildConfig.FIREBASE_APPLICATION_ID,
            BuildConfig.FIREBASE_API_KEY,
            BuildConfig.FIREBASE_PROJECT_ID,
            BuildConfig.FIREBASE_SENDER_ID,
        )
        if (values.any { it.isBlank() }) return false
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APPLICATION_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
            .build()
        return FirebaseApp.initializeApp(context.applicationContext, options) != null
    }

    suspend fun send(context: Context, token: String) {
        if (ApiClient.getTokenManager()?.isLoggedIn() != true || token.isBlank()) return
        val preferences = context.getSharedPreferences("push_installation", Context.MODE_PRIVATE)
        val installationId = preferences.getString("id", null) ?: UUID.randomUUID().toString().also {
            preferences.edit().putString("id", it).apply()
        }
        safeApiCall { ApiClient.apiService.registerPushToken(RegisterPushTokenDto(installationId, token)) }
    }

    suspend fun unregister(context: Context) {
        val installationId = context
            .getSharedPreferences("push_installation", Context.MODE_PRIVATE)
            .getString("id", null) ?: return
        safeApiCall { ApiClient.apiService.unregisterPushToken(installationId) }
    }
}
*/
