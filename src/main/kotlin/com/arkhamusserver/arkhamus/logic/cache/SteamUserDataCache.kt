package com.arkhamusserver.arkhamus.logic.cache

import com.arkhamusserver.arkhamus.logic.user.steam.SteamReaderLogic
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class SteamUserDataCache(
    private val steamReaderLogic: SteamReaderLogic
) {
    private val relationCache: ConcurrentHashMap<String, CachedSteamData> = ConcurrentHashMap()
    private val lastUpdateTimestamps: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    companion object {
        const val CACHE_EXPIRED_TIME = 5 * 60 * 1000
    }

    fun getCachedSteamData(steamId: String): CachedSteamData? {
        val lastUpdated = lastUpdateTimestamps[steamId]
        val now = System.currentTimeMillis()

        return if (lastUpdated != null && (now - lastUpdated) < (CACHE_EXPIRED_TIME) && relationCache[steamId] != null) {
            // Return cached data if the data is fresh
            relationCache[steamId]!!
        } else {
            // Otherwise, perform an immediate update
            val updatedSteamData = steamReaderLogic.readSteamUserData(steamId)
            val player = updatedSteamData.response?.players?.firstOrNull()
            if (player == null) return null
            val cachedData = CachedSteamData(
                steamId = player.steamid,
                name = player.personaname,
                steamPersonaState = player.personastate.let {
                    SteamPersonaState.fromId(it)
                }
            )
            relationCache[steamId] = cachedData
            lastUpdateTimestamps[steamId] = now
            cachedData
        }
    }

}