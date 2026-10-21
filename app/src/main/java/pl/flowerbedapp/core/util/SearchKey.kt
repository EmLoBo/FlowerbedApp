package pl.flowerbedapp.core.util

import java.text.Normalizer

private val COMBINING_MARKS = Regex("\\p{Mn}+")

/**
 * Lowercase, accent-free text for searching and sorting plants: "Łubin trwały" → "lubin trwaly".
 * Apply it both to stored names and to whatever the user types, or they won't match.
 */
fun searchKeyOf(vararg parts: String?): String =
    Normalizer.normalize(parts.filterNotNull().joinToString(" ").lowercase(), Normalizer.Form.NFD)
        .replace(COMBINING_MARKS, "")
        .replace('ł', 'l') // ł has no decomposed form, so NFD leaves it as is
        .trim()
