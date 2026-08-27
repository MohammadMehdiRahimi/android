package com.example.network

import org.json.JSONObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/send-otp") suspend fun requestOtp(@Body request: OtpRequestDto): Response<OtpRequestResponseDto>
    @POST("auth/verify-otp") suspend fun verifyOtp(@Body request: OtpVerifyDto): Response<OtpVerifyResponseDto>
    @POST("auth/register") suspend fun register(@Body request: RegisterRequest): Response<AuthResponseDto>
    @POST("auth/logout") suspend fun logout(): Response<SimpleResponseDto>
    @Headers("No-Authentication: true")
    @GET("base-info/onboarding") suspend fun getOnboardingOptions(): Response<OnboardingOptionsResponseDto>
    @PUT("student-profile/me/onboarding") suspend fun completeOnboarding(@Body request: CompleteOnboardingDto): Response<SimpleResponseDto>
    @GET("users/me") suspend fun getMe(): Response<UserProfileResponseDto>
    @Multipart
    @POST("users/me/profile-image")
    suspend fun uploadMyProfileImage(@Part file: MultipartBody.Part): Response<UserProfileResponseDto>
    @POST("progression/me/check-in") suspend fun checkIn(): Response<ProgressionResponseDto>
    @GET("progression/me/dashboard") suspend fun getProgressDashboard(): Response<ProgressDashboardResponseDto>
    @GET("progression/me/performance") suspend fun getPerformance(@Query("range") range: String): Response<PerformanceResponseDto>
    @GET("progression/leagues/me/leaderboard") suspend fun getCurrentLeagueLeaderboard(@Query("page") page: Int = 1, @Query("limit") limit: Int = 50): Response<LeagueLeaderboardResponseDto>
    @GET("progression/leagues/me/members/{userId}/activity") suspend fun getLeagueMemberActivity(@Path("userId") userId: String): Response<MemberActivityResponseDto>
    @GET("progression/feedback/options") suspend fun getFeedbackOptions(): Response<FeedbackOptionsResponseDto>
    @POST("progression/leagues/me/members/{userId}/feedback") suspend fun sendPeerFeedback(@Path("userId") userId: String, @Body request: SendPeerFeedbackDto): Response<SendPeerFeedbackResponseDto>
    @GET("progression/notifications") suspend fun getProgressionNotifications(@Query("page") page: Int = 1, @Query("limit") limit: Int = 50): Response<NotificationListResponseDto>
    @GET("progression/notifications/unread-count") suspend fun getUnreadNotificationCount(): Response<UnreadCountResponseDto>
    @PATCH("progression/notifications/{id}/read") suspend fun markNotificationRead(@Path("id") id: String): Response<SimpleResponseDto>
    @PATCH("progression/notifications/read-all") suspend fun markAllNotificationsRead(): Response<SimpleResponseDto>
    @POST("progression/push-tokens") suspend fun registerPushToken(@Body request: RegisterPushTokenDto): Response<SimpleResponseDto>
    @DELETE("progression/push-tokens/{installationId}") suspend fun unregisterPushToken(@Path("installationId") installationId: String): Response<SimpleResponseDto>
    @GET("study-groups/me") suspend fun getMyGroup(): Response<MyGroupResponseDto>
    @GET("study-groups/search") suspend fun searchGroups(@Query("search") search: String, @Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<GroupSearchResponseDto>
    @POST("study-groups") suspend fun createGroup(@Body request: CreateGroupDto): Response<GroupResponseDto>
    @POST("study-groups/{id}/request-join") suspend fun requestJoin(@Path("id") id: String): Response<SimpleResponseDto>
    @GET("study-groups/{id}/challenges") suspend fun getChallenges(@Path("id") id: String): Response<ChallengeListResponseDto>
    @POST("study-groups/{id}/challenges") suspend fun createChallenge(@Path("id") id: String, @Body request: CreateChallengeDto): Response<SimpleResponseDto>
    @GET("study-groups/{id}/challenges/{challengeId}") suspend fun getChallengeDetail(@Path("id") id: String, @Path("challengeId") challengeId: String, @Query("page") page: Int = 1, @Query("limit") limit: Int = 100): Response<ChallengeDetailResponseDto>
    @DELETE("study-groups/{id}/challenges/{challengeId}/members/{userId}") suspend fun removeFailedChallengeMember(@Path("id") id: String, @Path("challengeId") challengeId: String, @Path("userId") userId: String): Response<SimpleResponseDto>
    @GET("study-groups/{id}/tournaments/current") suspend fun getCurrentBattle(@Path("id") id: String): Response<BattleResponseDto>
    @GET("study-groups/{id}/tournaments/history") suspend fun getBattleHistory(@Path("id") id: String): Response<BattleHistoryResponseDto>
    @POST("study-groups/{id}/tournaments/matchmake") suspend fun startMatchmaking(@Path("id") id: String): Response<SimpleResponseDto>
    @POST("study-groups/{id}/battles/invitations") suspend fun inviteBattle(@Path("id") id: String, @Body request: InviteBattleDto): Response<SimpleResponseDto>
    @GET("study-groups/{id}/battles/invitations") suspend fun getBattleInvitations(@Path("id") id: String): Response<BattleInvitationListResponseDto>
    @POST("study-groups/battles/invitations/{id}/respond") suspend fun respondBattleInvitation(@Path("id") id: String, @Body request: RespondBattleInvitationDto): Response<SimpleResponseDto>
    @GET("study-groups/{id}/requests") suspend fun getJoinRequests(@Path("id") id: String): Response<JoinRequestListResponseDto>
    @POST("study-groups/requests/{id}/approve") suspend fun approveJoinRequest(@Path("id") id: String): Response<SimpleResponseDto>
    @POST("study-groups/requests/{id}/reject") suspend fun rejectJoinRequest(@Path("id") id: String): Response<SimpleResponseDto>
    @GET("study-groups/{id}/badges") suspend fun getBadges(@Path("id") id: String): Response<BadgeListResponseDto>
    @PATCH("study-groups/{id}/settings") suspend fun updateGroup(@Path("id") id: String, @Body request: UpdateGroupDto): Response<GroupResponseDto>
    @Multipart
    @POST("study-groups/{id}/profile-image")
    suspend fun uploadGroupProfileImage(
        @Path("id") id: String,
        @Part file: MultipartBody.Part,
    ): Response<GroupResponseDto>
    @DELETE("study-groups/me/leave") suspend fun leaveGroup(): Response<SimpleResponseDto>
    @DELETE("study-groups/{id}") suspend fun deleteGroup(@Path("id") id: String): Response<SimpleResponseDto>
    @PATCH("study-groups/{id}/members/{userId}/role") suspend fun updateMemberRole(@Path("id") id: String, @Path("userId") userId: String, @Body request: UpdateMemberRoleDto): Response<SimpleResponseDto>
    @GET("study-tasks/me/catalog") suspend fun getStudyTaskCatalog(): Response<StudyTaskCatalogResponseDto>
    @GET("study-tasks/me") suspend fun getStudyTasks(@Query("date") date: String): Response<DailyStudyTasksResponseDto>
    @POST("study-tasks/me/manual") suspend fun createManualStudyTask(@Body request: CreateManualStudyTaskDto): Response<ManualStudyTaskResponseDto>
    @PATCH("study-tasks/me/manual/{taskId}") suspend fun updateManualStudyTask(@Path("taskId") taskId: String, @Body request: UpdateManualStudyTaskDto): Response<ManualStudyTaskResponseDto>
    @DELETE("study-tasks/me/manual/{taskId}") suspend fun cancelManualStudyTask(@Path("taskId") taskId: String): Response<SimpleResponseDto>
    @POST("study-execution/me/items/{taskId}/events") suspend fun submitGeneratedStudyEvent(@Path("taskId") taskId: String, @Body request: StudyExecutionEventDto): Response<StudyExecutionResponseDto>
    @POST("study-execution/me/manual-tasks/{taskId}/events") suspend fun submitManualStudyEvent(@Path("taskId") taskId: String, @Body request: StudyExecutionEventDto): Response<StudyExecutionResponseDto>
}

