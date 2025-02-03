package ru.noxis.sleeptimelinegraphcompose.model

import kotlinx.datetime.Instant
import kotlinx.datetime.UtcOffset


/**
 * тип сна, в котором находится пользователь
 * в течение определенного периода времени
 * Общий сеанс, охватывающий всю продолжительность сна.
 * Отдельные стадии во время сеанса сна, такие как легкий или глубокий сон.
 */
data class SleepSessionRecord(
    val startTime: Instant,
    val startZoneOffset: UtcOffset,
    val endTime: Instant,
    val endZoneOffset: UtcOffset,
    val stages: List<Stage>,
) {
    data class Stage(
        val startTime: Instant,
        val endTime: Instant,
        val type: SleepSessionStageType,
    )
}
