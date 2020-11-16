package ru.my.test.controller

import org.springframework.web.bind.annotation.*
import ru.my.test.model.BookAddRequest
import ru.my.test.model.BookEditRequest
import ru.my.test.model.BookView
import ru.my.test.service.BookService
import javax.validation.Valid

@RestController
@RequestMapping("/books")
class BookController(
    private var bookService: BookService
) {

    @GetMapping
    fun getBooks(): Any {
        return bookService.getAll()
    }

    @GetMapping("/{bookId}")
    fun getBooks(@PathVariable("bookId") bookId: Int): Any {
        return bookService.getById(bookId)
    }

    @PostMapping
    fun createBook(@Valid @RequestBody request: BookAddRequest): BookView {
        return bookService.add(request)
    }

    @PutMapping("/{bookId}")
    fun editBook(
        @PathVariable("bookId") bookId: Int,
        @RequestBody @Valid request: BookEditRequest
    ): BookView {
        return bookService.edit(bookId, request)
    }

    @DeleteMapping("/{bookId}")
    fun deleteBook(
        @PathVariable("bookId") bookId: Int
    ) {
        bookService.delete(bookId)
    }
}