package ru.my.test.controller

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Contact
import ru.my.test.model.ContactEditRequest
import ru.my.test.model.ContactView
import ru.my.test.service.AuthorRepository
import ru.my.test.service.ContactRepository


class ContactControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var contactRepository: ContactRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun beforeEach() {
        contactRepository.deleteAll()
        authorRepository.deleteAll()
    }

    @Test
    fun `PUT create contact`() {
        val author = modelHelper.createAuthor()

        val request = ContactEditRequest(
            phone = "New phone number",
            email = "New email"
        )

        val response = mvc.put("/authors/${author.id}/contacts", request.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<ContactView>()

        response.email.shouldBe(request.email)
        response.phone.shouldBe(request.phone)

        transactional {
            contactRepository.findOrException(response.id).let {
                it.email.shouldBe(request.email)
                it.phone.shouldBe(request.phone)
            }
        }
    }

    @Test
    fun `PUT edited contact`() {
        val author = transactional {
            val author = modelHelper.createAuthor()
            author.contact = Contact(phone = "Old phone number", email = "Old email")
            authorRepository.save(author)
        }

        val request = ContactEditRequest(
            phone = "New phone number",
            email = "New email"
        )

        val response = mvc.put("/authors/${author.id}/contacts", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ContactView>()

        response.email.shouldBe(request.email)
        response.phone.shouldBe(request.phone)

        transactional {
            contactRepository.findOrException(response.id).let {
                it.email.shouldBe(request.email)
                it.phone.shouldBe(request.phone)
            }
        }
    }
}