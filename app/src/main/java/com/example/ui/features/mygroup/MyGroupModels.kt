package com.example.ui.features.mygroup

import com.example.network.GroupBadgeDto
import com.example.network.MyGroupBody
import com.example.network.StudyGroupDto

enum class GroupSearchFilter(val labelResId: Int) {
    GROUP_NAME(com.example.R.string.group_filter_name),
    GROUP_ID(com.example.R.string.group_filter_id),
    MEDALS(com.example.R.string.group_filter_medals),
}

enum class GroupTab {
    MEMBERS,
    MEDALS,
}

data class GroupHeaderData(
    val id: String = "2841",
    val numericId: String = "2841",
    val name: String = "قله موفقیت",
    val motto: String = "باهم بهتر، قوی‌تر",
    val roleBadge: String = "عضو",
    val rank: Int = 12,
    val points: Int = 13840,
    val membersCount: Int = 34,
    val profileImageUrl: String? = null,
)

data class GroupBattleData(
    val myGroupName: String = "قله موفقیت",
    val myGroupPoints: Int = 13840,
    val opponentGroupName: String = "ستارگان دانش",
    val opponentGroupPoints: Int = 11920,
    val daysRemaining: Int = 3,
    val currentWeek: Int = 4,
    val totalWeeks: Int = 12,
    val myPercentage: Int = 52,
    val opponentPercentage: Int = 48,
)

data class PersonalGroupStats(
    val rank: Int = 3,
    val points: Int = 2450,
    val studyHours: Int = 18,
    val completedTasks: Int = 330,
)

data class GroupMemberUiModel(
    val userId: String,
    val rank: Int,
    val name: String,
    val isCurrentUser: Boolean = false,
    val points: Int,
    val studyHours: Int,
    val taskCount: Int,
    val avatarBgColor: Long = 0xFF6C5CE7,
    val role: String = "MEMBER",
)

data class StudyGroupScreenUiState(
    val isLoading: Boolean = false,
    val isMember: Boolean = false,
    val errorMessage: String? = null,

    // State 1: Non-member search & filters
    val searchQuery: String = "",
    val activeSearchFilter: GroupSearchFilter = GroupSearchFilter.GROUP_NAME,
    val searchResults: List<StudyGroupDto> = emptyList(),

    // State 2: Active group data
    val groupDetails: GroupHeaderData? = null,
    val activeBattle: GroupBattleData? = null,
    val personalStats: PersonalGroupStats? = null,
    val selectedTab: GroupTab = GroupTab.MEMBERS,
    val members: List<GroupMemberUiModel> = emptyList(),
    val badges: List<GroupBadgeDto> = emptyList(),
    val isExpandedMembers: Boolean = false,
    val rawGroupBody: MyGroupBody? = null,
)
