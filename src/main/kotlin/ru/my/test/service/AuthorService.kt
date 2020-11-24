package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Author
import ru.my.test.model.AuthorAddRequest
import ru.my.test.model.AuthorEditRequest
import ru.my.test.model.AuthorView

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository,
) {

    @Autowired
    private lateinit var bookService: BookService
    @Autowired
    private lateinit var contactService: ContactService

    fun getAll(): List<AuthorView> {
        return authorRepository.findAll().map { it.toView() }
    }

    fun add(request: AuthorAddRequest): AuthorView {
        val author = Author(name = request.name)

        if (!request.bookIds.isNullOrEmpty()) {
            author.books = bookService.getAllByIds(request.bookIds)
        }

        return authorRepository.save(author).toView()
    }

    fun edit(authorId: Int, request: AuthorEditRequest): AuthorView {
        val author = authorRepository.findOrException(authorId)
        if (!request.name.isNullOrEmpty()) {
            author.name = request.name
        }
        if (!request.bookIds.isNullOrEmpty()) {
            author.books = bookService.getAllByIds(request.bookIds)
        }
        return authorRepository.save(author).toView()
    }

    fun delete(authorId: Int) {
        val author = authorRepository.findOrException(authorId)
        val contact = author.contact
        if (contact != null) {
            contactService.delete(contact.id)
        }
        return authorRepository.delete(author)
    }

    fun getById(authorId: Int): AuthorView {
        return this.getModelById(authorId).toView()
    }

    fun getModelById(authorId: Int): Author{
        return authorRepository.findOrException(authorId)
    }

    fun Author.toView(): AuthorView {
        return AuthorView(this.id, this.name, this.books.map { it.id })
    }

    fun getAllByIds(authorIds: List<Int>): List<Author> {
        return authorRepository.findAllByIdOrException(authorIds)
    }
}