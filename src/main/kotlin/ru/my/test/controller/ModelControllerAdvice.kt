package ru.my.test.controller

import ApiError
import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ModelControllerAdvice {
    @ExceptionHandler(value = [NotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundException(exception: NotFoundException): ApiError {
        return ApiError(
                type = ErrorType.PROCESS_ERROR,
                status = HttpStatus.NOT_FOUND.value(),
                title = "Ресурс не найден",
                detail = exception.message
        )
    }
}
