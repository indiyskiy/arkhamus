package com.arkhamusserver.arkhamus.model.ingame.parts

data class AdditionalInGameUserData(
    val madness: MadnessAdditionalInGameUserData,
    val inventory: InventoryAdditionalInGameUserData,
    var callToArms: Int,
    val originalSkin: InGameUserSkinSetting,
)