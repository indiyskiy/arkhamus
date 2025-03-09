package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.InscriptionClueGlyphRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.InscriptionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.InscriptionClue
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.InscriptionClueGlyph
import com.arkhamusserver.arkhamus.view.levelDesign.InscriptionClueFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.InscriptionGlyphFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignInscriptionClueInfoProcessor(
    private val inscriptionClueRepository: InscriptionClueRepository,
    private val inscriptionClueGlyphRepository: InscriptionClueGlyphRepository,
) {

    fun processInscriptionClueInfos(
        inscriptionCluesJson: List<InscriptionClueFromJson>,
        inscriptionClueGlyphsJson: List<InscriptionGlyphFromJson>,
        savedLevel: Level,
    ) {
        val inscriptionClues = inscriptionCluesJson.map { inscriptionClueJson ->
            InscriptionClue(
                x = inscriptionClueJson.x!!,
                y = inscriptionClueJson.y!!,
                z = inscriptionClueJson.z!!,
                effectRadius = inscriptionClueJson.effectRadius!!,
                interactionRadius = inscriptionClueJson.interactionRadius!!,
                inGameId = inscriptionClueJson.id!!,
                level = savedLevel,
            ).let {
                inscriptionClueRepository.save(it)
            }
        }
        inscriptionClues.forEach { inscriptionClue ->
            val inscriptionClueJsonGlyphs = inscriptionClueGlyphsJson.filter {
                it.relatedClue == inscriptionClue.inGameId
            }
            inscriptionClueJsonGlyphs.forEach { inscriptionClueJsonGlyph ->
                InscriptionClueGlyph(
                    relatedClue = inscriptionClue,
                    x = inscriptionClueJsonGlyph.x!!,
                    y = inscriptionClueJsonGlyph.y!!,
                    z = inscriptionClueJsonGlyph.z!!,
                    inGameId = inscriptionClueJsonGlyph.id!!,
                    level = savedLevel,
                ).apply {
                    inscriptionClueGlyphRepository.save(this)
                }
            }
        }
    }
}