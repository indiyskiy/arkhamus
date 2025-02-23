package com.arkhamusserver.arkhamus.logic.cache

import com.arkhamusserver.arkhamus.logic.user.relations.OtherUserRelationUpdateLogic
import com.arkhamusserver.arkhamus.logic.user.relations.UserRelationCacheMaker
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class OtherUserRelationCache(
    private val otherUserRelationUpdateLogic: OtherUserRelationUpdateLogic,
    private val userRelationCacheMaker: UserRelationCacheMaker
) {
    private val relationCache: ConcurrentHashMap<Long, List<CachedUserRelation>> = ConcurrentHashMap()
    private val lastUpdateTimestamps: ConcurrentHashMap<Long, Long> = ConcurrentHashMap()

    companion object {
        const val CACHE_EXPIRED_TIME = 15 * 60 * 1000
    }

    fun getCachedRelationsForUser(userId: Long): List<CachedUserRelation> {
        val lastUpdated = lastUpdateTimestamps[userId]
        val now = System.currentTimeMillis()

        return if (lastUpdated != null && (now - lastUpdated) < (CACHE_EXPIRED_TIME)) {
            // Return cached relations if the data is fresh
            relationCache[userId] ?: emptyList()
        } else {
            // Otherwise, perform an immediate update
            val updatedRelations = otherUserRelationUpdateLogic.updateRelations(userId)
            val cachedData = userRelationCacheMaker.mapCache(updatedRelations)
            relationCache[userId] = cachedData
            lastUpdateTimestamps[userId] = now
            cachedData
        }
    }

    fun cleanCacheForUser(userId: Long) {
        relationCache.remove(userId)
        lastUpdateTimestamps.remove(userId)
    }

}