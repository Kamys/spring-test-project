package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Contact
import ru.my.test.model.ContactAddRequest
import ru.my.test.model.ContactEditRequest
import ru.my.test.model.ContactView

@Service
@Transactional
class ContactService(
    private val contactRepository: ContactRepository,
) {

    @Autowired
    private lateinit var authorService: AuthorService

    fun getAll(): List<ContactView> {
        return contactRepository.findAll().map { it.toView() }
    }

    fun add(request: ContactAddRequest): ContactView {
        val author = authorService.getModelById(request.authorId)

        val contact = Contact(author = author, email = "", phone = "")
        author.contact = contact

        if (!request.email.isNullOrEmpty()) {
            contact.email = request.email
        }
        if (!request.phone.isNullOrEmpty()) {
            contact.phone = request.phone
        }

        return contactRepository.save(contact).toView()
    }

    fun edit(contactId: Int, request: ContactEditRequest): ContactView {
        val contact = contactRepository.findOrException(contactId)

        if (!request.email.isNullOrEmpty()) {
            contact.email = request.email
        }
        if (!request.phone.isNullOrEmpty()) {
            contact.phone = request.phone
        }

        return contactRepository.save(contact).toView()
    }

    fun delete(contactId: Int) {
        val contact = contactRepository.findOrException(contactId)
        val author = authorService.findModelByContact(contact)
        author.contact = null
        contactRepository.delete(contact)
    }

    fun getById(contactId: Int): ContactView {
        return contactRepository.findOrException(contactId).toView()
    }

    fun Contact.toView(): ContactView {
        return ContactView(this.id, this.phone, this.email, this.author.id)
    }
}