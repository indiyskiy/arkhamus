package com.arkhamusserver.arkhamus.logic.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class RecipeDto(
    var item: Item? = null,
    var ingredients: List<IngredientDto>? = emptyList()
)