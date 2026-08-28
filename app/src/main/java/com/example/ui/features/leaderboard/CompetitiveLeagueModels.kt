package com.example.ui.features.leaderboard

import androidx.compose.ui.graphics.Color

data class LeagueMember(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatarUrl: String,
    val isCurrentUser: Boolean = false,
    val badgeType: LeagueBadgeType = LeagueBadgeType.DIAMOND,
    val rankDiff: Int = 0 // positive = moved up, negative = moved down, 0 = same
)

enum class LeagueBadgeType {
    GOLD_SHIELD,
    SILVER_SHIELD,
    BRONZE_SHIELD,
    DIAMOND
}

enum class LeagueTab(val title: String) {
    ALL("همه"),
    FRIENDS("دوستان"),
    AROUND_ME("اطراف من"),
    RULES("قوانین لیگ")
}

data class CurrentUserLeagueInfo(
    val name: String = "محمد امین",
    val avatarUrl: String = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&q=80",
    val leagueName: String = "لیگ طلایی",
    val seasonName: String = "فصل شهریور",
    val rank: Int = 12,
    val score: Int = 6420,
    val scoreToNextRank: Int = 180,
    val targetLeagueName: String = "لیگ الماس",
    val currentPointsInTier: Int = 6420,
    val requiredPointsForNextTier: Int = 6600,
    val promotionNotice: String = "۳ نفر برتر فصل به لیگ الماس صعود می‌کنند و جوایز ویژه دریافت می‌کنند."
)

object LeagueSampleData {
    val currentUser = CurrentUserLeagueInfo()

    val top3Members = listOf(
        LeagueMember(
            rank = 1,
            name = "سینا قربانی",
            score = 8930,
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&q=80",
            badgeType = LeagueBadgeType.GOLD_SHIELD
        ),
        LeagueMember(
            rank = 2,
            name = "نگار رحیمی",
            score = 8210,
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&q=80",
            badgeType = LeagueBadgeType.SILVER_SHIELD
        ),
        LeagueMember(
            rank = 3,
            name = "پارسا احمدی",
            score = 7280,
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&q=80",
            badgeType = LeagueBadgeType.BRONZE_SHIELD
        )
    )

    val allMembers = listOf(
        LeagueMember(
            rank = 1,
            name = "سینا قربانی",
            score = 8930,
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&q=80",
            badgeType = LeagueBadgeType.GOLD_SHIELD
        ),
        LeagueMember(
            rank = 2,
            name = "نگار رحیمی",
            score = 8210,
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&q=80",
            badgeType = LeagueBadgeType.SILVER_SHIELD
        ),
        LeagueMember(
            rank = 3,
            name = "پارسا احمدی",
            score = 7280,
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&q=80",
            badgeType = LeagueBadgeType.BRONZE_SHIELD
        ),
        LeagueMember(
            rank = 4,
            name = "علی موسوی",
            score = 6940,
            avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 12,
            name = "محمد امین",
            score = 6420,
            avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&q=80",
            isCurrentUser = true,
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 13,
            name = "مینا دادخواه",
            score = 6120,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 14,
            name = "امیرحسین عباسی",
            score = 5980,
            avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 15,
            name = "رضا رستمی",
            score = 5430,
            avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        )
    )

    val friendsMembers = listOf(
        LeagueMember(
            rank = 1,
            name = "نگار رحیمی",
            score = 8210,
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&q=80",
            badgeType = LeagueBadgeType.SILVER_SHIELD
        ),
        LeagueMember(
            rank = 2,
            name = "علی موسوی",
            score = 6940,
            avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 3,
            name = "محمد امین",
            score = 6420,
            avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&q=80",
            isCurrentUser = true,
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 4,
            name = "مینا دادخواه",
            score = 6120,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        )
    )

    val aroundMeMembers = listOf(
        LeagueMember(
            rank = 10,
            name = "سهراب کریمی",
            score = 6600,
            avatarUrl = "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 11,
            name = "مریم نوری",
            score = 6510,
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 12,
            name = "محمد امین",
            score = 6420,
            avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&q=80",
            isCurrentUser = true,
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 13,
            name = "مینا دادخواه",
            score = 6120,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        ),
        LeagueMember(
            rank = 14,
            name = "امیرحسین عباسی",
            score = 5980,
            avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&q=80",
            badgeType = LeagueBadgeType.DIAMOND
        )
    )
}
