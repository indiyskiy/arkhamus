package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class IngredientDto(
    var item: Item? = null,
    var number: Int? = null
)

