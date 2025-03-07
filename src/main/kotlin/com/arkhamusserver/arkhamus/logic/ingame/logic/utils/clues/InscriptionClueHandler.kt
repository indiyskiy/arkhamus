package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameInscriptionClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.InscriptionClueGlyphRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.InscriptionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameInscriptionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameInscriptionClueGlyph
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.InscriptionClueAdditionalDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.PossiblerGlyphResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.RightGlyphResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class InscriptionClueHandler(
    private val inscriptionClueRepository: InscriptionClueRepository,
    private val inscriptionClueGlyphRepository: InscriptionClueGlyphRepository,
    private val inGameInscriptionClueRepository: InGameInscriptionClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
) : AdvancedClueHandler {

    companion object {
        const val GLYPH_VARIANTS = 20
        const val MAX_ON_GAME = 7
        private val random: Random = Random(System.currentTimeMillis())
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.INSCRIPTION)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.INSCRIPTION
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameInscriptionClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.inscription.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.inscription.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameInscriptionClueRepository.save(this)
        }
    }

    override fun canBeRemovedRabdomly(container: CluesContainer): Boolean {
        return container.inscription.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val inscription = target as InGameInscriptionClue
        return inscription.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = inscription,
            levelGeometryData = data.levelGeometryData,
            range = Ability.CLEAN_UP_CLUE.range!!.toDouble(),
            affectedByBlind = false,
            heightAffectVision = false,
            geometryAffectsVision = true,
        )
    }

    override fun anyCanBeRemoved(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.inscription.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val inscriptionClue = container.inscription.filter { it.turnedOn }.randomOrNull()
        inscriptionClue?.let {
            it.turnedOn = false
            it.castedAbilityUsers = emptySet()
            inGameInscriptionClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val inscriptionClue = data.clues.inscription.find { it.inGameId() == target.stringId().toLong() } ?: return
        inscriptionClue.turnedOn = false
        inscriptionClue.castedAbilityUsers = emptySet()
        inGameInscriptionClueRepository.save(inscriptionClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val inscriptionClues = inscriptionClueRepository.findByLevelId(
            session.gameSessionSettings.level!!.id!!
        )
        val inscriptionClueGlyphs = inscriptionClueGlyphRepository.findByLevelId(
            session.gameSessionSettings.level!!.id!!
        )
        val inscriptionCluesForGameSession = inscriptionClues.shuffled(random).take(MAX_ON_GAME)
        val inGameInscriptionClues = inscriptionCluesForGameSession.map {
            val clue = InGameInscriptionClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameInscriptionId = it.inGameId,
                x = it.x,
                y = it.y,
                z = it.z,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_INSCRIPTION,
                ),
                turnedOn = false,
                inscriptionClueGlyphs = inscriptionClueGlyphs.filter { glyph ->
                    it.inGameId == glyph.relatedClue!!.inGameId
                }.map { glyph ->
                    InGameInscriptionClueGlyph(
                        id = generateRandomId(),
                        gameId = session.id!!,
                        x = glyph.x(),
                        y = glyph.y(),
                        z = glyph.z(),
                        inGameId = glyph.inGameId,
                        visibilityModifiers = setOf(VisibilityModifier.HAVE_ITEM_INSCRIPTION),
                        inscriptionClueId = it.inGameId,
                        interactionRadius = glyph.interactionRadius,
                        value = 0
                    )
                },
            )
            shuffleGlyphValues(clue)
            clue.value = clue.inscriptionClueGlyphs.random(random).value
            clue
        }
        if (god.getTypes().contains(Clue.INSCRIPTION)) {
            val turnedOn = inGameInscriptionClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameInscriptionClueRepository.saveAll(inGameInscriptionClues)
    }

    private fun shuffleGlyphValues(clue: InGameInscriptionClue) {
        val values = (1..GLYPH_VARIANTS).shuffled(random)
        clue.inscriptionClueGlyphs.forEachIndexed { index, glyph ->
            glyph.value = values[index]
        }
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.inscription.filter {
            it.turnedOn == true
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.INSCRIPTION)
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
        }.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.INSCRIPTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.INSCRIPTION_CLUE,
                x = null,
                y = null,
                z = null,
                additionalData = fillActualAdditionalData(it, user),
                state = ClueState.ACTIVE_CLUE
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val inscriptionOptions = container.inscription
        return mapActualInscriptionInvestigators(inscriptionOptions, user, data) +
                mapWitnessesOfInvestigators(inscriptionOptions, user, data)
    }

    private fun mapWitnessesOfInvestigators(
        inscriptionOptions: List<InGameInscriptionClue>,
        user: InGameUser,
        data: GlobalGameData
    ): List<ExtendedClueResponse> {
        val allUserNotMe = data.users.values.filter { it.inGameId() != user.inGameId() }
        val usersCurrentUserSee = allUserNotMe.filter { investigator ->
            userLocationHandler.userCanSeeTarget(user, investigator, data.levelGeometryData, true)

        }
        val usersCanSeeInscriptions = usersCurrentUserSee.filter { investigator ->
            inscriptionOptions.any { clue ->
                userCanSeeClue(investigator, clue, data.levelGeometryData)
            }
        }
        val inscriptionsInvestigatorsCanSee = inscriptionOptions.filter { clue ->
            usersCanSeeInscriptions.any { investigator ->
                userCanSeeClue(investigator, clue, data.levelGeometryData)
            }
        }
        return inscriptionsInvestigatorsCanSee.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.INSCRIPTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.INSCRIPTION_CLUE,
                x = null,
                y = null,
                z = null,
                additionalData = fillWitnessesAdditionalData(
                    it,
                    data.levelGeometryData,
                    usersCanSeeInscriptions
                ),
                state = ClueState.ACTIVE_UNKNOWN,
            )
        }
    }

    private fun mapActualInscriptionInvestigators(
        inscriptionOptions: List<InGameInscriptionClue>,
        user: InGameUser,
        data: GlobalGameData
    ): List<ExtendedClueResponse> {
        val filteredByVisibilityTags = inscriptionOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.INSCRIPTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.INSCRIPTION_CLUE,
                x = null,
                y = null,
                z = null,
                additionalData = fillInvestigatorsAdditionalData(it, user, data.levelGeometryData),
                state = countState(it, user),
            )
        }
    }

    private fun countState(
        clue: InGameInscriptionClue,
        user: InGameUser,
    ): ClueState {
        return if (user.inGameId() in clue.castedAbilityUsers) {
            if (clue.turnedOn) {
                ClueState.ACTIVE_CLUE
            } else {
                ClueState.ACTIVE_NO_CLUE
            }
        } else ClueState.ACTIVE_UNKNOWN
    }

    private fun fillWitnessesAdditionalData(
        clue: InGameInscriptionClue,
        data: LevelGeometryData,
        usersCanSeeInscriptions: List<InGameUser>
    ): InscriptionClueAdditionalDataResponse {
        return InscriptionClueAdditionalDataResponse().apply {
            this.possiblyGlyphs = emptyList()
            this.rightGlyph = RightGlyphResponse(
                value = clue.value,
                playerIds = usersCanSeeInscriptions.filter { investigator ->
                    userCanSeeClue(investigator, clue, data)
                }.map { it.inGameId() }
            )
        }
    }

    private fun userCanSeeClue(
        investigator: InGameUser,
        clue: InGameInscriptionClue,
        data: LevelGeometryData
    ): Boolean = visibilityByTagsHandler.userCanSeeTarget(investigator, clue) &&
            userLocationHandler.userCanSeeTarget(
                investigator,
                clue,
                data,
                true
            )

    private fun fillInvestigatorsAdditionalData(
        clue: InGameInscriptionClue,
        user: InGameUser,
        data: LevelGeometryData
    ): InscriptionClueAdditionalDataResponse? {
        if (userCanSeeClue(user, clue, data)) {
            return InscriptionClueAdditionalDataResponse().apply {
                this.possiblyGlyphs = clue.inscriptionClueGlyphs.filter {
                    visibilityByTagsHandler.userCanSeeTarget(user, it)
                }.map {
                    PossiblerGlyphResponse(
                        glyphId = it.inGameId(),
                        value = it.value
                    )
                }
                this.rightGlyph = null
            }
        } else {
            return null
        }
    }

    private fun fillActualAdditionalData(
        clue: InGameInscriptionClue,
        user: InGameUser,
    ): InscriptionClueAdditionalDataResponse {
        return InscriptionClueAdditionalDataResponse().apply {
            this.possiblyGlyphs = clue.inscriptionClueGlyphs.filter {
                visibilityByTagsHandler.userCanSeeTarget(user, it)
            }.map {
                PossiblerGlyphResponse(
                    glyphId = it.inGameId(),
                    value = it.value
                )
            }
        }
    }

}