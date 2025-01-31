package com.arkhamusserver.arkhamus.logic.ingame.logic.visibility

import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class VisibilityMap private constructor(private val segments: Map<Double, MapSegmentCollection>) {

    private val gridSizes = segments.keys.sorted().toTypedArray()

    fun findVisibilitySegment(observer: WithPoint, observable: WithPoint): VisibilityMapSegment? {
        val xDiff = abs(observer.x() - observable.x())
        val yDiff = abs(observer.z() - observable.z())
        val minGridSize = max(xDiff, yDiff)
        // find grid size that could fit both objects
        // TODO we can return index as well, implement it later
        var index = findPointIndex(minGridSize, gridSizes, false)
        // try out and gradually increase grid size, should finish in one step if we step grid factor by 2
        while (index < gridSizes.size) {
            // TODO NPE
            val foundSegment = segments[gridSizes[index]] ?: return null
            val observerMarkerPoint = foundSegment.markerPointTruncator.invoke(observer.x(), observer.z())
            val observableMarkerPoint = foundSegment.markerPointTruncator.invoke(observable.x(), observable.z())
            if (observerMarkerPoint == observableMarkerPoint) {
                // we guessed the grid size right, both points are in one segment
                // TODO NPE
                return foundSegment.segmentMap[observerMarkerPoint]
            }
            index++
        }
        return null
    }

    companion object {
        const val MIN_SEGMENT_SIZE = 10.0

        fun build(obstaclesMap: ObstaclesMap, minSegmentSize: Double): VisibilityMap {
            val width = obstaclesMap.xMax - obstaclesMap.xMin
            val height = obstaclesMap.zMax - obstaclesMap.zMin
            var gridSize = min(width, height)
            val segments = HashMap<Double, MapSegmentCollection>()
            while (gridSize > minSegmentSize) {
                val segmentCollection = MapSegmentCollection.build(gridSize, obstaclesMap)
                segments[gridSize] = segmentCollection
                gridSize /= 2
            }
            return VisibilityMap(segments)
        }
    }
}

class MapSegmentCollection private constructor(
    val gridSize: Double,
    val markerPointTruncator: (Double, Double) -> Pair<Double, Double>,
    val segmentMap: Map<Pair<Double, Double>, VisibilityMapSegment>
) {
    companion object {
        fun build(gridSize: Double, map: ObstaclesMap): MapSegmentCollection {
            var markerX = 0.0
            var markerZ = 0.0
            val segmentMap = HashMap<Pair<Double, Double>, VisibilityMapSegment>()
            val markerXPoints = HashSet<Double>()
            val markerZPoints = HashSet<Double>()
            while (markerX < map.xMax) {
                markerXPoints += markerX
                while (markerZ < map.zMax) {
                    // we can do this better, but it's not worth it
                    markerZPoints += markerZ
                    val markerPoint = markerX to markerZ
                    val xMin = markerX
                    val zMin = markerZ
                    val xMax = min(xMin + gridSize, map.xMax)
                    val zMax = min(zMin + gridSize, map.zMax)
                    val obstacles = map.obstacles.filter { it.intersectsRect(xMin, zMin, xMax, zMax) }
                    segmentMap.put(markerPoint, VisibilityMapSegment(xMin, zMin, xMax, zMax, obstacles, markerPoint))
                    markerZ += gridSize
                }
                markerZ = 0.0
                markerX += gridSize
            }
            return MapSegmentCollection(
                gridSize = gridSize,
                markerPointTruncator = buildMarkerTruncator(markerXPoints, markerZPoints),
                segmentMap = segmentMap
            )
        }

        private fun buildMarkerTruncator(
            markerXPoints: HashSet<Double>,
            markerZPoints: HashSet<Double>
        ): (Double, Double) -> Pair<Double, Double> {
            val xSorted = markerXPoints.sorted().toTypedArray()
            val zSorted = markerZPoints.sorted().toTypedArray()
            return { x: Double, z: Double ->    findPoint(x, xSorted) to findPoint(z, zSorted) }
        }
    }
}

private fun findPoint(coord: Double, markerPoints: Array<Double>, returnPrevious: Boolean = true): Double {
    // TODO switch to binary search
    var previous = markerPoints.first()
    markerPoints.forEach { current ->
        if (current > coord) {
            return if (returnPrevious) previous else current
        } else {
            previous = current
        }
    }
    return markerPoints.last()
}

private fun findPointIndex(coord: Double, markerPoints: Array<Double>, returnPrevious: Boolean = true): Int {
    // TODO switch to binary search
    var index = 0
    while (index < markerPoints.size) {
        val current = markerPoints[index]
        if (current > coord) {
            return if (returnPrevious) index - 1 else index
        } else {
            index ++
        }
    }
    return markerPoints.size - 1
}

data class VisibilityMapSegment(
    val xMin: Double,
    val zMin: Double,
    val xMax: Double,
    val zMax: Double,
    val obstacles: List<VisibilityObstacle>,
    val markerPoint: Pair<Double, Double>
)