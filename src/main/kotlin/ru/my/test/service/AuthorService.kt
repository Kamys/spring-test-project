package ru.my.test.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Author
import ru.my.test.entity.Contact
import ru.my.test.model.*
import java.util.*
import javax.servlet.http.HttpServletResponse

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val contactRepository: ContactRepository,
) {

    fun getAll(): List<AuthorView> {
        return authorRepository.findAll().map { it.toView() }
    }

    fun add(request: AuthorAddRequest): AuthorView {
        val author = Author(name = request.name)
        return authorRepository.save(author).toView()
    }

    fun edit(authorId: Long, request: AuthorEditRequest): AuthorView {
        val author = authorRepository.findOrException(authorId).apply {
            name = request.name
        }
        return authorRepository.save(author).toView()
    }

    fun delete(authorId: Long) {
        val author = authorRepository.findOrException(authorId)
        author.contact?.run {
            author.contact = null
            contactRepository.delete(this)
        }
        return authorRepository.delete(author)
    }

    fun getById(authorId: Long): AuthorView {
        return this.getModelById(authorId).toView()
    }

    fun getModelById(authorId: Long): Author {
        return authorRepository.findOrException(authorId)
    }

    fun Author.toView(): AuthorView {
        return AuthorView(this.id, this.name, this.books.map { it.id })
    }

    fun getAllByIds(authorIds: List<Long>): List<Author> {
        return authorRepository.findAllByIdOrException(authorIds)
    }

    fun editContact(authorId: Long, request: ContactEditRequest, response: HttpServletResponse): ContactView {
        val currentAuthor = authorRepository.findOrException(authorId)
        val contact: Contact = Optional.ofNullable(currentAuthor.contact)
            .map {
                response.status = HttpStatus.OK.value()
                it.apply {
                    phone = request.phone
                    email = request.email
                }
            }
            .orElseGet {
                response.status = HttpStatus.CREATED.value()
                Contact(
                    phone = request.phone,
                    email = request.email,
                ).apply { author = currentAuthor }
            }

        return contactRepository.save(contact).toView()
    }


    fun Contact.toView(): ContactView {
        return ContactView(this.id, this.phone, this.email)
    }
}