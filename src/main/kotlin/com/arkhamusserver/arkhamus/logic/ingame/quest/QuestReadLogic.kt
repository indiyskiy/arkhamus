package com.arkhamusserver.arkhamus.logic.ingame.quest

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.view.dto.ingame.QuestDto
import com.arkhamusserver.arkhamus.view.maker.ingame.QuestDtoMaker
import org.springframework.stereotype.Component

@Component
class QuestReadLogic(
    private val questRepository: QuestRepository,
    private val questDtoMaker: QuestDtoMaker
) {
    fun listAllQuests(): List<QuestDto> {
        return questRepository.findByQuestState(QuestState.ACTIVE).map {
            questDtoMaker.convert(it)
        }
    }

    fun listQuests(levelId: Long): List<QuestDto> {
        return questRepository.findByLevelIdAndQuestState(levelId, QuestState.ACTIVE).map {
            questDtoMaker.convert(it)
        }
    }

}