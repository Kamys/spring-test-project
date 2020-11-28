package ru.my.test.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.my.test.model.AuthorAddRequest
import ru.my.test.model.AuthorEditRequest
import ru.my.test.model.AuthorView
import ru.my.test.service.AuthorService
import javax.validation.Valid

@RestController
@RequestMapping("/authors")
class AuthorController(
    private var authorService: AuthorService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAuthors(): Any {
        return authorService.getAll()
    }

    @GetMapping("/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAuthors(@PathVariable("authorId") authorId: Int): Any {
        return authorService.getById(authorId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAuthor(@Valid @RequestBody request: AuthorAddRequest): AuthorView {
        return authorService.add(request)
    }

    @PutMapping("/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    fun editAuthor(
        @PathVariable("authorId") authorId: Int,
        @RequestBody @Valid request: AuthorEditRequest
    ): AuthorView {
        return authorService.edit(authorId, request)
    }

    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAuthor(
        @PathVariable("authorId") authorId: Int
    ) {
        authorService.delete(authorId)
    }
}