package com.arkhamusserver.arkhamus.view.dto.netty.request

interface ActionRequestMessage {
    fun actionId(): Long
    fun updateActionId(actionId: Long)
}