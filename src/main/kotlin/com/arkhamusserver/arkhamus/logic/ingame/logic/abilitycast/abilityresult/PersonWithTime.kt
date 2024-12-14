package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult

data class PersonWithTime(
    var user: UserActivityView? = null,
    var currentTime: Long = 0,
    var eventTime: Long? = null,
)