data class OtpRequestDto(val phone: String)
data class OtpRequestBody(val message: String? = null, val expiresIn: Int? = null)
data class OtpRequestResponseDto(val body: OtpRequestBody? = null, val status: String? = null, val statusCode: Int? = null)
data class OtpVerifyDto(val phone: String, val otp: String, val deviceType: String = "ANDROID")
data class ProgressionTierDto(
    val id: String? = null,
    val nameFa: String? = null,
    val minimumPoints: Long = 0,
    val colorHex: String? = null,
    val iconKey: String? = null,
)
data class ProgressionDto(
    val userId: String? = null,
    val points: Long = 0,
    val streak: Int = 0,
    val title: ProgressionTierDto? = null,
    val league: ProgressionTierDto? = null,
    val nextTitle: ProgressionTierDto? = null,
    val nextLeague: ProgressionTierDto? = null,
)
data class UserDto(
    val id: String? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val role: String? = "STUDENT",
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val sessionExpiresAt: Long? = null,
    val progression: ProgressionDto? = null,
)
data class OnboardingStateDto(val required: Boolean = false, val missingFields: List<String> = emptyList())
data class AuthBodyDto(
    val isNew: Boolean = false,
    val registrationToken: String? = null,
    val registrationTokenExpiresIn: Int? = null,
    val accessToken: String? = null,
    val accessExpiresAt: Long? = null,
    val refreshExpiresAt: Long? = null,
    val sessionId: String? = null,
    val user: UserDto? = null,
    val onboarding: OnboardingStateDto? = null,
)
data class OtpVerifyResponseDto(val body: AuthBodyDto? = null, val statusCode: Int? = null)
data class AuthResponseDto(val body: AuthBodyDto? = null, val statusCode: Int? = null)
data class RegisterRequest(
    val phone: String,
    val registrationToken: String,
    val deviceType: String = "ANDROID",
    val fullName: String,
    val grade: String,
    val fieldOfStudy: String? = null
)
data class CompleteOnboardingDto(val fullName: String, val grade: String, val fieldOfStudy: String? = null)
data class AcademicOptionDto(
    val code: String = "",
    val label: String = "",
    val key: String? = null,
    val value: String? = null,
    val sortOrder: Int = 0,
    val requiresFieldOfStudy: Boolean = false
) {
    constructor(code: String, label: String, requiresFieldOfStudy: Boolean = false) : this(
        code = code,
        label = label,
        key = code,
        value = label,
        sortOrder = 0,
        requiresFieldOfStudy = requiresFieldOfStudy
    )

    val effectiveKey: String get() = key?.ifBlank { null } ?: code
    val effectiveValue: String get() = value?.ifBlank { null } ?: label
}
data class OnboardingOptionsBody(val grades: List<AcademicOptionDto> = emptyList(), val fieldsOfStudy: List<AcademicOptionDto> = emptyList())
data class OnboardingOptionsResponseDto(
    val body: OnboardingOptionsBody? = null,
    val grades: List<AcademicOptionDto>? = null,
    val fieldsOfStudy: List<AcademicOptionDto>? = null,
    val statusCode: Int? = null
) {
    val resolvedGrades: List<AcademicOptionDto>
        get() = body?.grades?.takeIf { it.isNotEmpty() } ?: grades ?: emptyList()

    val resolvedFieldsOfStudy: List<AcademicOptionDto>
        get() = body?.fieldsOfStudy?.takeIf { it.isNotEmpty() } ?: fieldsOfStudy ?: emptyList()
}
data class UserProfileResponseDto(val body: UserDto? = null, val statusCode: Int? = null)
data class ProgressionResponseDto(val body: ProgressionDto? = null, val statusCode: Int? = null)
data class SimpleResponseDto(val body: Any? = null, val statusCode: Int? = null)

