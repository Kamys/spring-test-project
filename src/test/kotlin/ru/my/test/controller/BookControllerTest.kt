package ru.my.test.controller

import ApiError
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Book
import ru.my.test.model.BookAddRequest
import ru.my.test.model.BookEditRequest
import ru.my.test.service.BookRepository
import ru.my.test.service.findOrException


class BookControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun beforeEach() {
        bookRepository.deleteAll()
    }

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
    fun `return book by id`() {
        val book = modelHelper.createBook()

        val andReturn = mvc.get("/books/${book.id}")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Book>()

        result.id.shouldBe(book.id)
        result.name.shouldBe(book.name)
        val allBooks = bookRepository.findAll()

        allBooks.size.shouldBe(1)
        allBooks.first().id.shouldBe(book.id)
        allBooks.first().name.shouldBe(book.name)
    }

    @Test
    fun `return error "not found" if book not exist`() {
        mvc.get("/books/1").andExpect(status().isNotFound)
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
    fun `return created book`() {
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

        val result = andReturn.asObject<ApiError>()

        result.title.shouldBe(ApiError.ERROR_MESSAGES_JSON_NOT_VALID)
    }

    @Test
    fun `return error "not found" if book for edit not exist`() {
        mvc.put("/books/1").andExpect(status().isNotFound)
    }

    @Test
    fun `return edited book`() {
        val editedBook = modelHelper.createBook("Book old name")
        val bookSecond = modelHelper.createBook()

        val request = BookEditRequest("Book new name")

        val andReturn = mvc.put("/books/${editedBook.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Book>()

        result.id.shouldBe(editedBook.id)
        result.name.shouldBe(request.name)

        bookRepository.findOrException(editedBook.id).name.shouldBe(request.name)
        bookRepository.findOrException(bookSecond.id).name.shouldBe(bookSecond.name)
    }

    @Test
    fun deleteBook() {
    }
}