package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class UserRitualData(
    private val inRitual: Boolean,
    private val canLeave: Boolean,
    private val leftAlready: Boolean,
    private val usersInRitual: Int,
    private val usersGoingToLeave: Int,
    private val madnessPenalty: Double
)