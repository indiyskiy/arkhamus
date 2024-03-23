package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

class ErrorGameResponse(
    var error: String,
    tick: Long
) : RequestProcessData(tick)