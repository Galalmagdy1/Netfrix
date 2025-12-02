package com.example.netfrix.data
import com.example.netfrix.data.Genre
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun fromGenreList_convertsListToJsonCorrectly() {
        val genreList = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Comedy")
        )

        val resultJson = converters.fromGenreList(genreList)


        val expectedJson = "[{\"id\":1,\"name\":\"Action\"},{\"id\":2,\"name\":\"Comedy\"}]"
        assertEquals(expectedJson, resultJson)
    }

    @Test
    fun toGenreList_convertsJsonToListCorrectly() {
        val jsonString = "[{\"id\":1,\"name\":\"Action\"},{\"id\":2,\"name\":\"Comedy\"}]"

        val resultList = converters.toGenreList(jsonString)

        assertEquals(2, resultList?.size)
        assertEquals("Action", resultList?.get(0)?.name)
        assertEquals("Comedy", resultList?.get(1)?.name)
    }
}