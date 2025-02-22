package com.arkhamusserver.arkhamus.logic.cache

import com.arkhamusserver.arkhamus.model.enums.UserRelationType

data class CachedUserRelation(
    val id: Long,
    val sourceUserId: Long,
    val targetUserId: Long?,
    val steamId: String?,
    val userRelationType: UserRelationType
)
