package com.arkhamusserver.arkhamus.model.enums.ingame

enum class Ability(
    private val id: Int,
    private val requiresItem: Boolean = false,
    private val consumesItem: Boolean = false
) {

    SUMMON_NIGHT(
        id = 201,
        requiresItem = true,
        consumesItem = true
    );

    fun isRequiresItem() = requiresItem
    fun isConsumesItem() = consumesItem

    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}