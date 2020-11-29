package ru.my.test.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.my.test.entity.Review
import ru.my.test.model.*
import ru.my.test.service.BookService
import javax.validation.Valid

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getBooks(): List<BookView> {
        return bookService.getAll()
    }

    @GetMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    fun getBooks(@PathVariable("bookId") bookId: Long): BookView {
        return bookService.getById(bookId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@Valid @RequestBody request: BookAddRequest): BookView {
        return bookService.add(request)
    }

    @PutMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    fun editBook(
        @PathVariable("bookId") bookId: Long,
        @RequestBody @Valid request: BookEditRequest
    ): BookView {
        return bookService.edit(bookId, request)
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(
        @PathVariable("bookId") bookId: Long
    ) {
        bookService.delete(bookId)
    }

    @GetMapping("/{bookId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    fun getReviews(@PathVariable("bookId") bookId: Long): List<ReviewView> {
        return bookService.getReviews(bookId)
    }

    @PostMapping("/{bookId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    fun creteReview(@PathVariable("bookId") bookId: Long, @Valid @RequestBody request: ReviewAddRequest): ReviewView {
        return bookService.addReview(bookId, request)
    }

    @PutMapping("/{bookId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    fun editReview(
        @PathVariable("bookId") bookId: Long,
        @PathVariable("reviewId") reviewId: Long,
        @Valid @RequestBody request: ReviewEditRequest
    ): ReviewView {
        return bookService.editReview(bookId, reviewId, request)
    }

    @DeleteMapping("/{bookId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReview(
        @PathVariable("bookId") bookId: Long,
        @PathVariable("reviewId") reviewId: Long
    ) {
        return bookService.deleteReview(bookId, reviewId)
    }
}