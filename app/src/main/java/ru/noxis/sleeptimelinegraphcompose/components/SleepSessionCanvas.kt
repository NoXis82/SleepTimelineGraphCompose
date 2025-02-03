package ru.noxis.sleeptimelinegraphcompose.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionRecord
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionStageType
import ru.noxis.sleeptimelinegraphcompose.model.SleepStageDrawPoint

@Composable
fun SleepSessionCanvas(
    modifier: Modifier = Modifier,
    record: SleepSessionRecord,
    stageHeight: Dp = 48.dp,
    stagesSpacing: Dp = 16.dp,
) {
    val colors = remember {
        mapOf(
            SleepSessionStageType.Awake to Color(0xFFFF9800),
            SleepSessionStageType.Light to Color(0xFF2196F3),
            SleepSessionStageType.Deep to Color(0xFF673AB7),
            SleepSessionStageType.REM to Color(0xFF795548),
        )
    }

    val stageHeightPx = with(LocalDensity.current) { stageHeight.toPx() }
    val stagesSpacingPx = with(LocalDensity.current) { stagesSpacing.toPx() }

    Spacer(
        modifier = modifier
            .requiredHeight(stageHeight * colors.size + stagesSpacing * (colors.size - 1))
            .drawWithCache {

                val stages = listOf(
                    SleepSessionStageType.Awake,
                    SleepSessionStageType.REM,
                    SleepSessionStageType.Light,
                    SleepSessionStageType.Deep,
                ).map { type ->
                    type to calculate(
                        canvasSize = size.copy(height = stageHeightPx),
                        recordStartTime = record.startTime,
                        recordEndTime = record.endTime,
                        stages = record.stages.filter { it.type == type },
                    )
                }

//                val points = calculate(
//                    canvasSize = size,
//                    recordStartTime = record.startTime,
//                    recordEndTime = record.endTime,
//                    stages = record.stages.filter { it.type == SleepSessionStageType.Deep },
//                )

                onDrawWithContent {
                    var offset = 0f
                    stages.forEach { (type, points) ->
                        translate(top = offset) {
                            // Draw background
                            drawRoundRect(
                                color = Color.LightGray,
                                topLeft = Offset(x = 0f, y = stageHeightPx / 4),
                                size = size.copy(height = stageHeightPx / 2),
                                cornerRadius = CornerRadius(stageHeightPx / 2),
                            )

                            // Draw stage points
                            points.forEach { point ->
                                drawRect(
                                    topLeft = point.topLeft,
                                    size = point.size,
                                    color = colors.getValue(type),
                                )
                            }
                        }
                        offset += stageHeightPx + stagesSpacingPx
                    }
                }
            }
    )
}


/**
 * To draw a rect in Compose we need topOffset and size .
 * topOffset = ((stage startTime) - (session startTime)) / session duration * canvas.width
 * size = stage duration * canvas.width
 */
private fun calculate(
    canvasSize: Size,
    recordStartTime: Instant,
    recordEndTime: Instant,
    stages: List<SleepSessionRecord.Stage>,
): List<SleepStageDrawPoint> {
    val totalDuration = (recordEndTime - recordStartTime).inWholeSeconds.toFloat()
        .coerceAtLeast(1f)

    return stages.map { stage ->
        val stageOffset =
            (stage.startTime - recordStartTime).inWholeSeconds / totalDuration
        val stageDuration =
            (stage.endTime - stage.startTime).inWholeSeconds.toFloat() / totalDuration

        SleepStageDrawPoint(
            topLeft = Offset(x = canvasSize.width * stageOffset, y = 0f),
            size = canvasSize.copy(width = canvasSize.width * stageDuration),
        )
    }
}
