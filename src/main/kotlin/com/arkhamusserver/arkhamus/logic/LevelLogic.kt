package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import com.arkhamusserver.arkhamus.view.maker.LevelDtoMaker
import org.springframework.stereotype.Component

@Component
class LevelLogic(
    val levelRepository: LevelRepository,
    val levelDtoMaker: LevelDtoMaker
) {
    fun all(): List<LevelDto> {
        return levelRepository.findAll().map {
            levelDtoMaker.mapLevelToDto(it)
        }
    }

}