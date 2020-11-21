package ru.my.test.controller

import ApiError
import ApiValidationError
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.model.BookAddRequest
import ru.my.test.model.BookEditRequest
import ru.my.test.model.BookView
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import ru.my.test.service.findOrException
import javax.transaction.Transactional


class BookControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var bookRepository: BookRepository
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun beforeEach() {
        bookRepository.deleteAll()
        authorRepository.deleteAll()
    }

    @Test
    fun `GET all exist books`() {

        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.map { it.name }.shouldContainExactly(listOf(bookFirst.name, bookSecond.name))
    }

    @Test
    fun `GET books with author ids`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val bookFirst = modelHelper.createBook(authors = listOf(authorFirst, authorSecond))
        val bookSecond = modelHelper.createBook()

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.findOrException { it.id == bookFirst.id }
            .authorIds
            .shouldContainExactly(listOf(authorFirst.id, authorSecond.id))
        response.findOrException { it.id == bookSecond.id }
            .authorIds
            .shouldBeEmpty()
    }

    @Test
    fun `GET books with reviews`() {
        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val reviewFirst = modelHelper.createReview(bookFirst)
        val reviewSecond = modelHelper.createReview(bookFirst)

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.findOrException { it.id == bookFirst.id }
            .reviews
            .map { it.id }
            .shouldContainExactly(listOf(reviewFirst.id, reviewSecond.id))
        response.findOrException { it.id == bookSecond.id }
            .authorIds
            .shouldBeEmpty()
    }

    @Test
    fun `GET book with author ids`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()
        val bookFirst = modelHelper.createBook(authors = listOf(authorFirst, authorSecond))

        val response = mvc.get("/books/${bookFirst.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.authorIds
            .shouldContainExactly(listOf(authorFirst.id, authorSecond.id))
    }

    @Test
    fun `GET book by id`() {
        val book = modelHelper.createBook()

        val response = mvc.get("/books/${book.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(book.id)
        response.name.shouldBe(book.name)

        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(1)
        allBooks.first().id.shouldBe(book.id)
        allBooks.first().name.shouldBe(book.name)
    }

    @Test
    fun `GET book by nonexistent ID should return 404`() {
        mvc.get("/books/1").andExpect(status().isNotFound)
    }

    @Test
    fun `GET empty array if books not exist`() {
        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.shouldBeEmpty()
    }

    @Test
    fun `POST created new book`() {
        val bookTitle = faker.book().title()
        val bookRequest = BookAddRequest(bookTitle)

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.name.shouldBe(bookTitle)
        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(1)
        allBooks.first().name.shouldBe(bookTitle)
    }

    @Test
    fun `POST return 400 if name not unique`() {
        val bookFirst = modelHelper.createBook()

        val request = BookEditRequest(bookFirst.name)

        val response = mvc.post("/books", request.asJson())
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiValidationError>()

        response.violations.first().message.shouldBe("Имя уже используется")
    }

    @Test
    @Transactional
    fun `POST created new book with authors`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val authorIds = listOf(authorFirst.id, authorSecond.id)
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = authorIds
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.authorIds.shouldContainExactly(authorIds)
        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(1)
        allBooks.first().authors.map { it.id }.shouldContainExactly(authorIds)
    }

    @Test
    @Transactional
    fun `POST created new book with empty authors`() {
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = emptyList()
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.authorIds.shouldBeEmpty()
        val allBooks = bookRepository.findAll()
        allBooks.size.shouldBe(1)
        allBooks.first().authors.shouldBeEmpty()
    }

    @Test
    @Transactional
    fun `POST return 404 if authorIds has nonexistent ID`() {
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = listOf(99)
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isNotFound)
            .andReturn()
            .asObject<ApiError>()

        response.detail.shouldBe("Не удалось найти автора с id: 99")
    }

    @Test
    fun `POST return 400 if request not correct`() {
        val response = mvc.post("/books", "{}")
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiError>()

        response.title.shouldBe(ApiError.ERROR_MESSAGES_JSON_NOT_VALID)
    }

    @Test
    fun `PUT book by nonexistent ID should return 404`() {
        val request = BookEditRequest("Book new name")

        mvc.put("/books/99", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `PUT book return edited book`() {
        val editedBook = modelHelper.createBook("Book old name")
        val bookSecond = modelHelper.createBook()

        val request = BookEditRequest("Book new name")

        val response = mvc.put("/books/${editedBook.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(editedBook.id)
        response.name.shouldBe(request.name)

        bookRepository.findOrException(editedBook.id).name.shouldBe(request.name)
        bookRepository.findOrException(bookSecond.id).name.shouldBe(bookSecond.name)
    }

    @Test
    @Transactional
    fun `PUT book edit authorIds`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()
        val authorThird = modelHelper.createAuthor()

        val editedBook = modelHelper.createBook(authors = listOf(authorFirst, authorSecond))
        val bookSecond = modelHelper.createBook()

        val newAuthorIds = listOf(authorThird.id)
        val request = BookEditRequest(authorIds = newAuthorIds)

        val response = mvc.put("/books/${editedBook.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(editedBook.id)
        response.authorIds.shouldContainExactly(newAuthorIds)

        bookRepository.findOrException(editedBook.id).authors
            .map { it.id }
            .shouldContainExactly(newAuthorIds)
        bookRepository.findOrException(bookSecond.id).authors.shouldBeEmpty()
    }

    @Test
    @Transactional
    fun `PUT return 400 if new name not unique`() {
        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val request = BookEditRequest(bookSecond.name)

        val response = mvc.put("/books/${bookFirst.id}", request.asJson())
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiValidationError>()

        response.violations.first().message.shouldBe("Имя уже используется")
    }

    @Test
    fun `DELETE book by nonexistent ID should return 404`() {
        mvc.delete("/books/1").andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE return 200 if book deleted`() {
        val bookForDelete = modelHelper.createBook()
        val book = modelHelper.createBook()

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isOk)

        val allBook = bookRepository.findAll()
        allBook.size.shouldBe(1)
        allBook.first().id.shouldBe(book.id)
        allBook.first().name.shouldBe(book.name)
    }

    @Test
    fun `DELETE book with authors`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val bookForDelete = modelHelper.createBook(authors = listOf(authorFirst, authorSecond))

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isOk)

        val allBook = bookRepository.findAll()
        allBook.shouldBeEmpty()

        val allAuthors = authorRepository.findAll()
        allAuthors.size.shouldBe(2)
    }
}