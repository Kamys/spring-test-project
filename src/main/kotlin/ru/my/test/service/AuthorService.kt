package ru.my.test.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Author
import ru.my.test.model.AuthorAddRequest
import ru.my.test.model.AuthorEditRequest
import ru.my.test.model.AuthorView

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository
) {
    fun getAll(): List<AuthorView> {
        return authorRepository.findAll().map { it.toView() }
    }

    fun add(request: AuthorAddRequest): AuthorView {
        val author = Author(name = request.name, dateOfBirth = request.dateOfBirth)
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
        // TODO: Remove emptyList
        return AuthorView(this.id, this.name, this.dateOfBirth, emptyList())
    }
}