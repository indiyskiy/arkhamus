package com.arkhamusserver.arkhamus.model.ingame.parts

import com.arkhamusserver.arkhamus.model.enums.SkinColor

data class InGameUserSkinSetting(
    var nickName: String,
    var skinColor: SkinColor,
)