package com.arkhamusserver.arkhamus.view.dto.netty.response

interface NettyResponseMessage {
    fun tick(): Long
    fun userId(): Long
    fun myGameUser(): MyGameUserResponseMessage
    fun allGameUsers(): List<GameUserResponseMessage>
}