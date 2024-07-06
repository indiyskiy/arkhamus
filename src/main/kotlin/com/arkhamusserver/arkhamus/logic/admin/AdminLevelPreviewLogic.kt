package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.*
import com.arkhamusserver.arkhamus.model.database.entity.*
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelGeometryDto
import com.arkhamusserver.arkhamus.view.dto.admin.EllipseDto
import com.arkhamusserver.arkhamus.view.dto.admin.PointDto
import com.arkhamusserver.arkhamus.view.dto.admin.PolygonDto
import org.springframework.stereotype.Component

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
            height = level.levelHeight.toInt(),
            width = level.levelWidth.toInt(),
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
                it.point.x.toFloat(),
                it.point.y.toFloat(),
                27L.toColor()
            )
        }
    }

    private fun mapCrafters(crafters: List<Crafter>): List<PointDto> {
        return crafters.map {
            PointDto(
                it.point.x.toFloat(),
                it.point.y.toFloat(),
                28L.toColor()
            )
        }
    }

    private fun mapAltars(altars: List<Altar>): List<PointDto> {
        return altars.map {
            PointDto(
                it.point.x.toFloat(),
                it.point.y.toFloat(),
                29L.toColor()
            )
        }
    }

    private fun mapLanterns(lanterns: List<Lantern>): List<PointDto> {
        return lanterns.map {
            PointDto(
                it.point.x.toFloat(),
                it.point.y.toFloat(),
                30L.toColor()
            )
        }
    }

    private fun mapPolygons(tetragons: List<Tetragon>): List<PolygonDto> {
        return tetragons.map { tetragon ->
            PolygonDto(
                listOf(
                    PointDto(
                        tetragon.point0.x.toFloat(),
                        tetragon.point0.y.toFloat(),
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point1.x.toFloat(),
                        tetragon.point1.y.toFloat(),
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point2.x.toFloat(),
                        tetragon.point2.y.toFloat(),
                        tetragon.levelZone.id!!.toColor()
                    ),
                    PointDto(
                        tetragon.point3.x.toFloat(),
                        tetragon.point3.y.toFloat(),
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
                cx = ellipse.point.x.toFloat(),
                cy = ellipse.point.y.toFloat(),
                rx = ellipse.width.toFloat() / 2,
                ry = ellipse.height.toFloat() / 2,
                color = ellipse.levelZone.id!!.toColor()
            )
        }
    }


}

private fun Long.toColor(): NiceColor {
    return NiceColor.values()[(this % NiceColor.values().size).toInt()]
}
