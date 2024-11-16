package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.*
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Altar
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Container
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Crafter
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Door
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Ellipse
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Lantern
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelTask
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.QuestGiver
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Tetragon
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Threshold
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.VoteSpot
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.dto.admin.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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
    private val voteSpotRepository: VoteSpotRepository,
    private val doorRepository: DoorRepository,
    private val thresholdRepository: ThresholdRepository,
) {

    companion object{
        const val SCREEN_ZOOM = 10
    }

    @Transactional
    fun geometry(
        levelId: Long,
        filter: LevelFilterDto = LevelFilterDto.allTrue(levelId)
    ): AdminGameLevelGeometryDto {
        val level = levelRepository.findByLevelId(levelId).maxBy { it.version }

        val tetragons = tetragonRepository.findByLevelZoneLevelId(levelId)
        val ellipses = ellipsesRepository.findByLevelZoneLevelId(levelId)

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

        val voteSpots = if (filter.voteSpotData == true) {
            voteSpotRepository.findByLevelId(levelId)
        } else emptyList()

        val doors = if (filter.voteSpotData == true) {
            doorRepository.findByLevelId(levelId)
        } else emptyList()

        val thresholds = if (filter.voteSpotData == true) {
            thresholdRepository.findByLevelId(levelId)
        } else emptyList()

        val banZones = if (filter.voteSpotData == true) {
            ZoneDataDto(
                mapPolygons(tetragons.filter { tetragon -> tetragon.levelZone.zoneType == ZoneType.BAN }),
                mapEllipses(ellipses.filter { ellipse -> ellipse.levelZone.zoneType == ZoneType.BAN }),
            )
        } else {
            ZoneDataDto(emptyList(), emptyList())
        }
        val clueZones = if (filter.clueZones == true) {
            ZoneDataDto(
                mapPolygons(tetragons.filter { tetragon -> tetragon.levelZone.zoneType == ZoneType.CLUE }),
                mapEllipses(ellipses.filter { ellipse -> ellipse.levelZone.zoneType == ZoneType.CLUE }),
            )
        } else {
            ZoneDataDto(emptyList(), emptyList())
        }

        return AdminGameLevelGeometryDto(
            levelId = level.levelId,
            height = level.levelHeight.toInt() * SCREEN_ZOOM,
            width = level.levelWidth.toInt() * SCREEN_ZOOM,
            banZones = banZones,
            clueZones = clueZones,
            keyPoints = mapKeyPoints(containers, altars, crafters, lanterns),
            questGivers = mapQuestGivers(questGivers),
            tasks = mapTasks(levelTasks),
            voteSpots = mapVoteSpots(voteSpots),
            doors = mapDoors(doors),
            thresholds = mapThresholds(thresholds)
        )
    }

    private fun mapQuestGivers(questGivers: List<QuestGiver>): List<NpcDto> {
        return questGivers.map {
            NpcDto(
                points = listOf(
                    PointDto(
                        (it.x * SCREEN_ZOOM).toFloat(),
                        (it.z * SCREEN_ZOOM).toFloat(),
                        NiceColor.VIOLET
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.VIOLET
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 10).toFloat(),
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
                        it.x.toFloat() * SCREEN_ZOOM,
                        it.z.toFloat() * SCREEN_ZOOM,
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 10).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    )
                ),
                color = NiceColor.MEDIUM_PURPLE
            )
        }
    }

    private fun mapVoteSpots(voteSpots: List<VoteSpot>): List<VoteSpotDto> {
        return voteSpots.map {
            VoteSpotDto(
                points = listOf(
                    PointDto(
                        it.x.toFloat() * SCREEN_ZOOM - 5,
                        it.z.toFloat() * SCREEN_ZOOM - 5,
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    )
                ),
                color = NiceColor.GREEN_YELLOW
            )
        }
    }

    private fun mapDoors(doors: List<Door>): List<DoorDto> {
        return doors.map {
            DoorDto(
                points = listOf(
                    PointDto(
                        it.x.toFloat() * SCREEN_ZOOM - 5,
                        it.z.toFloat() * SCREEN_ZOOM - 5,
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    )
                ),
                color = NiceColor.GREEN_YELLOW
            )
        }
    }


    private fun mapThresholds(doors: List<Threshold>): List<ThresholdDto> {
        return doors.map {
            ThresholdDto(
                points = listOf(
                    PointDto(
                        it.x.toFloat() * SCREEN_ZOOM - 5,
                        it.z.toFloat() * SCREEN_ZOOM - 5,
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM + 5).toFloat(),
                        (it.z * SCREEN_ZOOM - 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    ),
                    PointDto(
                        (it.x * SCREEN_ZOOM - 5).toFloat(),
                        (it.z * SCREEN_ZOOM + 5).toFloat(),
                        NiceColor.MEDIUM_PURPLE
                    )
                ),
                color = NiceColor.GREEN_YELLOW
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
                it.x.toFloat() * SCREEN_ZOOM,
                it.z.toFloat() * SCREEN_ZOOM,
                27L.toColor()
            )
        }
    }

    private fun mapCrafters(crafters: List<Crafter>): List<PointDto> {
        return crafters.map {
            PointDto(
                it.x.toFloat() * SCREEN_ZOOM,
                it.z.toFloat() * SCREEN_ZOOM,
                28L.toColor()
            )
        }
    }

    private fun mapAltars(altars: List<Altar>): List<PointDto> {
        return altars.map {
            PointDto(
                it.x.toFloat() * SCREEN_ZOOM,
                it.z.toFloat() * SCREEN_ZOOM,
                29L.toColor()
            )
        }
    }

    private fun mapLanterns(lanterns: List<Lantern>): List<PointDto> {
        return lanterns.map {
            PointDto(
                it.x.toFloat() * SCREEN_ZOOM,
                it.z.toFloat() * SCREEN_ZOOM,
                30L.toColor()
            )
        }
    }

    private fun mapPolygons(tetragons: List<Tetragon>): List<PolygonDto> {
        return tetragons.map { tetragon ->
            PolygonDto(
                listOf(
                    PointDto(
                        tetragon.point0X.toFloat() * SCREEN_ZOOM,
                        tetragon.point0Z.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point1X.toFloat() * SCREEN_ZOOM,
                        tetragon.point1Z.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point2X.toFloat() * SCREEN_ZOOM,
                        tetragon.point2Z.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.inGameId.toColor()
                    ),
                    PointDto(
                        tetragon.point3X.toFloat() * SCREEN_ZOOM,
                        tetragon.point3Z.toFloat() * SCREEN_ZOOM,
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
                cx = ellipse.x.toFloat() * SCREEN_ZOOM,
                cy = ellipse.z.toFloat() * SCREEN_ZOOM,
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
