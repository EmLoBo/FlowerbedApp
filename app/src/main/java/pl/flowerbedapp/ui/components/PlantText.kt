package pl.flowerbedapp.ui.components

import pl.flowerbedapp.core.domain.model.Plant
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

/** "6–12", or just "6" when both ends are equal. */
fun IntRange.toRangeText(): String = if (first == last) "$first" else "$first–$last"

fun ClosedFloatingPointRange<Double>.toRangeText(): String =
    if (start == endInclusive) "%.1f".format(start) else "%.1f–%.1f".format(start, endInclusive)

/** Bloom months as short names; English to match the rest of the UI. */
fun List<Int>.toMonthsText(): String =
    joinToString(", ") { Month.of(it).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }

/** One-line growing summary for list cards: "pH 6.5–8.0 · ☀ 6–12 h · Sandy, Loamy". */
fun Plant.summaryLine(): String =
    "pH ${ph.toRangeText()} · ☀ ${sunHours.toRangeText()} h · ${soilTypes.joinToString(", ") { it.label }}"
