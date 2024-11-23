package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameNettyLogic(
    val gameThreadPool: GameThreadPool,
    val userRepository: RedisGameUserRepository
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameNettyLogic::class.java)
    }

    fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageDataHolder
    ) {
        try {
            gameThreadPool.addTask(nettyTickRequestMessageContainer)
        } catch (exception: Exception) {
            logger.error("error on processing request", exception)
        }
    }

    @Transactional
    fun markPlayerDisconnected(user: UserOfGameSession) {
        userRepository.findByUserIdAndGameId(user.userAccount.id!!, user.gameSession.id!!).firstOrNull()?.let { redisUser ->
            redisUser.connected = false
            userRepository.save(redisUser)
        }
    }

    @Transactional
    fun markPlayerConnected(user: UserOfGameSession) {
        userRepository.findByUserIdAndGameId(user.userAccount.id!!, user.gameSession.id!!).firstOrNull()?.let { redisUser ->
            redisUser.connected = true
            userRepository.save(redisUser)
        }
    }
}