package ru.my.test.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Author
import ru.my.test.entity.Book
import ru.my.test.model.AuthorAddRequest
import ru.my.test.model.AuthorEditRequest
import ru.my.test.model.AuthorView
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {
    fun getAll(): List<AuthorView> {
        return authorRepository.findAll().map { it.toView() }
    }

    fun add(request: AuthorAddRequest): AuthorView {
        val author = Author(name = request.name)
        val book = Book(name = "Test book")
        bookRepository.save(book)
        author.books = listOf(book)
        return authorRepository.save(author).toView()
    }

    fun edit(authorId: Int, request: AuthorEditRequest): AuthorView {
        val author =  authorRepository.findOrException(authorId)
        author.apply {
            this.name = request.name
        }
        return authorRepository.save(author).toView()
    }
    fun delete(authorId: Int) {
        val author =  authorRepository.findOrException(authorId)
        return authorRepository.delete(author)
    }

    fun getById(authorId: Int): AuthorView {
        return authorRepository.findOrException(authorId).toView()
    }

    fun Author.toView(): AuthorView {
        return AuthorView(this.id, this.name)
    }
}