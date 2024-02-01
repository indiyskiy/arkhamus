package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class ArkhamusGameThread(
    private val gameRepository: RedisGameRepository,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val gameResponseBuilder: GameResponseBuilder,
    private val gamesMap: ConcurrentMap<Long, GameSession> = ConcurrentHashMap(),
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap(),
    private var threadLogic: ArkhamusGameThreadLoopLogic? = null
) : Runnable {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusGameThread::class.java)
    }


    override fun run() {
        logger.info("Thread started")
        try {
            threadLogic = ArkhamusGameThreadLoopLogic(
                gameRepository,
                gamesMap,
                tasksMap,
                gameResponseBuilder,
                responseSendingLoopManager
            )
            threadLogic?.run()
        } finally {
            logger.info("Thread dead")
        }
    }

    fun isThreadOfGame(gameId: Long) =
        gamesMap.contains(gameId)

    fun size(): Int {
        return gamesMap.size
    }

    fun addGame(gameSession: GameSession) {
        threadLogic?.addGame(gameSession)
    }

    fun addTask(task: NettyTickRequestMessageContainer) {
        threadLogic?.addTask(task)
    }

}