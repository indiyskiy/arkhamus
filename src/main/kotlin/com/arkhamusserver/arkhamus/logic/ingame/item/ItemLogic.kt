package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.view.dto.ingame.RecipeDto
import com.arkhamusserver.arkhamus.view.maker.ingame.ItemInformationDtoMaker
import com.arkhamusserver.arkhamus.view.maker.ingame.RecipeToRecipeDtoMaker
import org.springframework.stereotype.Component

@Component
class ItemLogic(
    private val itemsDtoMaker: ItemInformationDtoMaker,
    private val recipeDtoMaker: RecipeToRecipeDtoMaker,
    private val recipesSource: RecipesSource
) {
    fun listAllItems(): List<ItemInformationDto> =
        itemsDtoMaker.convert(Item.values().toList())

    fun listAllRecipes(): List<RecipeDto> {
        return recipesSource.getAllRecipes()
            .filter {
                it.ingredients.isNotEmpty()
            }.let { recipeDtoMaker.convert(it) }
    }
}