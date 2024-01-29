package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import io.netty.channel.Channel
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Component
class ChannelRepository {
    private val channelCache: ConcurrentMap<String, Channel> = ConcurrentHashMap()
    private val arkhamusChannelCache: ConcurrentMap<String, ArkhamusChannel> = ConcurrentHashMap()
    private val arkhamusChannelsOfTheGameCache: ConcurrentMap<Long, ConcurrentMap<String, ArkhamusChannel>> =
        ConcurrentHashMap()

    fun put(arkhamusChannel: ArkhamusChannel) {
        val channelId = arkhamusChannel.channelId
        channelCache[channelId] = arkhamusChannel.channel
        arkhamusChannelCache[channelId] = arkhamusChannel
        if (arkhamusChannel.gameSession != null) {
            addToTheGame(arkhamusChannel, channelId)
        }
    }

    fun update(arkhamusChannel: ArkhamusChannel) {
        val channelId = arkhamusChannel.channelId
        val channel = channelCache[channelId]
        if (channel != null) {
            val oldArkhamusChannel = arkhamusChannelCache[channelId]
            if (oldArkhamusChannel != null) {
                oldArkhamusChannel.gameSession = arkhamusChannel.gameSession
                oldArkhamusChannel.userRole = arkhamusChannel.userRole
                oldArkhamusChannel.userAccount = arkhamusChannel.userAccount
            } else {
                if (arkhamusChannel.gameSession != null) {
                    addToTheGame(arkhamusChannel, channelId)
                }
            }
        }
    }

    fun get(key: String): ArkhamusChannel? {
        return arkhamusChannelCache[key]
    }

    fun getChannel(key: String): Channel? {
        return channelCache[key]
    }

    fun getChannelsOfTheGame(id: Long): ConcurrentMap<String, ArkhamusChannel>? {
        return arkhamusChannelsOfTheGameCache[id]
    }

    fun remove(key: String) {
        channelCache.remove(key)
        val arkhamusChannel = arkhamusChannelCache.remove(key)
        if (arkhamusChannel != null) {
            val gameSessionId = arkhamusChannel.gameSession?.id
            arkhamusChannelsOfTheGameCache[gameSessionId]?.remove(key)
            if (arkhamusChannelsOfTheGameCache[gameSessionId]?.isEmpty() == true) {
                arkhamusChannelsOfTheGameCache.remove(gameSessionId)
            }
        }
    }

    fun size(): Int {
        return arkhamusChannelCache.size
    }

    fun channelsSize(): Int {
        return channelCache.size
    }

    private fun addToTheGame(
        arkhamusChannel: ArkhamusChannel,
        channelId: String
    ) {
        val gameChannels = arkhamusChannelsOfTheGameCache[arkhamusChannel.gameSession?.id]
        if (gameChannels == null) {
            arkhamusChannelsOfTheGameCache[arkhamusChannel.gameSession?.id] =
                ConcurrentHashMap<String, ArkhamusChannel>().apply {
                    put(channelId, arkhamusChannel)
                }
        } else {
            gameChannels[channelId] = arkhamusChannel
        }
    }
}