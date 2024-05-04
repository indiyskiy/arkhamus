package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import java.util.*

class TaskCollection {
    private var taskList: MutableList<NettyTickRequestMessageDataHolder> = Collections.synchronizedList(ArrayList())
    private var userIds: Set<Long> = emptySet()
    private var gameSession: GameSession? = null

    fun isEmpty(): Boolean = taskList.isEmpty()

    fun getList(): MutableList<NettyTickRequestMessageDataHolder> =
        taskList

    fun resetList() {
        taskList = Collections.synchronizedList(ArrayList())
    }

    fun init(gameSession: GameSession) {
        userIds = gameSession.usersOfGameSession.mapNotNull { it.userAccount.id }.toSet()
        this.gameSession = gameSession
    }

    fun add(container: NettyTickRequestMessageDataHolder):Boolean {
        if (!taskList.any {
                isCompetingRequest(it, container)
            }) {
            taskList.add(container)
            return true
        }
        return false
    }

    private fun isCompetingRequest(
        it: NettyTickRequestMessageDataHolder,
        container: NettyTickRequestMessageDataHolder
    ) = sameUser(it, container) &&
            sameTick(it, container)

    private fun sameTick(
        it: NettyTickRequestMessageDataHolder,
        container: NettyTickRequestMessageDataHolder
    ) = it.nettyRequestMessage.baseRequestData.tick == container.nettyRequestMessage.baseRequestData.tick

    private fun sameUser(
        it: NettyTickRequestMessageDataHolder,
        container: NettyTickRequestMessageDataHolder
    ) = it.userAccount.id == container.userAccount.id

}