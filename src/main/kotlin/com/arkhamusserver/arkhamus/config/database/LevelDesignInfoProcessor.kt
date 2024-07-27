package com.arkhamusserver.arkhamus.config.database

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.CREATE_TEST_QUESTS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.quest.LevelDifficultyLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.*
import com.arkhamusserver.arkhamus.model.database.entity.game.*
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.levelDesign.*
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import jakarta.annotation.PostConstruct
import org.postgresql.geometric.PGpoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import kotlin.math.min
import kotlin.random.Random

@Component
class LevelDesignInfoProcessor(
    private val levelRepository: LevelRepository,
    private val containerRepository: ContainerRepository,
    private val lanternRepository: LanternRepository,
    private val altarRepository: AltarRepository,
    private val ritualAreaRepository: RitualAreaRepository,
    private val levelZoneRepository: LevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipseRepository: EllipseRepository,
    private val crafterRepository: CrafterRepository,
    private val startMarkerRepository: StartMarkerRepository,
    private val questGiverRepository: QuestGiverRepository,
    private val levelTaskRepository: LevelTaskRepository,
    private val questRepository: QuestRepository,
    private val questStepRepository: QuestStepRepository,
    private val levelDifficultyLogic: LevelDifficultyLogic
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(LevelDesignInfoProcessor::class.java)
        var random = Random(System.currentTimeMillis())
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
        processContainers(levelFromJson.containers, savedLevel)
        processLanterns(levelFromJson.lanterns, savedLevel)
        processAltars(levelFromJson.altars, savedLevel)
        processRitualArea(levelFromJson.ritualAreas, savedLevel)
        processCrafters(levelFromJson.crafters, savedLevel)
        processStartMarkers(levelFromJson.startMarkers, savedLevel)
        processClueZones(levelFromJson.clueZones, savedLevel)

        val questGivers = processQuestGiverFromJson(levelFromJson.questGivers, savedLevel)
        val levelTasks = processLevelTasksFromJson(levelFromJson.levelTasks, savedLevel)

        if (CREATE_TEST_QUESTS) {
            logger.info("creating random quests")
            generateRandomQuests(savedLevel, questGivers, levelTasks)
        }
    }

    private fun processLevelTasksFromJson(levelTasks: List<LevelTaskFromJson>, savedLevel: Level): List<LevelTask> {
        levelTasks.map { jsonLevelTask ->
            LevelTask(
                inGameId = jsonLevelTask.id!!,
                point = PGpoint(jsonLevelTask.x!!, jsonLevelTask.y!!),
                interactionRadius = jsonLevelTask.interactionRadius!!,
                level = savedLevel,
                name = jsonLevelTask.name!!
            )
        }.apply {
            return levelTaskRepository.saveAll(this).toList()
        }
    }

    private fun processQuestGiverFromJson(questGivers: List<QuestGiverFromJson>, savedLevel: Level): List<QuestGiver> {
        questGivers.map { jsonQuestGiver ->
            QuestGiver(
                inGameId = jsonQuestGiver.id!!,
                point = PGpoint(jsonQuestGiver.x!!, jsonQuestGiver.y!!),
                interactionRadius = jsonQuestGiver.interactionRadius!!,
                level = savedLevel,
                name = jsonQuestGiver.name!!
            )
        }.apply {
            return questGiverRepository.saveAll(this).toList()
        }
    }

    private fun processClueZones(clueZones: List<ClueZoneFromJson>, savedLevel: Level) {
        clueZones.forEach { clueZone ->
            val levelZone = LevelZone(
                inGameId = clueZone.zoneId!!,
                zoneType = ZoneType.CLUE,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(clueZone.tetragons, levelZone)
            processClueEllipses(clueZone.ellipses, levelZone)
        }
    }

    private fun processClueTetragons(
        tetragons: List<TetragonFromJson>,
        levelZone: LevelZone,
    ) {
        tetragons.forEach { tetragon ->
            assertTrue(
                tetragon.points.size == 4,
                "Size of tetragon ${tetragon.id} is not 4",
                TetragonFromJson::class.simpleName!!
            )
            Tetragon(
                inGameId = tetragon.id!!,
                levelZone = levelZone,
                point0 = PGpoint(tetragon.points[0].x!!, tetragon.points[0].y!!),
                point1 = PGpoint(tetragon.points[1].x!!, tetragon.points[1].y!!),
                point2 = PGpoint(tetragon.points[2].x!!, tetragon.points[2].y!!),
                point3 = PGpoint(tetragon.points[3].x!!, tetragon.points[3].y!!),
            ).apply {
                tetragonRepository.save(this)
            }
        }
    }

    private fun processClueEllipses(
        ellipses: List<EllipseFromJson>,
        levelZone: LevelZone,
    ) {
        ellipses.forEach { ellipse ->
            Ellipse(
                inGameId = ellipse.id!!,
                levelZone = levelZone,
                point = PGpoint(ellipse.center!!.x!!, ellipse.center!!.y!!),
                height = ellipse.height!!,
                width = ellipse.width!!,
            ).apply {
                ellipseRepository.save(this)
            }
        }
    }

    private fun processContainers(
        containers: List<ContainerFromJson>,
        savedLevel: Level?
    ) {
        containers.forEach { container ->
            Container(
                inGameId = container.id!!,
                interactionRadius = container.interactionRadius!!,
                point = PGpoint(container.x!!, container.y!!),
                level = savedLevel
            ).apply {
                containerRepository.save(this)
            }
        }
    }

    private fun processLanterns(
        containers: List<LanternFromJson>,
        savedLevel: Level?
    ) {
        containers.forEach { lantern ->
            Lantern(
                inGameId = lantern.id!!,
                lightRange = lantern.lightRange,
                point = PGpoint(lantern.x!!, lantern.y!!),
                level = savedLevel!!
            ).apply {
                lanternRepository.save(this)
            }
        }
    }

    private fun processAltars(
        altars: List<AltarFromJson>,
        savedLevel: Level?
    ) {
        altars.forEach { altar ->
            Altar(
                inGameId = altar.id,
                interactionRadius = altar.interactionRadius,
                point = PGpoint(altar.x!!, altar.y!!),
                level = savedLevel
            ).apply {
                altarRepository.save(this)
            }
        }
    }

    private fun processRitualArea(
        ritualAreas: List<RitualAreaFromJson>,
        savedLevel: Level?
    ) {
        ritualAreas.first().let { ritualArea ->
            RitualArea(
                inGameId = ritualArea.id!!,
                radius = ritualArea.radius!!,
                point = PGpoint(ritualArea.x!!, ritualArea.y!!),
                level = savedLevel!!
            ).apply {
                ritualAreaRepository.save(this)
            }
        }
    }

    private fun processCrafters(
        containers: List<CrafterFromJson>,
        savedLevel: Level?,
    ) {
        containers.forEach { crafter ->
            Crafter(
                inGameId = crafter.id!!,
                interactionRadius = crafter.interactionRadius!!,
                point = PGpoint(crafter.x!!, crafter.y!!),
                level = savedLevel!!,
                crafterType = crafter.crafterType!!,
            ).apply {
                crafterRepository.save(this)
            }
        }
    }

    private fun processStartMarkers(
        containers: List<JsonStartMarker>,
        savedLevel: Level?
    ) {
        containers.forEach { startMarker ->
            StartMarker(
                point = PGpoint(startMarker.x!!, startMarker.y!!),
                level = savedLevel!!
            ).apply {
                startMarkerRepository.save(this)
            }
        }
    }

    private fun findSameLevel(levelFromJson: LevelFromJson, levelsFromDb: List<Level>): Level? =
        levelsFromDb.firstOrNull {
            it.levelId == levelFromJson.levelId && it.version == levelFromJson.levelVersion
        }


    private fun generateRandomQuests(level: Level, questGivers: List<QuestGiver>, levelTasks: List<LevelTask>) {
        val hasOldQuests = questRepository.findAll().iterator().hasNext()
        if (hasOldQuests) {
            return
        }
        logger.info("processing quest for level ${level.id}")
        (0..QUESTS_ON_START * 2).map { number ->
            val randomQuestGiverStart = questGivers.random()
            val randomQuestGiverEnd = questGivers.random()

            val stepSize = random.nextInt(1, min(5, levelTasks.size + 1))
            val newQuest = Quest(
                id = null,
                level = level,
                questSteps = mutableListOf(),
                questState = QuestState.ACTIVE,
                name = "awesome quest $number",
                startQuestGiver = randomQuestGiverStart,
                endQuestGiver = randomQuestGiverEnd,
            )
            levelTasks.shuffled(random).take(stepSize).forEachIndexed { i, task ->
                val step = QuestStep(
                    id = null,
                    stepNumber = i,
                    quest = newQuest,
                    levelTask = task
                )
                newQuest.addQuestStep(step)
            }

            logger.info("created quest: ${newQuest.name}")
            levelDifficultyLogic.recount(newQuest)
            questRepository.save(newQuest)
            questStepRepository.saveAll(newQuest.questSteps)
        }
    }
}