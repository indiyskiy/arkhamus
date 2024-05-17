package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

class ErrorGameResponse(
    var error: String,
    tick: Long
) : RequestProcessData(
    tick = tick
)