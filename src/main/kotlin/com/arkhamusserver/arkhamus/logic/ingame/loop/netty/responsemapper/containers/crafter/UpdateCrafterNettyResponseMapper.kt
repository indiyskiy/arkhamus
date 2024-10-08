package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.ContainerDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.CrafterDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.DoorDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.LanternDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.UpdateCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ItemsInBetweenHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter.UpdateCrafterNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class UpdateCrafterNettyResponseMapper(
    private val itemsInBetweenHandler: ItemsInBetweenHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == UpdateCrafterRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): UpdateCrafterNettyResponse {
        with(requestProcessData as UpdateCrafterRequestGameData) {
            return build(
                sortedUserInventory = sortedUserInventory.applyInBetween(
                    inBetweenEventHolder.inBetweenItemHolderChanges,
                    user.id!!
                ),
                itemsInside = crafter.items.map {
                    InventoryCell().apply {
                        number = it.value
                        itemId = it.key
                    }
                },
                gameData = this,
                user = user,
                gameUser = gameUser!!,
                availableAbilities = availableAbilities,
                ongoingCraftingProcess = ongoingCraftingProcess,
                containers = requestProcessData.containers,
                crafters = requestProcessData.crafters,
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues,
                userQuestProgresses = requestProcessData.userQuest,
                levelGeometryData = globalGameData.levelGeometryData,
                shortTimeEvents = globalGameData.shortTimeEvents,
                globalGameData = globalGameData
            )
        }
    }

    private fun build(
        sortedUserInventory: List<InventoryCell>,
        gameData: UpdateCrafterRequestGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        itemsInside: List<InventoryCell>,
        containers: List<RedisContainer>,
        crafters: List<RedisCrafter>,
        inZones: List<LevelZone>,
        clues: List<RedisClue>,
        userQuestProgresses: List<UserQuestResponse>,
        levelGeometryData: LevelGeometryData,
        shortTimeEvents: List<RedisShortTimeEvent>,
        globalGameData: GlobalGameData
    ) = UpdateCrafterNettyResponse(
        sortedUserInventory = sortedUserInventory,
        itemsInside = itemsInside,
        state = gameData.crafter.state,
        holdingUser = gameData.crafter.holdingUser,
        userInventory = sortedUserInventory,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponse(gameUser, userQuestProgresses),
        otherGameUsers = otherGameUsersDataHandler.map(
            myUser = gameUser,
            otherGameUsers = gameData.otherGameUsers,
            levelGeometryData = levelGeometryData,
        ),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        containers = containersDataHandler.map(
            gameUser,
            containers,
            levelGeometryData
        ),
        crafters = craftersDataHandler.map(
            gameUser,
            crafters,
            levelGeometryData
        ),
        ongoingCraftingProcess = ongoingCraftingProcess,
        availableAbilities = availableAbilities,
        executedSuccessfully = true,
        firstTime = true,
        inZones = inZones,
        clues = clues,
        shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
            shortTimeEvents,
            gameUser,
            inZones,
            globalGameData
        ),
        doors = doorDataHandler.map(
            gameUser,
            globalGameData.doorsByZoneId.values.flatten(),
            globalGameData.levelGeometryData
        ),
        lanterns = lanternDataHandler.map(
            gameUser,
            globalGameData.lanterns,
            globalGameData.levelGeometryData
        ),
    )

    private fun List<InventoryCell>.applyInBetween(
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        return itemsInBetweenHandler.applyInBetween(this, inBetweenItemHolderChanges, userId)
    }
}


