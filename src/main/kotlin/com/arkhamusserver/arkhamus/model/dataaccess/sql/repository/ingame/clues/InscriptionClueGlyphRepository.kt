package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.InscriptionClueGlyph
import org.springframework.data.repository.CrudRepository

interface InscriptionClueGlyphRepository : CrudRepository<InscriptionClueGlyph, Long> {
    fun findByLevelId(levelId: Long): List<InscriptionClueGlyph>
}