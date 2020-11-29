package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import ru.my.test.entity.Book
import ru.my.test.entity.Review
import ru.my.test.model.*
import ru.my.test.service.mapper.toView
import java.net.http.HttpHeaders

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val reviewRepository: ReviewRepository,
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

    fun edit(bookId: Long, request: BookEditRequest): BookView {
        val book = bookRepository.findOrException(bookId).apply {
            name = request.name
            authors = authorService.getAllByIds(request.authorIds)
        }
        return bookRepository.save(book).toView()
    }

    fun delete(bookId: Long) {
        val book = bookRepository.findOrException(bookId)
        return bookRepository.delete(book)
    }

    fun getById(bookId: Long): BookView {
        return bookRepository.findOrException(bookId).toView()
    }

    fun getModelById(bookId: Long): Book {
        return bookRepository.findOrException(bookId)
    }

    fun getAllByIds(booksIds: List<Long>): List<Book> {
        return bookRepository.findAllByIdOrException(booksIds)
    }

    fun existsByName(name: String): Boolean {
        return bookRepository.existsByName(name)
    }

    fun getReviews(bookId: Long): List<ReviewView> {
        return reviewRepository.findByBookId(bookId).map { it.toView() }
    }

    fun addReview(bookId: Long, request: ReviewAddRequest): ReviewView {
        val book = bookRepository.findOrException(bookId)
        val review = Review(text = request.text, rating = request.rating)
        review.book = book
        reviewRepository.save(review)
        return review.toView()
    }

    fun deleteReview(bookId: Long, reviewId: Long) {
        val book = bookRepository.findOrException(bookId)
        book.reviews.removeIf { it.id == reviewId }
        bookRepository.save(book)
    }

    fun editReview(bookId: Long, reviewId: Long, request: ReviewEditRequest): ReviewView {
        bookRepository.findOrException(bookId)
        val review = reviewRepository.findOrException(reviewId)
        if  (review.book.id != bookId) {
            throw BadRequest("У книги с id $bookId нет отзыва с id: $reviewId")
        }
        review.text = request.text
        review.rating = request.rating
        return review.toView()
    }
}