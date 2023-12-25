package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class RecipeDto(
    var item: Item? = null,
    var ingredients: List<IngredientDto>? = emptyList()
)