package ru.my.test.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.User
import ru.my.test.model.userAddRequest
import ru.my.test.model.userEditRequest
import ru.my.test.model.UserView

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
) {

    fun getAll(): List<UserView> {
        return userRepository.findAll().map { it.toView() }
    }

    fun add(request: userAddRequest): UserView {
        val user = User(
            name = request.name,
        )

        return userRepository.save(user).toView()
    }

    fun edit(userId: Int, request: userEditRequest): UserView {
        val user = userRepository.findOrException(userId)
        user.name = request.name
        return userRepository.save(user).toView()
    }

    fun delete(userId: Int) {
        val user = userRepository.findOrException(userId)
        return userRepository.delete(user)
    }

    fun getById(userId: Int): UserView {
        return this.getModelById(userId).toView()
    }

    fun getModelById(userId: Int): User {
        return userRepository.findOrException(userId)
    }

    fun User.toView(): UserView {
        return UserView(this.id, this.name)
    }
}