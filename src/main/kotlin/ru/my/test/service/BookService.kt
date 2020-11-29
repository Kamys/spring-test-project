package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Book
import ru.my.test.entity.Review
import ru.my.test.model.*

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val reviewRepository: ReviewRepository,
    private val authorService: AuthorService,
) {
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

    fun Review.toView(): ReviewView {
        return ReviewView(this.id, this.text, this.rating)
    }

    fun Book.toView(): BookView {
        return BookView(this.id, this.name, this.authors.map { it.id }, this.reviews.map { it.toView() })
    }
}