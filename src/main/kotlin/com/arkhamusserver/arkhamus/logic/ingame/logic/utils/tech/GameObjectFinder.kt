package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class GameObjectFinder {

    fun findById(
        id: String,
        type: GameObjectType,
        data: GlobalGameData
    ): WithStringId? {
        return when (type) {
            CHARACTER -> data.users[id.toLong()]
            VOTE_SPOT -> data.voteSpots.firstOrNull { it.inGameId() == id.toLong() }
            CONTAINER -> data.containers[id.toLong()]
            CRAFTER -> data.crafters[id.toLong()]
            ALTAR -> data.altars[id.toLong()]
            QUEST_GIVER -> data.questGivers.firstOrNull { it.inGameId() == id.toLong() }
            LANTERN -> data.lanterns.firstOrNull { it.inGameId() == id.toLong() }
            DOOR -> data.doors.firstOrNull { it.inGameId() == id.toLong() }
            //clues
            SCENT_CLUE -> data.clues.scent.firstOrNull { it.inGameId() == id.toLong() }
            SOUND_CLUE -> data.clues.sound.firstOrNull { it.inGameId() == id.toLong() }
            SOUND_CLUE_JAMMER -> data.clues.sound.map { it.soundClueJammers }.flatten()
                .firstOrNull { it.inGameId() == id.toLong() }

            OMEN_CLUE -> data.clues.omen.firstOrNull {
                it.stringId() == id
            }

            AURA_CLUE -> data.clues.aura.firstOrNull { it.inGameId() == id.toLong() }
            CORRUPTION_CLUE -> data.clues.corruption.firstOrNull { it.inGameId() == id.toLong() }
            DISTORTION_CLUE -> data.clues.distortion.firstOrNull { it.inGameId() == id.toLong() }
            INSCRIPTION_CLUE -> data.clues.inscription.firstOrNull { it.inGameId() == id.toLong() }
            INSCRIPTION_CLUE_GLYPH -> data.clues.inscription.flatMap { it.inscriptionClueGlyphs }.firstOrNull {
                it.inGameId() == id.toLong()
            }
        }
    }

    fun all(
        types: List<GameObjectType>,
        data: GlobalGameData
    ): List<WithStringId> {
        return types.flatMap { type ->
            listByGameObjectType(type, data)
        }
    }

    fun allTyped(
        types: List<GameObjectType>,
        data: GlobalGameData
    ): List<TypedGameObject> {
        return types.flatMap { type ->
            val gameObjects = listByGameObjectType(type, data)
            gameObjects.map { TypedGameObject(type, it) }
        }
    }

    private fun listByGameObjectType(
        type: GameObjectType,
        data: GlobalGameData
    ): Collection<WithStringId> = when (type) {
        CHARACTER -> data.users.values
        VOTE_SPOT -> data.voteSpots
        CONTAINER -> data.containers.values
        CRAFTER -> data.crafters.values
        ALTAR -> data.altars.values
        QUEST_GIVER -> data.questGivers
        LANTERN -> data.lanterns
        DOOR -> data.doors
        //clues
        SCENT_CLUE -> data.clues.scent
        SOUND_CLUE -> data.clues.sound
        SOUND_CLUE_JAMMER -> data.clues.sound.map { it.soundClueJammers }.flatten()
        AURA_CLUE -> data.clues.aura
        OMEN_CLUE -> data.clues.omen
        CORRUPTION_CLUE -> data.clues.corruption
        DISTORTION_CLUE -> data.clues.distortion
        INSCRIPTION_CLUE -> data.clues.inscription
        INSCRIPTION_CLUE_GLYPH -> data.clues.inscription.flatMap { it.inscriptionClueGlyphs }
    }

    data class TypedGameObject(
        val type: GameObjectType,
        val gameObject: WithStringId
    )
}