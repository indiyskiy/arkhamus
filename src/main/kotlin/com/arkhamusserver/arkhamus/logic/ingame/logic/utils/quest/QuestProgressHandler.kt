package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.TakeQuestRewardRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState.*
import com.arkhamusserver.arkhamus.model.ingame.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestGiverResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestStepResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class QuestProgressHandler(
    private val questProgressRepository: InGameUserQuestProgressRepository,
    private val userQuestCreationHandler: UserQuestCreationHandler,
    private val activityHandler: ActivityHandler,
    private val userLocationHandler: UserLocationHandler
) {

    companion object {
        val notTakenStates = setOf(AWAITING)
        var logger: Logger = LoggerFactory.getLogger(QuestProgressHandler::class.java)
    }

    @Transactional
    fun acceptTheQuest(
        game: InRamGame,
        userQuestProgress: InGameUserQuestProgress?,
        data: QuestAcceptRequestProcessData,
        currentGameTime: Long
    ) {
        userQuestProgress?.let {
            it.questState = IN_PROGRESS
            it.questCurrentStep = 0
            it.acceptanceGameTime = currentGameTime
            questProgressRepository.save(it)

            data.canAccept = false
            data.canDecline = true

            activityHandler.addUserWithTargetActivity(
                gameId = game.inGameId(),
                activityType = ActivityType.QUEST_ACCEPTED,
                sourceUser = data.gameUser!!,
                gameTime = game.globalTimer,
                relatedGameObjectType = GameObjectType.QUEST_GIVER,
                withTrueIngameId = data.questGiver,
                relatedEventId = it.questId
            )

        }
    }

    @Transactional
    fun finishQuest(
        user: InGameUser,
        globalGameData: GlobalGameData,
        data: TakeQuestRewardRequestProcessData
    ) {
        data.userQuestProgress?.let {
            it.questState = FINISHED
            it.finishGameTime = globalGameData.game.globalTimer
            questProgressRepository.save(it)

            data.canAccept = false
            data.canDecline = false
            data.canFinish = false

            activityHandler.addUserWithTargetActivity(
                gameId = globalGameData.game.inGameId(),
                activityType = ActivityType.QUEST_COMPLETE,
                sourceUser = data.gameUser!!,
                gameTime = globalGameData.game.globalTimer,
                relatedGameObjectType = GameObjectType.QUEST_GIVER,
                withTrueIngameId = data.questGiver,
                relatedEventId = it.questId
            )
        }


        addMoreQuestsMaybe(user, globalGameData, data, globalGameData.game.globalTimer)
    }

    @Transactional
    fun declineTheQuest(
        user: InGameUser,
        globalGameData: GlobalGameData,
        data: QuestDeclineRequestProcessData
    ) {
        data.userQuestProgress?.let {
            it.questState = DECLINED
            it.finishGameTime = globalGameData.game.globalTimer
            questProgressRepository.save(it)
            activityHandler.addUserWithTargetActivity(
                gameId = globalGameData.game.inGameId(),
                activityType = ActivityType.QUEST_DECLINED,
                sourceUser = data.gameUser!!,
                gameTime = globalGameData.game.globalTimer,
                relatedGameObjectType = GameObjectType.QUEST_GIVER,
                withTrueIngameId = data.questGiver,
                relatedEventId = it.questId
            )

            data.canAccept = false
            data.canDecline = false
            data.canFinish = false
        }

        addMoreQuestsMaybe(user, globalGameData, data, globalGameData.game.globalTimer)
    }

    private fun addMoreQuestsMaybe(
        user: InGameUser,
        globalGameData: GlobalGameData,
        data: GameUserData,
        currentGameTime: Long
    ) {
        logger.info("add more quests maybe?")
        val userQuestProgress = globalGameData.questProgressByUserId[data.gameUser!!.inGameId()] ?: emptyList()
        if (userQuestCreationHandler.needToAddQuests(userQuestProgress)) {
            logger.info("add more quests definitely")
            val newQuestProgress = userQuestCreationHandler.addQuests(
                data,
                globalGameData.quests,
                userQuestProgress,
                currentGameTime
            )
            logger.info("new quest progress ${newQuestProgress.size}")
            data.userQuest += newQuestProgress.map {
                mapQuestProgress(user, globalGameData, globalGameData.quests, it)
            }
        }
    }

    fun mapQuestProgresses(
        data: GlobalGameData,
        questProgressByUserId: Map<Long, List<InGameUserQuestProgress>>,
        user: InGameUser,
        quests: List<InGameQuest>
    ): List<UserQuestResponse> {
        return (questProgressByUserId[user.inGameId()] ?: emptyList()).map { userQuest ->
            mapQuestProgress(user, data, quests, userQuest)
        }
    }

    fun mapQuestProgress(
        user: InGameUser,
        data: GlobalGameData,
        quests: List<InGameQuest>,
        userQuest: InGameUserQuestProgress
    ): UserQuestResponse {
        val quest = quests.firstOrNull { it.inGameId() == userQuest.questId }
        return mapQuestProgress(
            user,
            data,
            quest,
            userQuest
        )
    }

    fun mapQuestProgress(
        user: InGameUser,
        data: GlobalGameData,
        quest: InGameQuest?,
        userQuest: InGameUserQuestProgress,
    ): UserQuestResponse {
        return if (quest != null && questTaken(userQuest)) {
            UserQuestResponse(
                id = userQuest.id,
                questId = userQuest.questId,
                questState = userQuest.questState,
                questCurrentStep = userQuest.questCurrentStep,
                questSteps = quest.levelTasks.mapQuests(user, data),
                endQuestGiver = quest.endQuestGiverId.mapNpc(user, data),
                startQuestGiver = quest.startQuestGiverId.mapNpc(user, data),
                textKey = quest.textKey,
                creationGameTime = userQuest.creationGameTime,
                readGameTime = userQuest.readGameTime,
                acceptanceGameTime = userQuest.acceptanceGameTime,
                finishGameTime = userQuest.acceptanceGameTime,
            )
        } else {
            UserQuestResponse(
                id = userQuest.id,
                questId = null,
                questState = userQuest.questState,
                questCurrentStep = userQuest.questCurrentStep,
                questSteps = emptyList(),
                endQuestGiver = null,
                startQuestGiver = quest?.startQuestGiverId?.mapNpc(user, data),
                textKey = null,
                creationGameTime = userQuest.creationGameTime,
                readGameTime = userQuest.readGameTime,
                acceptanceGameTime = userQuest.acceptanceGameTime,
                finishGameTime = userQuest.acceptanceGameTime,
            )
        }
    }

    fun canAccept(quest: InGameQuest?, userQuestProgress: InGameUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            AWAITING,
            READ
        )

    fun canDecline(quest: InGameQuest?, userQuestProgress: InGameUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            AWAITING,
            READ,
            IN_PROGRESS
        )

    fun isCompleted(quest: InGameQuest?, userQuestProgress: InGameUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(IN_PROGRESS, COMPLETED) &&
                userQuestProgress.questCurrentStep == quest.levelTasks.size

    fun canFinish(quest: InGameQuest?, userQuestProgress: InGameUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(COMPLETED) &&
                userQuestProgress.questCurrentStep == quest.levelTasks.size

    @Transactional
    fun readTheQuest(
        userQuestProgress: InGameUserQuestProgress?,
        currentGameTime: Long
    ) {
        userQuestProgress?.let {
            it.questState = READ
            it.readGameTime = currentGameTime
            questProgressRepository.save(it)
        }
    }

    fun questAndProgress(
        levelTaskId: Long,
        globalGameData: GlobalGameData,
        userId: Long?
    ): Pair<InGameQuest?, InGameUserQuestProgress?> {

        val questSteps =
            globalGameData.questProgressByUserId[userId]?.filter { it.questState == IN_PROGRESS }
        val quests = globalGameData.quests
        val questIdToStep = questSteps?.map { it.questId to it.questCurrentStep }
        val questToStep = questIdToStep
            ?.map { quests.first { quest -> quest.inGameId() == it.first } to it.second }
            ?.map { it.first to task(it.second, it.first.levelTasks) }
        val quest = questToStep?.firstOrNull { it.second?.inGameId() == levelTaskId }?.first
        val userQuestProgress = quest?.let {
            questSteps.firstOrNull { questStep -> it.inGameId() == questStep.questId }
        }

        return Pair(quest, userQuestProgress)
    }

    @Transactional
    fun nextStep(
        userQuestProgress: InGameUserQuestProgress?,
        quest: InGameQuest?,
        globalGameData: GlobalGameData,
        currentUser: InGameUser?,
    ) {
        userQuestProgress?.let { progress ->
            quest?.let { questNotNull ->
                currentUser?.let { currentUserNotNull ->
                    progress.questCurrentStep = min(progress.questCurrentStep + 1, questNotNull.levelTasks.size)
                    if (isCompleted(quest, userQuestProgress)) {
                        complete(userQuestProgress)
                    }
                    questProgressRepository.save(progress)

                    val game = globalGameData.game
                    activityHandler.addUserWithTargetActivity(
                        gameId = game.inGameId(),
                        activityType = ActivityType.QUEST_ACCEPTED,
                        sourceUser = currentUserNotNull,
                        gameTime = game.globalTimer,
                        relatedGameObjectType = null,//GameObjectType.QUEST_GIVER,
                        withTrueIngameId = null, //todo add inGame quest steps
                        relatedEventId = questNotNull.inGameId()
                    )
                }
            }
        }
    }

    private fun task(stepNumber: Int, levelTasks: List<InGameTask>): InGameTask? {
        if (stepNumber < 0) {
            return null
        }
        if (stepNumber >= levelTasks.size) {
            return null
        }
        return levelTasks[stepNumber]
    }

    private fun questTaken(userQuest: InGameUserQuestProgress): Boolean {
        return userQuest.questState !in notTakenStates
    }

    private fun complete(
        userQuestProgress: InGameUserQuestProgress?
    ) {
        userQuestProgress?.questState = COMPLETED
    }

    private fun List<InGameTask>.mapQuests(
        user: InGameUser,
        data: GlobalGameData
    ): List<QuestStepResponse> {
        return this.map {
            QuestStepResponse(
                id = it.inGameId(),
                state = if (userLocationHandler.userCanSeeTargetInRange(
                        user,
                        it,
                        data.levelGeometryData,
                        it.interactionRadius,
                        true
                    )
                ) {
                    MapObjectState.ACTIVE
                } else {
                    MapObjectState.NOT_IN_SIGHT
                }

            )
        }
    }

    private fun Long.mapNpc(
        user: InGameUser,
        data: GlobalGameData
    ): QuestGiverResponse? {
        val npcId: Long = this
        val npc = data.questGivers.firstOrNull { it.inGameId() == npcId } ?: return null
        val state = if (userLocationHandler.userCanSeeTargetInRange(
                user,
                npc,
                data.levelGeometryData,
                npc.interactionRadius,
                true
            )
        ) {
            MapObjectState.ACTIVE
        } else {
            MapObjectState.NOT_IN_SIGHT
        }
        return QuestGiverResponse(
            id = npc.inGameId(),
            state = state
        )
    }
}




