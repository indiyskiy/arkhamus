package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.*
import com.arkhamusserver.arkhamus.model.database.entity.*
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelGeometryDto
import com.arkhamusserver.arkhamus.view.dto.admin.EllipseDto
import com.arkhamusserver.arkhamus.view.dto.admin.PointDto
import com.arkhamusserver.arkhamus.view.dto.admin.PolygonDto
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
) {

    fun geometry(levelId: Long): AdminGameLevelGeometryDto {
        val level = levelRepository.findByLevelId(levelId).maxBy { it.version }
        val tetragons = tetragonRepository.findByLevelZoneLevelId(levelId)
        val ellipses = ellipsesRepository.findByLevelZoneLevelId(levelId)

        val containers = containerRepository.findByLevelId(levelId)
        val altars = altarRepository.findByLevelId(levelId)
        val crafters = crafterRepository.findByLevelId(levelId)
        val lanterns = lanternRepository.findByLevelId(levelId)

        return AdminGameLevelGeometryDto(
            height = level.levelHeight.toInt() * SCREEN_ZOOM,
            width = level.levelWidth.toInt() * SCREEN_ZOOM,
            polygons = mapPolygons(tetragons),
            ellipses = mapEllipses(ellipses),
            keyPoints = mapKeyPoints(containers, altars, crafters, lanterns),

            )
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
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point1.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point1.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point2.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point2.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point3.x.toFloat() * SCREEN_ZOOM,
                        tetragon.point3.y.toFloat() * SCREEN_ZOOM,
                        tetragon.levelZone.id!!.toColor()
                    ),
                ),
                color = tetragon.levelZone.id!!.toColor()
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
                color = ellipse.levelZone.id!!.toColor()
            )
        }
    }


}

private fun Long.toColor(): NiceColor {
    return NiceColor.values()[(this % NiceColor.values().size).toInt()]
}
