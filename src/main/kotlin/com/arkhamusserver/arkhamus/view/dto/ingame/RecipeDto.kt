package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType

data class RecipeDto(
    var item: ItemInformationDto? = null,
    var ingredients: List<IngredientDto>? = emptyList(),
    var numberOfItems: Long = 1,
    var timeToCraft: Long = 0L,
    var crafterTypes: List<CrafterType> = emptyList(),
)