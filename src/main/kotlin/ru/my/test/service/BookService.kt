package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Book
import ru.my.test.model.BookAddRequest
import ru.my.test.model.BookEditRequest
import ru.my.test.model.BookView
import ru.my.test.service.mapper.toView

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
) {

    @Autowired
    private lateinit var authorService: AuthorService

    fun getAll(): List<BookView> {
        return bookRepository.findAll().map { it.toView() }
    }

    fun add(request: BookAddRequest): BookView {
        val book = Book(name = request.name)

        if (!request.authorIds.isNullOrEmpty()) {
            book.authors = authorService.getAllByIds(request.authorIds)
        }

        return bookRepository.save(book).toView()
    }

    fun edit(bookId: Int, request: BookEditRequest): BookView {
        val book = bookRepository.findOrException(bookId)
        if (!request.name.isNullOrEmpty()) {
            book.name = request.name
        }
        if (!request.authorIds.isNullOrEmpty()) {
            book.authors = authorService.getAllByIds(request.authorIds)
        }
        return bookRepository.save(book).toView()
    }

    fun delete(bookId: Int) {
        val book = bookRepository.findOrException(bookId)
        return bookRepository.delete(book)
    }

    fun getById(bookId: Int): BookView {
        return bookRepository.findOrException(bookId).toView()
    }

    fun getModelById(bookId: Int): Book {
        return bookRepository.findOrException(bookId)
    }

    fun getAllByIds(booksIds: List<Int>): List<Book> {
        return bookRepository.findAllByIdOrException(booksIds)
    }

    fun existsByName(name: String): Boolean {
        return bookRepository.existsByName(name)
    }
}