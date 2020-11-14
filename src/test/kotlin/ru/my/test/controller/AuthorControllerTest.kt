package ru.my.test.controller

import ApiError
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Author
import ru.my.test.model.AuthorAddRequest
import ru.my.test.model.AuthorEditRequest
import ru.my.test.service.AuthorRepository
import ru.my.test.service.findOrException


class AuthorControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun beforeEach() {
        authorRepository.deleteAll()
    }

    private fun Author.shouldEquals(author: Author) {
        this.id.shouldBe(author.id)
        this.name.shouldBe(author.name)
        this.dateOfBirth.shouldBe(author.dateOfBirth)
        // TODO: shouldBe booksIds
        // this.booksIds.shouldBe(author.booksIds)
    }

    @Test
    fun `return all exist author`() {

        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val andReturn = mvc.get("/authors/")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<List<Author>>()

        result.size.shouldBe(2)
        result.map { it.name }.shouldContainExactly(listOf(authorFirst.name, authorSecond.name))
    }

    @Test
    fun `return author by id`() {
        val author = modelHelper.createAuthor()

        val andReturn = mvc.get("/authors/${author.id}")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Author>()

        result.id.shouldBe(author.id)
        result.name.shouldBe(author.name)
        val allAuthors = authorRepository.findAll()

        allAuthors.size.shouldBe(1)
        allAuthors.first().id.shouldBe(author.id)
        allAuthors.first().name.shouldBe(author.name)
    }

    @Test
    fun `return error "not found" if author not exist`() {
        mvc.get("/authors/1").andExpect(status().isNotFound)
    }

    @Test
    fun `return empty array`() {
        val andReturn = mvc.get("/authors/")
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<List<Author>>()

        result.size.shouldBe(0)
    }

    @Test
    fun `return created author`() {
        val authorTitle = faker.book().author()
        val dateOfBirth = modelHelper.generateBirthday()
        val authorRequest = AuthorAddRequest(authorTitle, dateOfBirth, emptyList())

        val andReturn = mvc.post("/authors/", authorRequest.asJson())
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Author>()

        result.name.shouldBe(authorTitle)
        val allAuthors = authorRepository.findAll()
        allAuthors.size.shouldBe(1)
        allAuthors.first().name.shouldBe(authorTitle)
    }

    @Test
    fun `return validation error`() {
        val andReturn = mvc.post("/authors/", "{}")
            .andExpect(status().isBadRequest)
            .andReturn();

        val result = andReturn.asObject<ApiError>()

        result.title.shouldBe(ApiError.ERROR_MESSAGES_JSON_NOT_VALID)
    }

    @Test
    fun `return error "not found" if author for edit not exist`() {
        val request = AuthorEditRequest("Author new name", modelHelper.generateBirthday(), emptyList())

        mvc.put("/authors/1", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `return edited author`() {
        val editedAuthor = modelHelper.createAuthor("Author old name")
        val authorSecond = modelHelper.createAuthor()

        val request = AuthorEditRequest("Author new name", editedAuthor.dateOfBirth, emptyList())

        val andReturn = mvc.put("/authors/${editedAuthor.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn();

        val result = andReturn.asObject<Author>()

        result.id.shouldBe(editedAuthor.id)
        result.name.shouldBe(request.name)

        authorRepository.findOrException(editedAuthor.id).name.shouldBe(request.name)
        authorRepository.findOrException(authorSecond.id).name.shouldBe(authorSecond.name)
    }

    @Test
    fun `return error "not found" if author for delete not exist`() {
        mvc.delete("/authors/1").andExpect(status().isNotFound)
    }

    @Test
    fun `return status "Ok" if author deleted`() {
        val authorForDelete = modelHelper.createAuthor()
        val author = modelHelper.createAuthor()

        mvc.delete("/authors/${authorForDelete.id}").andExpect(status().isOk)

        val allAuthor = authorRepository.findAll()
        allAuthor.size.shouldBe(1)
        allAuthor.first().id.shouldBe(author.id)
        allAuthor.first().name.shouldBe(author.name)
    }
}