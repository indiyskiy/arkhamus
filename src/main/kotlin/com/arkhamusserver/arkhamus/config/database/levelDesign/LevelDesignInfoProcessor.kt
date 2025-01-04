package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.clues.LevelDesignScentClueInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.clues.LevelDesignSoundClueInfoProcessor
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.CREATE_TEST_QUESTS
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
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
    private val levelDesignContainerInfoProcessor: LevelDesignContainerInfoProcessor,
    private val levelDesignLanternInfoProcessor: LevelDesignLanternInfoProcessor,
    private val levelDesignAltarInfoProcessor: LevelDesignAltarInfoProcessor,
    private val levelDesignRitualAreaInfoProcessor: LevelDesignRitualAreaInfoProcessor,
    private val levelDesignCrafterInfoProcessor: LevelDesignCrafterInfoProcessor,
    private val levelDesignProcessStartInfoProcessor: LevelDesignProcessStartInfoProcessor,
    private val levelDesignZonesInfoProcessor: LevelDesignZonesInfoProcessor,
    private val levelDesignQuestGiverInfoProcessor: LevelDesignQuestGiverInfoProcessor,
    private val levelDesignLevelTaskInfoProcessor: LevelDesignLevelTaskInfoProcessor,
    private val levelDesignVoteSpotInfoProcessor: LevelDesignVoteSpotInfoProcessor,
    private val levelDesignThresholdInfoProcessor: LevelDesignThresholdInfoProcessor,
    private val levelDesignDoorInfoProcessor: LevelDesignDoorInfoProcessor,
    private val levelDesignScentClueInfoProcessor: LevelDesignScentClueInfoProcessor,
    private val levelDesignSoundClueInfoProcessor: LevelDesignSoundClueInfoProcessor,
    private val randomQuestGenerator: RandomQuestGenerator,
) {
    companion object {
        private const val JSON_PATH = "ingame/level/level_data.json"
        private val logger: Logger = LoggerFactory.getLogger(LevelDesignInfoProcessor::class.java)
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
        val newLevel = Level(
            levelId = levelFromJson.levelId!!,
            version = levelFromJson.levelVersion!!,
            levelWidth = levelFromJson.levelWidth!!,
            levelHeight = levelFromJson.levelHeight!!,
            state = LevelState.ACTIVE
        )
        val savedLevel = levelRepository.save(newLevel)
        levelDesignContainerInfoProcessor.processContainers(levelFromJson.containers, savedLevel)
        levelDesignLanternInfoProcessor.processLanterns(levelFromJson.lanterns, savedLevel)
        levelDesignAltarInfoProcessor.processAltars(levelFromJson.altars, savedLevel)
        levelDesignRitualAreaInfoProcessor.processRitualArea(levelFromJson.ritualZones, savedLevel)
        levelDesignCrafterInfoProcessor.processCrafters(levelFromJson.crafters, savedLevel)
        levelDesignProcessStartInfoProcessor.processStartMarkers(levelFromJson.startMarkers, savedLevel)
        levelDesignZonesInfoProcessor.processZones(
            clueZones = levelFromJson.clueZones,
            banZones = levelFromJson.banZones,
            soundZones = levelFromJson.soundClueZones,
            level = savedLevel
        )

        val questGivers =
            levelDesignQuestGiverInfoProcessor.processQuestGiverFromJson(levelFromJson.questGivers, savedLevel)
        val levelTasks =
            levelDesignLevelTaskInfoProcessor.processLevelTasksFromJson(levelFromJson.levelTasks, savedLevel)

        if (CREATE_TEST_QUESTS) {
            logger.info("creating random quests")
            randomQuestGenerator.generateRandomQuests(savedLevel, questGivers, levelTasks)
        }
        levelDesignVoteSpotInfoProcessor.processVoteSpots(levelFromJson.votespots, savedLevel)
        levelDesignThresholdInfoProcessor.processThresholds(
            levelFromJson.ritualThresholds,
            ThresholdType.RITUAL,
            savedLevel
        )
        levelDesignThresholdInfoProcessor.processThresholds(
            levelFromJson.banThresholds,
            ThresholdType.BAN,
            savedLevel
        )
        levelDesignDoorInfoProcessor.processDoors(levelFromJson.doors, savedLevel)
        levelDesignScentClueInfoProcessor.processClueInfos(levelFromJson.scentClues, savedLevel)
        levelDesignSoundClueInfoProcessor.processSoundInfos(
            levelFromJson.soundClues,
            levelFromJson.soundClueJammers,
            savedLevel
        )
    }

    private fun findSameLevel(levelFromJson: LevelFromJson, levelsFromDb: List<Level>): Level? =
        levelsFromDb.firstOrNull {
            it.levelId == levelFromJson.levelId && it.version == levelFromJson.levelVersion
        }
}