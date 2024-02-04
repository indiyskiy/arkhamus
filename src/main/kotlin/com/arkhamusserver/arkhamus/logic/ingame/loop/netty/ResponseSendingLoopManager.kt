package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.ArkhamusGameThreadLoopLogic
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessageContainer
import com.google.gson.Gson
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class ResponseSendingLoopManager(
    val channelRepository: ChannelRepository
) : Runnable {
    private val sendingResponseMap: ConcurrentMap<Long, NettyResponseContainer> = ConcurrentHashMap()
    private val bufferResponseMap: ConcurrentMap<Long, NettyResponseContainer> = ConcurrentHashMap()
    val locker = Object()
    val gson = Gson()

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ResponseSendingLoopManager::class.java)
    }

    @PostConstruct
    fun start() {
        Thread(this).start()
    }

    override fun run() {
        synchronized(locker) {
            ArkhamusGameThreadLoopLogic.logger.info("endless sending loop started")
            while (true) {
                if (
                    sendingResponseMap.isEmpty() ||
                    sendingResponseMap.all { it.value.isEmpty() }
                ) {
                    try {
                        ArkhamusGameThreadLoopLogic.logger.info("endless sending loop goes to sleep")
                        locker.wait()
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw RuntimeException(e)
                    }
                }
                ArkhamusGameThreadLoopLogic.logger.info("sending ${sendingResponseMap.size} game packages")
                val setToRemove = mutableSetOf<Pair<Long, Set<Long>>>()
                sendingResponseMap.forEach {
                    val gameId = it.key
                    val nettyResponseContainer = it.value
                    ArkhamusGameThreadLoopLogic.logger.info("sending ${nettyResponseContainer.size()} ticks")
                    val ticks = sendAllFoGame(nettyResponseContainer)
                    setToRemove.add(gameId to ticks)
                }
                setToRemove.forEach { (gameId, ticks) ->
                    val sendingResponseContainer = sendingResponseMap[gameId]
                    if (sendingResponseContainer != null) {
                        ticks.forEach { tick ->
                            sendingResponseContainer.remove(tick)
                        }
                    }
                }
            }
        }
    }

    private fun sendAllFoGame(nettyResponseContainer: NettyResponseContainer) =
        nettyResponseContainer.forEach { messageResponseContainer: NettyResponseMessageContainer ->
            val channel = channelRepository.getChannel(messageResponseContainer.channelId)
            channel?.writeAndFlush(
                gson.toJson(
                    messageResponseContainer.nettyResponseMessage
                )
            )
        }

    fun addResponse(
        response: NettyResponseMessage,
        userId: Long,
        tick: Long,
        game: RedisGame,
        gameId: Long,
        channelId: String
    ) {
        logger.info("add response ${response.javaClass.simpleName}")
        var responseContainer = bufferResponseMap[gameId]
        if (responseContainer == null) {
            responseContainer = NettyResponseContainer()
            bufferResponseMap[gameId] = responseContainer
        }
        responseContainer.put(
            NettyResponseMessageContainer(
                response,
                userId,
                channelId
            )
        )
    }

    fun flush(tick: Long, game: RedisGame, gameId: Long) {
        synchronized(locker) {
            logger.info("FLUSH! ${bufferResponseMap.size}")
            val nettyResponseContainer = bufferResponseMap[gameId]
            sendingResponseMap[gameId] = nettyResponseContainer
            bufferResponseMap.remove(gameId)
            locker.notify()
        }
    }
}