package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.abilityresult

import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting

data class UserActivityView(
    var id: Long? = null,
    var nickName: String? = null,
    var skin: InGameUserSkinSetting? = null,
)