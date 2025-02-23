package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.logic.cache.CachedUserRelation
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import org.springframework.stereotype.Component

@Component
class UserRelationCacheMaker {
    fun mapCache(updatedRelations: List<UserRelation>): List<CachedUserRelation> =
        updatedRelations.map { relation ->
            CachedUserRelation(
                id = relation.id!!,
                sourceUserId = relation.sourceUser!!.id!!,
                targetUserId = relation.targetUser?.id,
                steamId = relation.targetSteamId,
                userRelationType = relation.userRelationType!!
            )
        }
}
