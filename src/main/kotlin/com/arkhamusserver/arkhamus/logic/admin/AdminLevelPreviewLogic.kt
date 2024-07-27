package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.*
import com.arkhamusserver.arkhamus.model.database.entity.game.*
import com.arkhamusserver.arkhamus.view.dto.admin.*
import org.springframework.stereotype.Component

private const val SCREEN_ZOOM = 10

@Component
class AdminLevelPreviewLogic(
    private val levelRepository: LevelRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipsesRepository: EllipseRepository,
    private val containerRepository: ContainerRepository,
    private val altarRepository: AltarRepository,
    private val crafterRepository: CrafterRepository,
    private val lanternRepository: LanternRepository,
    private val questGiverRepository: QuestGiverRepository,
    private val levelTaskRepository: LevelTaskRepository,
) {
    fun geometry(
        levelId: Long,
        filter: LevelFilterDto = LevelFilterDto.allTrue(levelId)
    ): AdminGameLevelGeometryDto {
        val level = levelRepository.findByLevelId(levelId).maxBy { it.version }

        val tetragons = if (filter.zones == true) {
            tetragonRepository.findByLevelZoneLevelId(levelId)
        } else emptyList()

        val ellipses = if (filter.zones == true) {
            ellipsesRepository.findByLevelZoneLevelId(levelId)
        } else emptyList()

        val containers = if (filter.containers == true) {
            containerRepository.findByLevelId(levelId)
        } else emptyList()
        val altars = if (filter.altars == true) {
            altarRepository.findByLevelId(levelId)
        } else emptyList()
        val crafters = if (filter.containers == true) {
            crafterRepository.findByLevelId(levelId)
        } else emptyList()
        val lanterns = if (filter.lanterns == true) {
            lanternRepository.findByLevelId(levelId)
        } else emptyList()

        val questGivers = if (filter.questGivers == true) {
            questGiverRepository.findByLevelId(levelId)
        } else emptyList()

        val levelTasks = if (filter.levelTasks == true) {
            levelTaskRepository.findByLevelId(levelId)
        } else emptyList()

        return AdminGameLevelGeometryDto(
            levelId = level.levelId,
            height = level.levelHeight.toInt() * SCREEN_ZOOM,
            width = level.levelWidth.toInt() * SCREEN_ZOOM,
            polygons = mapPolygons(tetragons),
            ellipses = mapEllipses(ellipses),
            keyPoints = mapKeyPoints(containers, altars, crafters, lanterns),
            questGivers = mapQuestGivers(questGivers),
            tasks = mapTasks(levelTasks)
        )
    }

    private fun mapQuestGivers(questGivers: List<QuestGiver>): List<NpcDto> {
        return questGivers.map {
            NpcDto(
                points = listOf(
                    PointDto(
                        (it.point.x * SCREEN_ZOOM).toFloat(),
                        (it.point.y * SCREEN_ZOOM).toFloat(),
                        NiceColor.VIOLET
                    ),
                    PointDto(
                        (it.point.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.point.y * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.VIOLET
                    ),
                    PointDto(
                        (it.point.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.point.y * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.VIOLET
                    )
                ),
                color = NiceColor.VIOLET
            )
        }
    }

    private fun mapTasks(levelTasks: List<LevelTask>): List<TaskGeometryDto> {
        return levelTasks.map {
            TaskGeometryDto(
                points = listOf(
                    PointDto(
                        it.point.x.toFloat() * SCREEN_ZOOM,
                        it.point.y.toFloat() * SCREEN_ZOOM,
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.point.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.point.y * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.point.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.point.y * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    )
                ),
                color = NiceColor.MEDIUM_PURPLE
            )
        }
    }

    private fun mapKeyPoints(
        containers: List<Container>,
        altars: List<Altar>,
        crafters: List<Crafter>,
        lanterns: List<Lantern>
    ): List<PointDto> {
        return mapContainers(containers) + mapCrafters(crafters) + mapAltars(altars) + mapLanterns(lanterns)
    }

    private fun mapContainers(containers: List<Container>): List<PointDto> {
        return containers.map {
            PointDto(
                it.point.x.toFloat() * SCREEN_ZOOM,
                it.point.y.toFloat() * SCREEN_ZOOM,
                27L.toColor()
            )
        }
    }

    private fun mapCrafters(crafters: List<Crafter>): List<PointDto> {
        return crafters.map {
            PointDto(
                it.point.x.toFloat() * SCREEN_ZOOM,
                it.point.y.toFloat() * SCREEN_ZOOM,
                28L.toColor()
            )
        }
    }

    private fun mapAltars(altars: List<Altar>): List<PointDto> {
        return altars.map {
            PointDto(
                it.point.x.toFloat() * SCREEN_ZOOM,
                it.point.y.toFloat() * SCREEN_ZOOM,
                29L.toColor()
            )
        }
    }

    private fun mapLanterns(lanterns: List<Lantern>): List<PointDto> {
        return lanterns.map {
            PointDto(
                it.point.x.toFloat() * SCREEN_ZOOM,
                it.point.y.toFloat() * SCREEN_ZOOM,
                30L.toColor()
            )
        }
    }

    private fun mapPolygons(tetragons: List<Tetragon>): List<PolygonDto> {
        return tetragons.map { tetragon ->
            PolygonDto(
                listOf(
                    PointDto(
                        tetragon.point0.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point0.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point1.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point1.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point2.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point2.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point3.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point3.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                ),
                color = tetragon.levelZone.inGameId.toColor()
            )
        }
    }

    private fun mapEllipses(ellipses: List<Ellipse>): List<EllipseDto> {
        return ellipses.map { ellipse ->
            EllipseDto(
                cx = ellipse.point.x.toFloat() * SCREEN_ZOOM,
                cy = ellipse.point.y.toFloat() * SCREEN_ZOOM,
                rx = ellipse.width.toFloat() / 2 * SCREEN_ZOOM,
                ry = ellipse.height.toFloat() / 2 * SCREEN_ZOOM,
                color = ellipse.levelZone.inGameId.toColor()
            )
        }
    }


}

private fun Long.toColor(): NiceColor {
    return NiceColor.values()[(this % NiceColor.values().size).toInt()]
}