data class ProgressDashboardBodyDto(
    val points: Long = 0,
    val rank: Int = 0,
    val leagueMemberCount: Int = 0,
    val totalStudySeconds: Long = 0,
    val league: ProgressionTierDto? = null,
)
data class ProgressDashboardResponseDto(val body: ProgressDashboardBodyDto? = null)
data class PerformanceBucketDto(
    val key: String,
    val label: String,
    val startAt: String,
    val endAt: String? = null,
    val value: Long = 0,
    val isFuture: Boolean = false,
)
data class PerformanceBodyDto(
    val range: String,
    val metric: String,
    val timezone: String,
    val buckets: List<PerformanceBucketDto> = emptyList(),
)
data class PerformanceResponseDto(val body: PerformanceBodyDto? = null)
data class LeagueMemberDto(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String? = null,
    val points: Long = 0,
    val rank: Int = 0,
    val totalStudySeconds: Long = 0,
    val totalTestCount: Int = 0,
    val isMe: Boolean = false,
)
data class LeagueLeaderboardBodyDto(
    val league: ProgressionTierDto? = null,
    val data: List<LeagueMemberDto> = emptyList(),
    val me: LeagueMemberDto? = null,
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 50,
)
data class LeagueLeaderboardResponseDto(val body: LeagueLeaderboardBodyDto? = null)
data class ActivityWindowDto(val studySeconds: Long = 0, val testCount: Int = 0)
data class MemberActivityBodyDto(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String? = null,
    val today: ActivityWindowDto = ActivityWindowDto(),
    val last7Days: ActivityWindowDto = ActivityWindowDto(),
    val last30Days: ActivityWindowDto = ActivityWindowDto(),
    val allTime: ActivityWindowDto = ActivityWindowDto(),
)
data class MemberActivityResponseDto(val body: MemberActivityBodyDto? = null)
data class FeedbackOptionDto(val code: String, val labelFa: String)
data class FeedbackOptionsResponseDto(val body: List<FeedbackOptionDto>? = emptyList())
data class SendPeerFeedbackDto(val code: String)
data class SentFeedbackBodyDto(val id: String, val code: String, val labelFa: String, val createdAt: String, val nextAllowedAt: String)
data class SendPeerFeedbackResponseDto(val body: SentFeedbackBodyDto? = null)
data class StudentNotificationDto(
    val id: String,
    val type: String,
    val titleFa: String,
    val bodyFa: String,
    val actorId: String? = null,
    val actorName: String? = null,
    val actorProfileImageUrl: String? = null,
    val feedbackId: String? = null,
    val readAt: String? = null,
    val createdAt: String,
)
data class NotificationListBodyDto(
    val data: List<StudentNotificationDto> = emptyList(),
    val total: Int = 0,
    val unreadCount: Int = 0,
)
data class NotificationListResponseDto(val body: NotificationListBodyDto? = null)
data class UnreadCountBodyDto(val count: Int = 0)
data class UnreadCountResponseDto(val body: UnreadCountBodyDto? = null)
data class RegisterPushTokenDto(val installationId: String, val token: String)

