package ru.my.test.controller

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Author
import ru.my.test.entity.Contact
import ru.my.test.model.*
import ru.my.test.service.AuthorRepository
import ru.my.test.service.ContactRepository
import ru.my.test.service.findOrException


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

    fun ContactView.shouldBe(contact: Contact) {
        this.email.shouldBe(contact.email)
        this.phone.shouldBe(contact.phone)
        this.authorId.shouldBe(contact.author.id)
    }

    fun List<ContactView>.shouldContainExactly(contact: Contact) {
        this.findOrException { it.id == contact.id }.shouldBe(contact)
    }

    @Test
    fun `GET all exist contacts`() {
        val contactFirst = modelHelper.createContact()
        val contactSecond = modelHelper.createContact()

        val response = mvc.get("/contacts")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ContactView>>()

        response.size.shouldBe(2)
        response.shouldContainExactly(contactFirst)
        response.shouldContainExactly(contactSecond)
    }

    @Test
    fun `GET contact by id`() {
        val contact = modelHelper.createContact()

        val response = mvc.get("/contacts/${contact.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ContactView>()

        response.shouldBe(contact)

        contactRepository.count().shouldBe(1)
        contactRepository.findOrException(contact.id).let {
            it.email.shouldBe(contact.email)
            it.phone.shouldBe(contact.phone)
        }
    }

    @Test
    fun `GET contact by nonexistent ID should return 404`() {
        mvc.get("/contacts/99").andExpect(status().isNotFound)
    }

    @Test
    fun `GET return empty array if contacts not exist`() {
        val response = mvc.get("/contacts")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ContactView>>()

        response.size.shouldBe(0)
    }

    @Test
    fun `POST created new contact`() {
        val author = modelHelper.createAuthor()
        val request = ContactAddRequest(
            phone = faker.phoneNumber().phoneNumber(),
            email = faker.internet().emailAddress(),
            authorId = author.id,
        )

        val response = mvc.post("/contacts", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ContactView>()

        response.email.shouldBe(request.email)
        response.phone.shouldBe(request.phone)

        contactRepository.count().shouldBe(1)
        contactRepository.findOrException(response.id).let {
            it.email.shouldBe(request.email)
            it.phone.shouldBe(request.phone)
        }
        authorRepository.findOrException(author.id).let {
            it.contact.shouldNotBeNull()
            it.contact?.id.shouldBe(response.id)
        }
    }

    @Test
    fun `POST return 400 if request not correct`() {
        val response = mvc.post("/contacts", "{}")
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiError>()

        response.title.shouldBe(ErrorMessages.FAILED_VALIDATION.text)
    }

    @Test
    fun `PUT contact by nonexistent ID should return 404`() {
        val request = ContactEditRequest()

        mvc.put("/contacts/99", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `PUT edited contact`() {
        val editedContact = modelHelper.createContact(phone = "Old phone number", email = "Old email")
        val contactSecond = modelHelper.createContact()

        val request = ContactEditRequest(
            phone = "New phone number",
            email = "New email"
        )

        val response = mvc.put("/contacts/${editedContact.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ContactView>()

        response.email.shouldBe(request.email)
        response.phone.shouldBe(request.phone)

        contactRepository.findOrException(editedContact.id).let {
            it.email.shouldBe(request.email)
            it.phone.shouldBe(request.phone)
        }
        contactRepository.findOrException(contactSecond.id).let {
            it.email.shouldBe(contactSecond.email)
            it.phone.shouldBe(contactSecond.phone)
        }
    }

    @Test
    fun `DELETE contact by nonexistent ID should return 404`() {
        mvc.delete("/contacts/99").andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE return 200 if contact deleted`() {
        val contactForDelete = modelHelper.createContact()
        val contact = modelHelper.createContact()

        mvc.delete("/contacts/${contactForDelete.id}").andExpect(status().isOk)

        contactRepository.count().shouldBe(1)
        contactRepository.findOrException(contact.id).let {
            it.email.shouldBe(contact.email)
            it.phone.shouldBe(contact.phone)
            it.author.id.shouldBe(contact.author.id)
        }
    }

    @Test
    fun `DELETE contact not deleted Author`() {
        val author = Author(name = "Author")
        val contact = Contact(phone = "Phone number", email = "Email", author = author)
        author.contact = contact
        authorRepository.save(author)

        //mvc.delete("/contacts/${contact.id}").andExpect(status().isOk)
        contactRepository.delete(contact)

        contactRepository.count().shouldBe(0)
        authorRepository.count().shouldBe(1)
        authorRepository.findOrException(author.id).let {
            it.contact.shouldBeNull()
            it.name.shouldBe(author.name)
        }
    }
}