package com.arkhamusserver.arkhamus.logic.dto.ingame

data class GodDto(
    var name: String? = null,
    var title: String? = null,
    var types: List<String>? = emptyList()
)