package ru.my.test.controller

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.model.*
import ru.my.test.service.AuthorRepository
import ru.my.test.service.ContactRepository
import ru.my.test.service.findOrException
import javax.transaction.Transactional


class AuthorControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var authorRepository: AuthorRepository
    @Autowired
    private lateinit var contactRepository: ContactRepository

    @BeforeEach
    fun beforeEach() {
        authorRepository.deleteAll()
    }

    @Test
    fun `GET all exist authors`() {

        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val response = mvc.get("/authors")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<AuthorView>>()

        response.size.shouldBe(2)
        response.map { it.name }.shouldContainExactly(listOf(authorFirst.name, authorSecond.name))
    }

    @Test
    fun `GET author by id`() {
        val author = modelHelper.createAuthor()

        val response = mvc.get("/authors/${author.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<AuthorView>()

        response.id.shouldBe(author.id)
        response.name.shouldBe(author.name)
        val allAuthors = authorRepository.findAll()

        allAuthors.size.shouldBe(1)
        allAuthors.first().id.shouldBe(author.id)
        allAuthors.first().name.shouldBe(author.name)
    }

    @Test
    fun `GET author by nonexistent ID should return 404`() {
        mvc.get("/authors/99").andExpect(status().isNotFound)
    }

    @Test
    fun `GET return empty array if authors not exist`() {
        val response = mvc.get("/authors")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<AuthorView>>()

        response.size.shouldBe(0)
    }

    @Test
    fun `POST created new author`() {
        val authorTitle = faker.book().author()
        val authorRequest = AuthorAddRequest(authorTitle, emptyList())

        val response = mvc.post("/authors", authorRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<AuthorView>()

        response.name.shouldBe(authorTitle)
        val allAuthors = authorRepository.findAll()
        allAuthors.size.shouldBe(1)
        allAuthors.first().name.shouldBe(authorTitle)
    }

    @Test
    @Transactional
    fun `POST created new author with books`() {
        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val bookIds = listOf(bookFirst.id, bookSecond.id)
        val authorRequest = AuthorAddRequest(
            name = "Author name",
            bookIds = bookIds
        )

        val response = mvc.post("/authors", authorRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<AuthorView>()

        response.bookIds.shouldContainExactly(bookIds)

        val allAuthors = authorRepository.findAll()
        allAuthors.size.shouldBe(1)
        allAuthors.first().books.map { it.id }.shouldContainExactly(bookIds)
    }

    @Test
    fun `POST return 404 if bookIds has nonexistent ID`() {
        val authorRequest = AuthorAddRequest(
            name = "Author name",
            bookIds = listOf(99)
        )

        val response = mvc.post("/authors", authorRequest.asJson())
            .andExpect(status().isNotFound)
            .andReturn()
            .asObject<ApiError>()

        response.detail.shouldBe("Не удалось найти книгу с id: 99")
    }

    @Test
    fun `POST return 400 if request not correct`() {
        val response = mvc.post("/authors", "{}")
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiError>()

        response.title.shouldBe(ErrorMessages.JSON_NOT_VALID.text)
    }

    @Test
    fun `PUT author by nonexistent ID should return 404`() {
        val request = AuthorEditRequest("Author new name", emptyList())

        mvc.put("/authors/99", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `PUT edited author`() {
        val editedAuthor = modelHelper.createAuthor("Author old name")
        val authorSecond = modelHelper.createAuthor()

        val request = AuthorEditRequest("Author new name", emptyList())

        val response = mvc.put("/authors/${editedAuthor.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<AuthorView>()

        response.id.shouldBe(editedAuthor.id)
        response.name.shouldBe(request.name)

        authorRepository.findOrException(editedAuthor.id).name.shouldBe(request.name)
        authorRepository.findOrException(authorSecond.id).name.shouldBe(authorSecond.name)
    }

    @Test
    @Transactional
    fun `PUT edited booksIds`() {
        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val editedAuthor = modelHelper.createAuthor(books = listOf(bookFirst))

        val newBookIds = listOf(bookSecond.id)
        val request = AuthorEditRequest(bookIds = newBookIds)

        val response = mvc.put("/authors/${editedAuthor.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<AuthorView>()

        response.id.shouldBe(editedAuthor.id)
        println(request.bookIds)
        println(response.bookIds)
        response.bookIds.shouldBe(newBookIds)

        val repAuthor = authorRepository.findOrException(editedAuthor.id)
        repAuthor.books.map { it.id }.shouldBe(newBookIds)
    }

    @Test
    @Transactional
    fun `PUT return 404 if set nonexistent book ID in booksIds`() {
        val editedAuthor = modelHelper.createAuthor()

        val request = AuthorEditRequest(bookIds = listOf(99))

        val response = mvc.put("/authors/${editedAuthor.id}", request.asJson())
            .andExpect(status().isNotFound)
            .andReturn()
            .asObject<ApiError>()

        response.detail.shouldBe("Не удалось найти книгу с id: 99")
    }

    @Test
    fun `DELETE author by nonexistent ID should return 404`() {
        mvc.delete("/authors/99").andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE return 200 if author deleted`() {
        val authorForDelete = modelHelper.createAuthor()
        val author = modelHelper.createAuthor()

        mvc.delete("/authors/${authorForDelete.id}").andExpect(status().isNoContent)

        val allAuthor = authorRepository.findAll()
        allAuthor.size.shouldBe(1)
        allAuthor.first().id.shouldBe(author.id)
        allAuthor.first().name.shouldBe(author.name)
    }

    @Test
    @Transactional
    fun `DELETE author also delete contact`() {
        val author = modelHelper.createAuthor()
        modelHelper.createContact(author)

        mvc.delete("/authors/${author.id}").andExpect(status().isNoContent)

        authorRepository.count().shouldBe(0)
        contactRepository.count().shouldBe(0)
        contactRepository.findAll()
    }
}