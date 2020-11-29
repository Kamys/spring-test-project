package ru.my.test.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.my.test.model.*
import ru.my.test.service.AuthorService
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAuthors(): List<AuthorView> {
        return authorService.getAll()
    }

    @GetMapping("/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAuthors(@PathVariable("authorId") authorId: Long): AuthorView {
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
        @PathVariable("authorId") authorId: Long,
        @RequestBody @Valid request: AuthorEditRequest
    ): AuthorView {
        return authorService.edit(authorId, request)
    }

    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAuthor(
        @PathVariable("authorId") authorId: Long
    ) {
        authorService.delete(authorId)
    }

    @PutMapping("/{authorId}/contacts")
    fun editContact(
        @PathVariable("authorId") authorId: Long,
        @RequestBody @Valid request: ContactEditRequest,
        response: HttpServletResponse
    ): ContactView {
        return authorService.editContact(authorId, request, response)
    }
}