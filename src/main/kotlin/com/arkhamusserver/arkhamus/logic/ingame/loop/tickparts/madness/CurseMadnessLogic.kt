package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithGameTags
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CurseMadnessLogic(
    private val inGameContainerRepository: InGameContainerRepository,
    private val inGameCrafterRepository: InGameCrafterRepository,
    private val userLocationHandler: UserLocationHandler,
) {
    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun curseSomething(user: InGameGameUser, data: GlobalGameData, timePassedMillis: Long): Boolean {
        val validCrafters = nonCursedOpenCrafters(data, user)
        val validContainers = nonCursedOpenContainer(data, user)
        val allObjects: List<WithGameTags> = validCrafters + validContainers
        if (allObjects.isNotEmpty()) {
            curseRandomItem(allObjects)
            return true
        }
        return false
    }

    private fun curseRandomItem(allObjects: List<WithGameTags>) {
        val randomObject = allObjects.random(random)
        randomObject.writeGameTags(randomObject.gameTags().plus(InGameObjectTag.PEEKABOO_CURSE))
        if (randomObject is InGameCrafter) {
            inGameCrafterRepository.save(randomObject)
        }
        if (randomObject is InGameContainer) {
            inGameContainerRepository.save(randomObject)
        }

    }

    private fun nonCursedOpenContainer(
        data: GlobalGameData,
        user: InGameGameUser
    ): List<InGameContainer> = data.containers.values.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) &&
                it.state == MapObjectState.ACTIVE &&
                !it.gameTags.contains(InGameObjectTag.PEEKABOO_CURSE)
                && userLocationHandler.userInInteractionRadius(user, it)
    }.toList()

    private fun nonCursedOpenCrafters(
        data: GlobalGameData,
        user: InGameGameUser
    ): List<InGameCrafter> = data.crafters.values.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) &&
                it.state == MapObjectState.ACTIVE &&
                !it.gameTags.contains(InGameObjectTag.PEEKABOO_CURSE)
                && userLocationHandler.userInInteractionRadius(user, it)
    }.toList()
}