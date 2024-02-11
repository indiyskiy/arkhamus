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
    private val arkhamusUserCache: ConcurrentMap<Long, ArkhamusChannel> = ConcurrentHashMap()


    fun put(arkhamusChannel: ArkhamusChannel) {
        val channelId = arkhamusChannel.channelId
        channelCache[channelId] = arkhamusChannel.channel
        arkhamusChannelCache[channelId] = arkhamusChannel
        arkhamusChannel.userAccount?.id?.let {
            arkhamusUserCache[it] = arkhamusChannel
        }
    }

    fun update(arkhamusChannel: ArkhamusChannel) {
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
    }

    fun get(key: String): ArkhamusChannel? {
        return arkhamusChannelCache[key]
    }

    fun getUserChannel(key: Long): ArkhamusChannel? {
        return arkhamusUserCache[key]
    }

    //todo remove when game ends
//    fun remove(key: String) {
//        channelCache.remove(key)
//        val arkhamusChannel = arkhamusChannelCache.remove(key)
//        arkhamusChannel?.userAccount?.id?.let { arkhamusUserCache.remove(it) }
//    }
}