package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.UsersConfig
import com.arkhamusserver.arkhamus.config.database.levelDesign.clues.LevelDesignAllCluesInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignZonesInfoProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileReader

@Component
class LevelDesignInfoProcessor(
    private val levelRepository: LevelRepository,
    private val levelDesignZonesInfoProcessor: LevelDesignZonesInfoProcessor,
    private val levelDesignGeometryProcessor: LevelDesignGeometryProcessor,
    private val levelDesignQuestsRelatedStuffProcessor: LevelDesignQuestsRelatedStuffProcessor,
    private val levelDesignAllCluesInfoProcessor: LevelDesignAllCluesInfoProcessor,
    private val levelDesignRitualRelatedStuffProcessor: LevelDesignRitualRelatedStuffProcessor,
    private val levelDesignOtherGameObjectsProcessor: LevelDesignOtherGameObjectsProcessor
) {
    companion object {
        private const val JSON_PATH = "ingame/level/level_data.json"
        private val logger = LoggingUtils.getLogger<LevelDesignInfoProcessor>()
        private val gson: Gson = Gson()
    }

    @PostConstruct
    @Transactional
    fun updateLevels() {
        try {
            val levelsFromJson: List<LevelFromJson> = readLevelsFromFile()
            val levelsFromDb = levelRepository.findByState(LevelState.ACTIVE)
            processLevels(levelsFromJson, levelsFromDb)
        } catch (e: IllegalStateException) {
            logger.error("failed to load level info", e)
        }
    }

    private fun readLevelsFromFile(): List<LevelFromJson> {
        val path = javaClass.getClassLoader().getResource(JSON_PATH)
        val uri = path?.toURI() ?: throw IllegalStateException("no level file!")
        val reader = JsonReader(FileReader(File(uri)))
        val listType = object : TypeToken<ArrayList<LevelFromJson>>() {}.type
        val levels: List<LevelFromJson> = gson.fromJson(reader, listType)
        logger.info("read ${levels.size} levels")
        return levels
    }

    private fun processLevels(levelsFromJson: List<LevelFromJson>, levelsFromDb: List<Level>) {
        levelsFromJson.forEach {
            val sameLevel = findSameLevel(it, levelsFromDb)
            if (sameLevel == null) {
                createAndSaveLevel(it)
                deprecateOldVersions(levelsFromDb, it.levelVersion, it.levelId)
            }
        }
    }

    private fun deprecateOldVersions(levelsFromDb: List<Level>, levelVersion: Long?, levelId: Long?) {
        levelVersion?.let { version ->
            levelId?.let { id ->
                levelsFromDb
                    .filter {
                        (it.levelId == id) &&
                                (version > (it.version)) &&
                                (it.state != LevelState.INACTIVE)
                    }
                    .forEach {
                        logger.info("deprecate old versioned level $id v.$version")
                        it.state = LevelState.OLD
                        levelRepository.save(it)
                    }
            }
        }
    }

    private fun createAndSaveLevel(levelFromJson: LevelFromJson) {
        logger.info("create level ${levelFromJson.levelId} v.${levelFromJson.levelVersion}")
        val savedLevel = createLevel(levelFromJson)
        val zones = levelDesignZonesInfoProcessor.processZones(
            banZones = levelFromJson.banZones,
            soundZones = levelFromJson.soundClueZones,
            auraZones = levelFromJson.auraClueZones,
            level = savedLevel
        )
        levelDesignGeometryProcessor.processGeometry(levelFromJson, savedLevel)
        levelDesignRitualRelatedStuffProcessor.processRitualRelatedStuff(levelFromJson, savedLevel)
        levelDesignQuestsRelatedStuffProcessor.generateQuestRelatedStuff(levelFromJson, savedLevel)
        levelDesignAllCluesInfoProcessor.processClues(levelFromJson, savedLevel, zones)
        levelDesignOtherGameObjectsProcessor.processAllSortOfMapObjects(levelFromJson, savedLevel)
    }

    private fun createLevel(levelFromJson: LevelFromJson): Level = Level(
        levelId = levelFromJson.levelId!!,
        version = levelFromJson.levelVersion!!,
        levelWidth = levelFromJson.levelWidth!!,
        levelHeight = levelFromJson.levelHeight!!,
        state = LevelState.ACTIVE
    ).let {  levelRepository.save(it) }

    private fun findSameLevel(levelFromJson: LevelFromJson, levelsFromDb: List<Level>): Level? =
        levelsFromDb.firstOrNull {
            it.levelId == levelFromJson.levelId && it.version == levelFromJson.levelVersion
        }
}