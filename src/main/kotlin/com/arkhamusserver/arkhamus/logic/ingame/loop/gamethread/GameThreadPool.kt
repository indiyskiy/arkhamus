package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
class GameThreadPool(
    private val gameRepository: RedisGameRepository,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val gameResponseBuilder: GameResponseBuilder
) {
    var gameTreads: List<ArkhamusGameThread>? = null

    @PostConstruct
    fun initThreads() {
        gameTreads = Collections.synchronizedList(ArrayList<ArkhamusGameThread>()).apply {
            repeat(5) {
                val runnable = ArkhamusGameThread(
                    gameRepository,
                    responseSendingLoopManager,
                    gameResponseBuilder
                )
                add(runnable)
                Thread(runnable).start()
            }
        }
    }

    fun addGame(gameSession: GameSession) {
        if (gameTreads?.any {
                it.isThreadOfGame(gameSession.id ?: 0)
            } == true
        ) {
            return //game already added to thread
        }
        val lessLoadedThread = gameTreads?.minByOrNull { it.size() }
        lessLoadedThread?.addGame(gameSession)
    }

}