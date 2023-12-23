package com.arkhamusserver.arkhamus.logic.dto.ingame

data class RecipeDto(
    var item: String? = null,
    var ingredients: List<IngredientDto>? = emptyList()
)