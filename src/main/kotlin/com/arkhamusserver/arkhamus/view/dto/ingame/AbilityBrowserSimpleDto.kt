package com.arkhamusserver.arkhamus.view.dto.ingame

data class AbilityBrowserSimpleDto(
    var id: Int,
    var name: String,
    var requiresItem: Boolean,
    var roleBased: Boolean,
    var classBased: Boolean,
)