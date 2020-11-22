package ru.my.test.controller

import org.springframework.web.bind.annotation.*
import ru.my.test.model.userAddRequest
import ru.my.test.model.userEditRequest
import ru.my.test.model.UserView
import ru.my.test.service.UserService
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getUsers(): Any {
        return userService.getAll()
    }

    @GetMapping("/{userId}")
    fun getUsers(@PathVariable("userId") userId: Int): Any {
        return userService.getById(userId)
    }

    @PostMapping
    fun createUser(@Valid @RequestBody request: userAddRequest): UserView {
        return userService.add(request)
    }

    @PutMapping("/{userId}")
    fun editUser(
        @PathVariable("userId") userId: Int,
        @RequestBody @Valid request: userEditRequest
    ): UserView {
        return userService.edit(userId, request)
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(
        @PathVariable("userId") userId: Int
    ) {
        userService.delete(userId)
    }
}