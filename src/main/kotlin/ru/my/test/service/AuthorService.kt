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
class AuthorService {
    @Autowired
    private lateinit var authorRepository: AuthorRepository
    @Autowired
    private lateinit var bookService: BookService
    @Autowired
    private lateinit var userService: UserService

    fun getAll(): List<AuthorView> {
        return authorRepository.findAll().map { it.toView() }
    }

    fun add(request: AuthorAddRequest): AuthorView {
        val user = userService.getModelById(request.userId)

        val author = Author(
            descriptionOfWrittenStyle = request.descriptionOfWrittenStyle,
            user = user,
        )

        if (!request.bookIds.isNullOrEmpty()) {
            author.books = bookService.getAllByIds(request.bookIds)
        }

        return authorRepository.save(author).toView()
    }

    fun edit(authorId: Int, request: AuthorEditRequest): AuthorView {
        val author = authorRepository.findOrException(authorId)
        if (!request.descriptionOfWrittenStyle.isNullOrEmpty()) {
            author.descriptionOfWrittenStyle = request.descriptionOfWrittenStyle
        }
        if (!request.bookIds.isNullOrEmpty()) {
            author.books = bookService.getAllByIds(request.bookIds)
        }
        return authorRepository.save(author).toView()
    }

    fun delete(authorId: Int) {
        val author = authorRepository.findOrException(authorId)
        return authorRepository.delete(author)
    }

    fun getById(authorId: Int): AuthorView {
        return authorRepository.findOrException(authorId).toView()
    }

    fun Author.toView(): AuthorView {
        return AuthorView(this.id, this.descriptionOfWrittenStyle, this.books.map { it.id }, this.user)
    }

    fun getAllByIds(authorIds: List<Int>): List<Author> {
        return authorRepository.findAllByIdOrException(authorIds)
    }
}