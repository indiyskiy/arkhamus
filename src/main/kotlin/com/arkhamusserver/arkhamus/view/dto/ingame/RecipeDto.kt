package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType

data class RecipeDto(
    var recipeId: Int,
    var item: ItemInformationDto,
    var ingredients: List<IngredientDto> = emptyList(),
    var numberOfItems: Int = 1,
    var timeToCraft: Long = 0L,
    var crafterTypes: List<CrafterType> = emptyList(),
)