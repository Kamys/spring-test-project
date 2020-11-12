package ru.my.test.controller

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Book
import ru.my.test.model.BookAddRequest
import ru.my.test.service.BookRepository


class BookControllerTest: AbstractIntegrationTest() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun `return all exist book`() {

        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val andReturn = mvc.get("/books/")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<List<Book>>()

        result.size.shouldBe(2)
        result.map { it.name }.shouldContainExactly(listOf(bookFirst.name, bookSecond.name))
    }

    @Test
    fun `return empty array`() {
        val andReturn = mvc.get("/books/")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<List<Book>>()

        result.size.shouldBe(0)
    }

    @Test
    fun `create book`() {
        val bookTitle = faker.book().title()
        val bookRequest = BookAddRequest(bookTitle)

        val andReturn = mvc.post("/books/", bookRequest.asJson())
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Book>()

        result.name.shouldBe(bookTitle)
        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(1)
        allBooks.first().name.shouldBe(bookTitle)
    }

    @Test
    fun `return validation error`() {
        val andReturn = mvc.post("/books/", "{}")
            .andExpect(status().isBadRequest)
            .andReturn();

        println("response: " + andReturn.response.contentAsString)

        //result.name.shouldBe("")
        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(0)
    }

    @Test
    fun editBook() {
    }

    @Test
    fun deleteBook() {
    }
}