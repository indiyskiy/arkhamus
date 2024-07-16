package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import org.springframework.stereotype.Component

@Component
class LevelDtoMaker {
    fun mapLevelToDto(it: Level) =
        LevelDto().apply {
            levelId = it.levelId
            version = it.version
            name = "tbd"
            state = it.state.name
        }
}