data class StudyGroupDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val profileImageUrl: String? = null,
    val ownerId: String,
    val inviteCode: String,
    val isPublic: Boolean,
    val capacity: Int = 20,
    val totalGroupPoints: Int = 0,
)
data class GroupMemberDto(val userId: String, val fullName: String = "کاربر شتاب", val role: String = "MEMBER", val points: Int = 0, val studyMinutes: Int = 0, val testCount: Int = 0)
data class WeeklyStatsDto(val points: Int = 0, val studyMinutes: Int = 0, val testCount: Int = 0)
data class MembershipDto(val role: String = "MEMBER")
data class MyGroupBody(val group: StudyGroupDto, val member: MembershipDto, val weeklyStats: WeeklyStatsDto = WeeklyStatsDto(), val members: List<GroupMemberDto> = emptyList())
data class MyGroupResponseDto(val body: MyGroupBody? = null)
data class GroupResponseDto(val body: StudyGroupDto? = null)
data class GroupSearchBody(val data: List<StudyGroupDto> = emptyList(), val total: Int = 0)
data class GroupSearchResponseDto(val body: GroupSearchBody? = null)
data class CreateGroupDto(val name: String, val description: String? = null, val capacity: Int, val isPublic: Boolean)
data class UpdateGroupDto(val name: String? = null, val description: String? = null, val capacity: Int? = null, val isPublic: Boolean? = null)
data class UpdateMemberRoleDto(val role: String)

