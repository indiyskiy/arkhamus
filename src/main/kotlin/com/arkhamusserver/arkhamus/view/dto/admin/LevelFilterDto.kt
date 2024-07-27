package com.arkhamusserver.arkhamus.view.dto.admin

data class LevelFilterDto(
    var zones: Boolean? = false,
    var containers: Boolean? = false,
    var altars: Boolean? = false,
    var lanterns: Boolean? = false,
    var questGivers: Boolean? = false,
    var levelTasks: Boolean? = false,
    var levelId: Long? = null
) {
    companion object {
        fun allTrue(levelId: Long): LevelFilterDto {
            return LevelFilterDto(
                true, true, true, true, true, true, levelId
            )
        }
    }
}