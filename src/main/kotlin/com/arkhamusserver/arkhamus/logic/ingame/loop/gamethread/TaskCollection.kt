package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.NettyTickRequestMessageContainer
import java.util.*

class TaskCollection {
    var taskList: MutableList<NettyTickRequestMessageContainer> = Collections.synchronizedList(ArrayList())
    fun isEmpty(): Boolean = taskList.isEmpty()
    fun getByTick(currentTick: Long): List<NettyTickRequestMessageContainer> =
        taskList.filter { it.nettyRequestMessage.baseRequestData().tick == currentTick }

    fun filterOut(tick: Long) {
        taskList =
            Collections.synchronizedList(taskList.filterNot { it.nettyRequestMessage.baseRequestData().tick == tick })
    }
}