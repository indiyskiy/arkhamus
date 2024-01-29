package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyTickRequestMessage
import java.util.*


class TaskCollection {
    var taskList: List<NettyTickRequestMessage> = Collections.synchronizedList(ArrayList())
    fun isEmpty(): Boolean = taskList.isEmpty()
    fun getByTick(currentTick: Long): List<NettyTickRequestMessage> =
        taskList.filter{it.tick() == currentTick}
}