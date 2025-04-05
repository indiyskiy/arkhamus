package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.InGameTagsHandler
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestGiverRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class DispellHandler(
    private val inGameTagsHandler: InGameTagsHandler,
    private val inGameContainerRepository: InGameContainerRepository,
    private val inGameCrafterRepository: InGameCrafterRepository,
    private val questGiverRepository: InGameQuestGiverRepository,
) {
    fun dispellItem(target: WithStringId?) {
        if (target != null) {
            if (target is InGameContainer) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.PEEKABOO_CURSE)
                inGameContainerRepository.save(target)
            }
            if (target is InGameCrafter) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.PEEKABOO_CURSE)
                inGameCrafterRepository.save(target)
            }
        }
    }

    fun dispellPlayerOrNpc(target: WithStringId?) {
        if (target != null) {
            if (target is InGameQuestGiver) {
                inGameTagsHandler.removeTag(target, InGameObjectTag.DARK_THOUGHTS)
                questGiverRepository.save(target)
            }
            if (target is InGameUser) {
                target.stateTags -= UserStateTag.MADNESS_LINK_TARGET
                target.stateTags -= UserStateTag.MADNESS_LINK_SOURCE
            }
        }
    }


}