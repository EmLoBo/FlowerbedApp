package pl.flowerbedapp.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchKeyTest {

    @Test
    fun `Polish letters are lowercased and stripped of diacritics`() {
        assertEquals("zazolc gesla jazn", searchKeyOf("Zażółć gęślą jaźń"))
    }

    @Test
    fun `l with stroke becomes plain l`() {
        assertEquals("lubin trwaly", searchKeyOf("Łubin trwały"))
    }

    @Test
    fun `parts are joined with spaces and nulls are skipped`() {
        assertEquals("lawenda lavandula angustifolia", searchKeyOf("Lawenda", "Lavandula angustifolia", null))
    }

    @Test
    fun `typed query matches the stored key regardless of case and accents`() {
        val stored = searchKeyOf("Śliwa wiśniowa", "Prunus cerasifera", "Cherry plum")
        assertTrue(stored.contains(searchKeyOf("ŚLIWA")))
        assertTrue(stored.contains(searchKeyOf("wisniowa")))
    }
}
