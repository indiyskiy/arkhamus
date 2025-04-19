package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import io.netty.channel.Channel
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class ChannelRepository {
    private val channelCache: ConcurrentMap<String, Channel> = ConcurrentHashMap()
    private val arkhamusChannelCache: ConcurrentMap<String, ArkhamusChannel> = ConcurrentHashMap()
    private val arkhamusUserCache: ConcurrentMap<Long, ArkhamusChannel> = ConcurrentHashMap()

    companion object {
        private val logger = LoggingUtils.getLogger<ChannelRepository>()
    }

    fun put(arkhamusChannel: ArkhamusChannel) {
        try {
            val channelId = arkhamusChannel.channelId
            channelCache[channelId] = arkhamusChannel.channel
            arkhamusChannelCache[channelId] = arkhamusChannel
            arkhamusChannel.userAccount?.id?.let {
                arkhamusUserCache[it] = arkhamusChannel
            }
        } catch (e: Exception) {
            logger.error("Error occurred while putting ArkhamusChannel into cache", e)
        }
    }

    fun update(arkhamusChannel: ArkhamusChannel) {
        try {
            val channelId = arkhamusChannel.channelId
            val channel = channelCache[channelId]
            if (channel != null) {
                val oldArkhamusChannel = arkhamusChannelCache[channelId]
                if (oldArkhamusChannel != null) {
                    oldArkhamusChannel.gameSession = arkhamusChannel.gameSession
                    oldArkhamusChannel.userOfGameSession = arkhamusChannel.userOfGameSession
                    oldArkhamusChannel.userAccount = arkhamusChannel.userAccount
                }
                arkhamusChannel.userAccount?.id?.let {
                    arkhamusUserCache[it] = arkhamusChannel
                }
            }
        } catch (e: Exception) {
            logger.error("Error occurred while updating ArkhamusChannel", e)
        }
    }

    fun get(key: String): ArkhamusChannel? {
        return arkhamusChannelCache[key]
    }

    fun getUserChannel(key: Long): ArkhamusChannel? {
        return arkhamusUserCache[key]
    }

    fun closeAndRemove(arkhamusChannel: ArkhamusChannel) {
        val key = arkhamusChannel.channelId
        closeAndRemove(key)
    }

    fun closeAndRemove(channelId: String) {
        try {
            logger.warn("close and remove socket $channelId")
            val channel = channelCache.remove(channelId)
            channel?.close()?.sync()
            val arkhamusChannel = arkhamusChannelCache.remove(channelId)
            arkhamusChannel?.userAccount?.id?.let { arkhamusUserCache.remove(it) }
        } catch (e: Exception) {
            logger.error("Error occurred while removing ArkhamusChannel", e)
        }
    }

    fun getByGameId(gameId: Long): List<ArkhamusChannel> =
        arkhamusUserCache.values.filter { it.gameSession?.id == gameId }

    fun updateGame(gameSession: GameSession) {
        arkhamusChannelCache.forEach { (_, value) ->
            if (value.gameSession != null && value!!.gameSession!!.id == gameSession.id) {
                value.gameSession = gameSession
            }
        }
        arkhamusUserCache.forEach { (_, value) ->
            if (value.gameSession != null && value!!.gameSession!!.id == gameSession.id) {
                value.gameSession = gameSession
            }
        }
    }

    fun allArkhamusChannels() = arkhamusChannelCache.values

}