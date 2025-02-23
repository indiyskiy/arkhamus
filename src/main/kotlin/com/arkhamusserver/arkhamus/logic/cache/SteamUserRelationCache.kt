package com.arkhamusserver.arkhamus.logic.cache

import com.arkhamusserver.arkhamus.logic.user.relations.SteamUserRelationsUpdateLogic
import com.arkhamusserver.arkhamus.logic.user.relations.UserRelationCacheMaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class SteamUserRelationCache(
    private val steamUserUpdateLogic: SteamUserRelationsUpdateLogic,
    private val userRelationCacheMaker: UserRelationCacheMaker
) {
    private val relationCache: ConcurrentHashMap<Long, List<CachedUserRelation>> = ConcurrentHashMap()
    private val lastUpdateTimestamps: ConcurrentHashMap<Long, Long> = ConcurrentHashMap()

    companion object {
        const val CACHE_EXPIRED_TIME = 15 * 60 * 1000
        private val logger = LoggerFactory.getLogger(SteamUserRelationCache::class.java)
    }

    fun getCachedRelationsForUser(userId: Long, steamIds: List<String>): List<CachedUserRelation> {
        val lastUpdated = lastUpdateTimestamps[userId]
        val now = System.currentTimeMillis()

        return if (lastUpdated != null && (now - lastUpdated) < (CACHE_EXPIRED_TIME)) {
            // Return cached relations if the data is fresh
            relationCache[userId] ?: emptyList()
        } else {
            // Otherwise, perform an immediate update
            val updatedRelations = steamUserUpdateLogic.updateSteamUser(userId, steamIds)
            val cachedData = userRelationCacheMaker.mapCache(updatedRelations)
            logger.info("rewrite cache data for user $userId, number of relations: ${cachedData.size}")
            relationCache[userId] = cachedData
            lastUpdateTimestamps[userId] = now
            cachedData
        }
    }

    fun getCachedRelationsForUser(userId: Long): List<CachedUserRelation> {
        val lastUpdated = lastUpdateTimestamps[userId]
        val now = System.currentTimeMillis()

        return if (lastUpdated != null && (now - lastUpdated) < (CACHE_EXPIRED_TIME)) {
            // Return cached relations if the data is fresh
            val result = relationCache[userId] ?: emptyList()
            logger.info("Returning cached relations for user $userId, number of relations: ${result.size}")
            result
        } else {
            // Otherwise, perform an immediate update
            val updatedRelations = steamUserUpdateLogic.readSteamUser(userId)
            val cachedData = userRelationCacheMaker.mapCache(updatedRelations)
            cachedData
        }
    }

}