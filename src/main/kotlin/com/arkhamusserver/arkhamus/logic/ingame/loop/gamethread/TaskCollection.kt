package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import java.util.*

class TaskCollection {
    private var taskList: MutableList<NettyTickRequestMessageContainer> = Collections.synchronizedList(ArrayList())
    private var userIds: Set<Long> = emptySet()
    private var gameSession: GameSession? = null

    fun isEmpty(): Boolean = taskList.isEmpty()
    fun getByTick(currentTick: Long): List<NettyTickRequestMessageContainer> =
        taskList.filter { it.nettyRequestMessage.baseRequestData().tick == currentTick }

    fun getList(): MutableList<NettyTickRequestMessageContainer> =
        taskList

    fun init(gameSession: GameSession) {
        userIds = gameSession.usersOfGameSession.mapNotNull { it.userAccount.id }.toSet()
        this.gameSession = gameSession
    }

    fun add(container: NettyTickRequestMessageContainer) {
        taskList.add(container)
    }

    fun userIds(): Set<Long> {
        return userIds
    }
}