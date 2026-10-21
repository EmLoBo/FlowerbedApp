package pl.flowerbedapp.core.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class PlantConvertersTest {

    private val converters = PlantConverters()

    @Test
    fun `string list is stored wrapped in commas`() {
        assertEquals(",SANDY,LOAMY,", converters.fromStringList(listOf("SANDY", "LOAMY")))
    }

    @Test
    fun `string list survives a round trip`() {
        val soils = listOf("SANDY", "CLAY", "CHALKY")
        assertEquals(soils, converters.toStringList(converters.fromStringList(soils)))
    }

    @Test
    fun `int list survives a round trip`() {
        val months = listOf(6, 7, 8)
        assertEquals(months, converters.toIntList(converters.fromIntList(months)))
    }

    @Test
    fun `empty list is stored as empty text and read back as empty list`() {
        assertEquals("", converters.fromIntList(emptyList()))
        assertEquals(emptyList<Int>(), converters.toIntList(""))
    }
}
