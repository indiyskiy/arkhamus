package com.arkhamusserver.arkhamus.view.dto.ingame

data class ItemToAbilityDto (
    val item: ItemInformationDto,
    val ability: AbilityDto
)

data class SimpleItemToAbilityDto (
    val itemId: Int,
    val abilityId: Int
)