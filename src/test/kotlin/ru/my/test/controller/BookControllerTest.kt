package ru.my.test.controller

import com.google.gson.Gson
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Book


class BookControllerTest: AbstractIntegrationTest() {
    @Test
    fun `return all exist book`() {

        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val andReturn = mvc.perform(get("/books/"))
            .andExpect(status().isOk)
            .andReturn();

        val result = Gson().fromJson(andReturn.response.contentAsString, Array<Book>::class.java)

        result.size.shouldBe(2)
        result.map { it.name }.shouldContainExactly(listOf(bookFirst.name, bookSecond.name))
    }

    @Test
    fun `return empty array`() {
        val andReturn = mvc.perform(get("/books/"))
            .andExpect(status().isOk)
            .andReturn();

        val result = Gson().fromJson(andReturn.response.contentAsString, Array<Book>::class.java)

        result.size.shouldBe(0)
    }

    @Test
    fun `create book`() {
        /*val bookRequest = BookAddRequest(faker.book().title())

        val andReturn = mvc.perform(post("/books/", bookRequest))
            .andExpect(status().isOk)
            .andReturn();

        val result = Gson().fromJson(andReturn.response.contentAsString, Array<Book>::class.java)
        assertNotNull(result)*/
    }

    @Test
    fun editBook() {
    }

    @Test
    fun deleteBook() {
    }
}