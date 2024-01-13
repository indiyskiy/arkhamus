package com.arkhamusserver.arkhamus.view.dto.ingame

data class RecipeDto(
    var item: ItemInformationDto? = null,
    var ingredients: List<IngredientDto>? = emptyList()
)