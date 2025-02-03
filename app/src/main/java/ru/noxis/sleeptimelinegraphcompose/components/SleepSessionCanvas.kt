package ru.noxis.sleeptimelinegraphcompose.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionRecord
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionStageType
import kotlin.time.Duration.Companion.minutes

@Composable
fun SleepSessionCanvas(
    modifier: Modifier = Modifier,
    record: SleepSessionRecord,
    stageHeight: Dp = 48.dp,
    stagesSpacing: Dp = 16.dp,
    textSpacing: Dp = 4.dp,
    typeTextStyle: TextStyle = LocalTextStyle.current,
    timeTextStyle: TextStyle = LocalTextStyle.current,
) {
    val colors = remember {
        mapOf(
            SleepSessionStageType.Awake to Color(0xFFFF9800),
            SleepSessionStageType.Light to Color(0xFF2196F3),
            SleepSessionStageType.Deep to Color(0xFF673AB7),
            SleepSessionStageType.REM to Color(0xFF795548),
        )
    }
    val textMeasurer = rememberTextMeasurer()

    val stageHeightPx = with(LocalDensity.current) { stageHeight.toPx() }
    val stagesSpacingPx = with(LocalDensity.current) { stagesSpacing.toPx() }
    val textSpacingPx = with(LocalDensity.current) { textSpacing.toPx() }

    val typeTextHeight = with(LocalDensity.current) {
        remember {
            textMeasurer.measure("", style = typeTextStyle).size.height
        }.toDp()
    }

    val timeTextHeight = with(LocalDensity.current) {
        remember {
            textMeasurer.measure("", style = timeTextStyle).size.height
        }.toDp()
    }
    Spacer(
        modifier = modifier
            .requiredHeight((stageHeight + typeTextHeight + textSpacing) * colors.size + stagesSpacing * (colors.size - 1) + textSpacing + timeTextHeight)
            .drawWithCache {
                val stages = listOf(
                    SleepSessionStageType.Awake,
                    SleepSessionStageType.REM,
                    SleepSessionStageType.Light,
                    SleepSessionStageType.Deep,
                ).map { type ->
                    val stages = record.stages.filter { it.type == type }
                    val stageDuration =
                        stages.sumOf { (it.endTime - it.startTime).inWholeMinutes }.minutes
                    DrawStage(
                        title = textMeasurer.measure(
                            text = "$type + $stageDuration",
                            style = typeTextStyle,
                            maxLines = 1
                        ),
                        type = type,
                        points = calculatePoints(
                            canvasSize = size.copy(height = stageHeightPx),
                            recordStartTime = record.startTime,
                            recordEndTime = record.endTime,
                            stages = record.stages.filter { it.type == type },
                        )
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
                    stages.forEach { (title, type, points) ->
                        // Draw title (type and duration)
                        translate(top = offset) {
                            drawText(title)
                        }
                        offset += title.size.height + textSpacingPx
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
private fun calculatePoints(
    canvasSize: Size,
    recordStartTime: Instant,
    recordEndTime: Instant,
    stages: List<SleepSessionRecord.Stage>,
): List<DrawPoint> {
    val totalDuration = (recordEndTime - recordStartTime).inWholeSeconds.toFloat()
        .coerceAtLeast(1f)

    return stages.map { stage ->
        val stageOffset =
            (stage.startTime - recordStartTime).inWholeSeconds / totalDuration
        val stageDuration =
            (stage.endTime - stage.startTime).inWholeSeconds.toFloat() / totalDuration

        DrawPoint(
            topLeft = Offset(x = canvasSize.width * stageOffset, y = 0f),
            size = canvasSize.copy(width = canvasSize.width * stageDuration),
        )
    }
}

private data class DrawPoint(
    val topLeft: Offset,
    val size: Size,
)

private data class DrawStage(
    val title: TextLayoutResult,
    val type: SleepSessionStageType,
    val points: List<DrawPoint>,
)