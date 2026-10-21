package pl.flowerbedapp.core.data.local.seed

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.flowerbedapp.core.data.mapper.toEntityOrNull
import java.io.File

class SeedFileTest {

    // Unit tests run with the module directory (app/) as the working directory
    private val seed: SeedFileDto = Moshi.Builder().build()
        .adapter(SeedFileDto::class.java)
        .fromJson(File("src/main/assets/plants_seed.json").readText())!!

    @Test
    fun `every record in the shipped seed file passes validation`() {
        val rejected = seed.plants.filter { it.toEntityOrNull() == null }.map { it.scientificName }
        assertEquals("Invalid seed records: $rejected", emptyList<String>(), rejected)
    }

    @Test
    fun `scientific names are unique`() {
        val duplicates = seed.plants.groupingBy { it.scientificName }.eachCount().filterValues { it > 1 }.keys
        assertEquals("Duplicated species: $duplicates", emptySet<String>(), duplicates)
    }

    @Test
    fun `seed version is positive`() {
        assertTrue(seed.version >= 1)
    }
}
