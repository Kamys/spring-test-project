package ru.my.test.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Book
import ru.my.test.model.BookAddRequest
import ru.my.test.model.BookEditRequest
import ru.my.test.model.BookView

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository
) {
    fun getAll(): List<BookView> {
        return bookRepository.findAll().map { it.toView() }
    }

    fun add(request: BookAddRequest): BookView {
        val book = Book(name = request.name)
        return bookRepository.save(book).toView()
    }

    fun edit(bookId: Int, request: BookEditRequest): BookView {
        val book =  bookRepository.findOrException(bookId)
        book.apply {
            this.name = request.name
        }
        return bookRepository.save(book).toView()
    }
    fun delete(bookId: Int) {
        val book =  bookRepository.findOrException(bookId)
        return bookRepository.delete(book)
    }

    fun getById(bookId: Int): BookView {
        return bookRepository.findOrException(bookId).toView()
    }

    fun Book.toView(): BookView {
        return BookView(this.id, this.name)
    }
}