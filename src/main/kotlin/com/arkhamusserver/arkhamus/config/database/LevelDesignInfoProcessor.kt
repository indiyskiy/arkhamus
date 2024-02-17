package com.arkhamusserver.arkhamus.config.database

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.LevelRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.model.database.entity.StartMarker
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.view.levelDesign.ContainerFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.JsonStartMarker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader


@Component
class LevelDesignInfoProcessor(
    private val levelRepository: LevelRepository,
    private val containerRepository: ContainerRepository,
    private val startMarkerRepository: StartMarkerRepository
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(LevelDesignInfoProcessor::class.java)
        var gson: Gson = Gson()
    }

    @PostConstruct
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
        val path = javaClass.getClassLoader().getResource("ingame/level/level_data.json")
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
                                (version > (it.version ?: 0)) &&
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
            levelId = levelFromJson.levelId,
            version = levelFromJson.levelVersion,
            levelWidth = levelFromJson.levelWidth,
            levelHeight = levelFromJson.levelHeight,
            state = LevelState.ACTIVE
        )
        val savedLevel = levelRepository.save(newLevel)
        processContainers(levelFromJson.containers, savedLevel)
        processStartMarkers(levelFromJson.startMarkers, savedLevel)
    }

    private fun processContainers(
        containers: List<ContainerFromJson>,
        savedLevel: Level?
    ) {
        containers.forEach { container ->
            Container(
                inGameId = container.id,
                interactionRadius = container.interactionRadius,
                x = container.x,
                y = container.y,
                level = savedLevel
            ).apply {
                containerRepository.save(this)
            }
        }
    }

    private fun processStartMarkers(
        containers: List<JsonStartMarker>,
        savedLevel: Level?
    ) {
        containers.forEach { startMarker ->
            StartMarker(
                x = startMarker.x,
                y = startMarker.y,
                level = savedLevel
            ).apply {
                startMarkerRepository.save(this)
            }
        }
    }

    private fun findSameLevel(levelFromJson: LevelFromJson, levelsFromDb: List<Level>): Level? =
        levelsFromDb.firstOrNull {
            it.levelId == levelFromJson.levelId && it.version == levelFromJson.levelVersion
        }

}