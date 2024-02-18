package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class ChannelRepository {
    private val channelCache: ConcurrentMap<String, Channel> = ConcurrentHashMap()
    private val arkhamusChannelCache: ConcurrentMap<String, ArkhamusChannel> = ConcurrentHashMap()
    private val arkhamusUserCache: ConcurrentMap<Long, ArkhamusChannel> = ConcurrentHashMap()

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ChannelRepository::class.java)
    }
    fun put(arkhamusChannel: ArkhamusChannel) {
        try {
            val channelId = arkhamusChannel.channelId
            channelCache[channelId] = arkhamusChannel.channel
            arkhamusChannelCache[channelId] = arkhamusChannel
            arkhamusChannel.userAccount?.id?.let {
                arkhamusUserCache[it] = arkhamusChannel
            }
        } catch (e: Exception){
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
        } catch (e: Exception){
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
        remove(channelId)
    }

    fun remove(key: String) {
        try {
            logger.warn("close and remove socket $key")
            val channel = channelCache.remove(key)
            channel?.close()
            val arkhamusChannel = arkhamusChannelCache.remove(key)
            arkhamusChannel?.userAccount?.id?.let { arkhamusUserCache.remove(it) }
        } catch (e: Exception){
            logger.error("Error occurred while removing ArkhamusChannel", e)
        }
    }

    fun getByGameId(gameId: Long): List<ArkhamusChannel> =
        arkhamusUserCache.values.filter { it.gameSession?.id == gameId }

}