data class ChallengeProgressDto(
    val occurrenceCount: Int = 0,
    val succeededOccurrences: Int = 0,
    val failedOccurrences: Int = 0,
    val completedMemberCount: Int = 0,
    val requiredMemberCount: Int = 0,
    val allMembersRequired: Boolean = true,
)
data class ChallengeDto(
    val id: String,
    val title: String,
    val metric: String,
    val period: String,
    val targetValue: Int,
    val targetTime: String? = null,
    val allowedLatenessMinutes: Int = 0,
    val status: String,
    val progress: ChallengeProgressDto? = null,
)
data class ChallengeListResponseDto(val body: List<ChallengeDto>? = emptyList())
data class CreateChallengeDto(
    val title: String,
    val metric: String,
    val period: String,
    val targetValue: Int,
    val targetTime: String? = null,
    val allowedLatenessMinutes: Int = 0,
    val startsAt: String,
    val endsAt: String,
)
data class ChallengeOccurrenceDto(
    val id: String,
    val periodKey: String,
    val status: String,
    val eligibleMemberCount: Int = 0,
    val completedMemberCount: Int = 0,
)
data class ChallengeMemberProgressDto(
    val userId: String,
    val fullName: String,
    val role: String = "MEMBER",
    val actualValue: Int = 0,
    val targetValue: Int = 0,
    val status: String,
    val canRemove: Boolean = false,
)
data class ChallengeDetailBodyDto(
    val challenge: ChallengeDto,
    val occurrences: List<ChallengeOccurrenceDto> = emptyList(),
    val currentOccurrenceId: String? = null,
    val members: List<ChallengeMemberProgressDto> = emptyList(),
    val page: Int = 1,
    val limit: Int = 100,
    val total: Int = 0,
)
data class ChallengeDetailResponseDto(val body: ChallengeDetailBodyDto? = null)
data class InviteBattleDto(val opponentGroupId: String)
data class RespondBattleInvitationDto(val accept: Boolean)
data class BattleInvitationDto(val id: String, val challengerGroupId: String, val opponentGroupId: String, val status: String, val expiresAt: String)
data class BattleInvitationListResponseDto(val body: List<BattleInvitationDto>? = emptyList())
data class JoinRequestDto(val id: String, val userId: String, val status: String, val createdAt: String)
data class JoinRequestListResponseDto(val body: List<JoinRequestDto>? = emptyList())
data class BattleGroupDto(val id: String, val name: String)
data class BattleMatchDto(val id: String, val status: String, val groupAId: String, val groupBId: String, val groupA: BattleGroupDto? = null, val groupB: BattleGroupDto? = null, val groupAPoints: Int = 0, val groupBPoints: Int = 0, val winnerGroupId: String? = null, val prizeGroupPoints: Int = 0, val startedAt: String, val endsAt: String)
data class CurrentBattleBody(val match: BattleMatchDto, val groupAPoints: Int = 0, val groupBPoints: Int = 0)
data class BattleResponseDto(val body: CurrentBattleBody? = null)
data class BattleHistoryBody(val data: List<BattleMatchDto> = emptyList(), val total: Int = 0)
data class BattleHistoryResponseDto(val body: BattleHistoryBody? = null)
data class BadgeDefinitionDto(val id: String, val name: String, val description: String? = null, val iconKey: String? = null)
data class GroupBadgeDto(val id: String, val earnedAt: String, val badge: BadgeDefinitionDto)
data class BadgeListResponseDto(val body: List<GroupBadgeDto>? = emptyList())

data class StudyTaskTopicDto(
    val id: String,
    val name: String,
    val order: Int = 0,
)

data class StudyTaskChapterDto(
    val id: String,
    val name: String,
    val order: Int = 0,
    val topics: List<StudyTaskTopicDto> = emptyList(),
)

data class StudyTaskBookDto(
    val id: String,
    val name: String,
    val grade: String? = null,
    val fieldOfStudy: List<String> = emptyList(),
    val chapters: List<StudyTaskChapterDto> = emptyList(),
)

data class StudyTaskAcademicProfileDto(
    val grade: String? = null,
    val fieldOfStudy: String? = null,
)

