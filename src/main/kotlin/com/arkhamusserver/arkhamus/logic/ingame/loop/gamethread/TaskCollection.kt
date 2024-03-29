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
        taskList.filter { it.nettyRequestMessage.baseRequestData.tick == currentTick }

    fun getList(): MutableList<NettyTickRequestMessageContainer> =
        taskList

    fun resetList() {
        taskList = Collections.synchronizedList(ArrayList())
    }

    fun init(gameSession: GameSession) {
        userIds = gameSession.usersOfGameSession.mapNotNull { it.userAccount.id }.toSet()
        this.gameSession = gameSession
    }

    fun add(container: NettyTickRequestMessageContainer):Boolean {
        if (!taskList.any {
                isCompetingRequest(it, container)
            }) {
            taskList.add(container)
            return true
        }
        return false
    }

    private fun isCompetingRequest(
        it: NettyTickRequestMessageContainer,
        container: NettyTickRequestMessageContainer
    ) = sameUser(it, container) &&
            sameTick(it, container)

    private fun sameTick(
        it: NettyTickRequestMessageContainer,
        container: NettyTickRequestMessageContainer
    ) = it.nettyRequestMessage.baseRequestData.tick == container.nettyRequestMessage.baseRequestData.tick

    private fun sameUser(
        it: NettyTickRequestMessageContainer,
        container: NettyTickRequestMessageContainer
    ) = it.userAccount.id == container.userAccount.id

    fun userIds(): Set<Long> {
        return userIds
    }
}