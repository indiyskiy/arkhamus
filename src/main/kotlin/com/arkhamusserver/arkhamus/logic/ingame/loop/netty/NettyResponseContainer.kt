package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessageContainer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

data class NettyResponseContainer(
    private val tickMap: ConcurrentMap<Long, MutableList<NettyResponseMessageContainer>> = ConcurrentHashMap()
) {
    fun get(tickId: Long): MutableList<NettyResponseMessageContainer> {
        val tickList = tickMap[tickId]
        return if (tickList == null) {
            val newTickList: MutableList<NettyResponseMessageContainer> = Collections.synchronizedList(ArrayList())
            tickMap[tickId] = newTickList
            newTickList
        } else {
            tickList
        }
    }

    fun put(ettyResponseMessageContainer: NettyResponseMessageContainer) {
        val tick = ettyResponseMessageContainer.nettyResponseMessage.tick()
        val tickList = tickMap[tick]
        if (tickList == null) {
            val newTickList: MutableList<NettyResponseMessageContainer> = Collections.synchronizedList(ArrayList())
            newTickList.add(ettyResponseMessageContainer)
            tickMap[tick] = newTickList
        } else {
            tickList.add(ettyResponseMessageContainer)
        }
    }

    fun remove(tickId: Long) {
        tickMap.remove(tickId)
    }

    fun isEmpty() = tickMap.isEmpty()
    fun size() = tickMap.size
    fun forEach(function: (NettyResponseMessageContainer) ->Any?): Set<Long> {
        return tickMap.map {
            it.value.forEach {
                function.invoke(it)
            }
            it.key
        }.toSet()
    }

}