data class StudyTaskCatalogBodyDto(
    val academicProfile: StudyTaskAcademicProfileDto? = null,
    val books: List<StudyTaskBookDto> = emptyList(),
)

data class StudyTaskCatalogResponseDto(val body: StudyTaskCatalogBodyDto? = null)

data class StudyTaskNamedRefDto(
    val id: String,
    val name: String,
)

data class StudyTaskExecutionDto(
    val id: String,
    val status: String,
    val eventSequence: Int = 0,
    val activeSeconds: Int = 0,
    val completionPercent: Int? = null,
    val startedAt: String? = null,
    val activeSinceAt: String? = null,
    val stoppedAt: String? = null,
    val finishedAt: String? = null,
)

data class StudyTaskDto(
    val id: String,
    val sourceType: String,
    val sourceId: String? = null,
    val planId: String? = null,
    val title: String,
    val book: StudyTaskNamedRefDto,
    val chapter: StudyTaskNamedRefDto,
    val topic: StudyTaskNamedRefDto,
    val scheduledOn: String,
    val periodCount: Int = 1,
    val minutesPerPeriod: Int,
    val plannedMinutes: Int,
    val activityType: String? = null,
    val sequence: Int = 0,
    val execution: StudyTaskExecutionDto? = null,
)

data class StudyTaskSummaryDto(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val completionPercent: Int = 0,
)

data class DailyStudyTasksBodyDto(
    val date: String,
    val items: List<StudyTaskDto> = emptyList(),
    val summary: StudyTaskSummaryDto = StudyTaskSummaryDto(),
)

data class DailyStudyTasksResponseDto(val body: DailyStudyTasksBodyDto? = null)

data class CreateManualStudyTaskDto(
    val requestId: String,
    val topicId: String,
    val scheduledOn: String,
    val periodCount: Int,
    val minutesPerPeriod: Int,
)

data class UpdateManualStudyTaskDto(
    val topicId: String? = null,
    val scheduledOn: String? = null,
    val periodCount: Int? = null,
    val minutesPerPeriod: Int? = null,
)

data class ManualStudyTaskBodyDto(
    val id: String,
    val sourceType: String = "MANUAL",
    val title: String,
    val scheduledOn: String,
    val book: StudyTaskNamedRefDto,
    val chapter: StudyTaskNamedRefDto,
    val topic: StudyTaskNamedRefDto,
    val periodCount: Int,
    val minutesPerPeriod: Int,
    val plannedMinutes: Int,
    val status: String,
    val execution: StudyTaskExecutionDto? = null,
)

data class ManualStudyTaskResponseDto(val body: ManualStudyTaskBodyDto? = null)

data class StudyExecutionEventDto(
    val clientEventId: String,
    val expectedSequence: Int,
    val type: String,
    val occurredAt: String,
    val completionOutcome: String? = null,
    val completionPercent: Int? = null,
    val note: String? = null,
)

data class StudyExecutionBodyDto(
    val id: String = "",
    val manualTaskId: String? = null,
    val status: String = "",
    val eventSequence: Int = 0,
    val plannedMinutes: Int = 0,
    val actualSeconds: Int = 0,
    val persistedActiveSeconds: Int = 0,
    val timerElapsedSeconds: Int = 0,
    val pausedSeconds: Int = 0,
    val completionPercent: Int? = null,
    val startedAt: String? = null,
    val activeSinceAt: String? = null,
    val stoppedAt: String? = null,
    val finishedAt: String? = null,
)

data class StudyExecutionResponseDto(val body: StudyExecutionBodyDto? = null)

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> = try {
    val response = apiCall()
    val body = response.body()
    if (response.isSuccessful && body != null) NetworkResult.Success(body) else {
        val raw = response.errorBody()?.string()
        var message = "خطایی رخ داده است (${response.code()})"
        if (!raw.isNullOrBlank()) {
            message = runCatching {
                val json = JSONObject(raw)
                val error = json.optJSONObject("body") ?: json
                error.optString("messageFa").ifBlank { error.optString("message").ifBlank { error.optString("messageEn", raw) } }
            }.getOrDefault(raw)
        }
        NetworkResult.Error(response.code(), message)
    }
} catch (error: Exception) { NetworkResult.Exception(error) }
