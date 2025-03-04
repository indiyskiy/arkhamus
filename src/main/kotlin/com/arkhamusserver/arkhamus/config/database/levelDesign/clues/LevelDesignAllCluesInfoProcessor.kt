package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignAllCluesInfoProcessor(
    private val levelDesignScentClueInfoProcessor: LevelDesignScentClueInfoProcessor,
    private val levelDesignInscriptionClueInfoProcessor: LevelDesignInscriptionClueInfoProcessor,
    private val levelDesignSoundClueInfoProcessor: LevelDesignSoundClueInfoProcessor,
    private val levelDesignCorruptionClueInfoProcessor: LevelDesignCorruptionClueInfoProcessor,
    private val levelDesignDistortionClueInfoProcessor: LevelDesignDistortionClueInfoProcessor,
    private val levelDesignAuraClueInfoProcessor: LevelDesignAuraClueInfoProcessor,
) {
    fun processClues(
        levelFromJson: LevelFromJson,
        savedLevel: Level,
        zones: List<LevelZone>
    ) {
        levelDesignInscriptionClueInfoProcessor.processInscriptionClueInfos(
            levelFromJson.inscriptionClues,
            levelFromJson.inscriptionGlyphs,
            savedLevel
        )
        levelDesignScentClueInfoProcessor.processScentClueInfos(
            levelFromJson.scentClues,
            savedLevel
        )
        levelDesignSoundClueInfoProcessor.processSoundInfos(
            levelFromJson.soundClues,
            levelFromJson.soundClueJammers,
            savedLevel,
            zones
        )
        levelDesignCorruptionClueInfoProcessor.processCorruptionClueInfos(
            levelFromJson.corruptionClues,
            savedLevel
        )
        levelDesignDistortionClueInfoProcessor.processDistortionClueInfos(
            levelFromJson.distortionClues,
            savedLevel
        )
        levelDesignAuraClueInfoProcessor.processAuraClueInfos(
            levelFromJson.auraClues,
            savedLevel,
            zones
        )
    }
}